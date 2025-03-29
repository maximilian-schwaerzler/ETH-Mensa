/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager.MenuLanguage
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.mapJsonObjectToOffers
import com.github.maximilianschwaerzler.ethuzhmensa.network.isConnected
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val menuService: CookpitMenuService,
    private val menuDao: MenuDao,
    private val dataStoreManager: DataStoreManager,
    private val connMgr: ConnectivityManager,
    @ApplicationContext private val appContext: Context
) {

    private suspend fun fetchOfferForFacility(
        facilityId: Int,
        date: LocalDate = LocalDate.now(),
        language: MenuLanguage = MenuLanguage.GERMAN
    ): Response<JsonObject> {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        return menuService.fetchMenus(
            facilityId,
            dateFormatter.format(startOfWeek),
            language.langCode,
            dateFormatter.format(endOfWeek)
        )
    }

    private suspend fun MenuDao.offerExists(facilityId: Int, date: LocalDate): Boolean {
        return runCatching { getOfferForFacilityDate(facilityId, date) }.isSuccess
    }

    @Throws(IllegalStateException::class)
    private suspend fun saveAllMenusToDB(
        date: LocalDate = LocalDate.now(),
        force: Boolean = false
    ) {
        if (!force && !connMgr.isConnected()) {
            Log.w("MenuRepository", "No internet connection, skipping menu update")
            throw IllegalStateException("No internet connection")
        }

        val validFacilityIds =
            appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)

        supervisorScope {
            for (facilityId in validFacilityIds) {
                launch {
                    val apiResponse = fetchOfferForFacility(
                        facilityId,
                        date,
                        dataStoreManager.menuLanguage.first()
                    )
                    if (!apiResponse.isSuccessful) return@launch

                    val offers = mapJsonObjectToOffers(apiResponse.body()!!) ?: return@launch

                    for (offer in offers) {
                        if (offer.menuOptions.isEmpty()) continue
                        if (menuDao.offerExists(facilityId, offer.date!!)) {
                            Log.d(
                                "MenuRepository",
                                "Offer for facility $facilityId already exists, skipping"
                            )
                            continue
                        }

                        val dbOffer = Offer(
                            id = 0, facilityId = offer.facilityId!!, date = offer.date
                        )
                        val dbOfferId = menuDao.insertOffer(dbOffer)

                        for (menuOptionDto in offer.menuOptions) {
                            val menu = Offer.Menu(
                                id = 0,
                                offerId = dbOfferId,
                                name = menuOptionDto.name!!,
                                mealName = menuOptionDto.mealName!!,
                                mealDescription = menuOptionDto.mealDescription!!,
                                imageUrl = menuOptionDto.imageUrl
                            )
                            val menuId = menuDao.insertMenu(menu)

                            for (menuPriceDto in menuOptionDto.pricing) {
                                val price = Offer.Menu.MenuPrice(
                                    id = 0,
                                    menuId = menuId,
                                    price = menuPriceDto.price!!,
                                    customerGroupId = menuPriceDto.customerGroupId!!,
                                    customerGroupDesc = menuPriceDto.customerGroupDesc!!,
                                    customerGroupDescShort = menuPriceDto.customerGroupDescShort!!
                                )
                                menuDao.insertPrice(price)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun tryUpdateMenusIfNecessary(date: LocalDate?): Boolean {
        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
        val currentWeek = date?.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
            ?: LocalDate.MIN.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

        return if (lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != currentWeek) {
            runCatching {
                saveAllMenusToDB()
                dataStoreManager.updateLastMenuFetchDate()
            }.onFailure {
                Log.w("MenuRepository", "Failed to fetch menus, skipping update", it)
            }.isSuccess
        } else false
    }

    private suspend fun getOffer(facilityId: Int, date: LocalDate?): OfferWithPrices {
        val targetDate = date ?: LocalDate.now()
        return runCatching {
            menuDao.getOfferForFacilityDate(facilityId, targetDate)
        }.getOrElse {
            Log.w(
                "MenuRepository",
                "No offer found for facility $facilityId, updating from API",
                it
            )
            tryUpdateMenusIfNecessary(date)
            menuDao.getOfferForFacilityDate(facilityId, targetDate)
        }
    }

    suspend fun getOffersForDate(date: LocalDate): List<OfferWithPrices> {
        var offers = menuDao.getAllOffersForDate(date)

        if (tryUpdateMenusIfNecessary(date)) {
            saveAllMenusToDB()
            dataStoreManager.updateLastMenuFetchDate()
            offers = menuDao.getAllOffersForDate(date)
        }

        return offers
    }

    suspend fun getOfferForFacilityDate(
        facilityId: Int, date: LocalDate = LocalDate.now()
    ): OfferWithPrices {
        return getOffer(facilityId, date)
    }

    /**
     * Rebuilds the database by deleting all existing offers and fetching new ones from the API. For the language change
     */
    suspend fun rebuildDatabase() {
        menuDao.deleteAllOffers()
        saveAllMenusToDB(LocalDate.now(), true)
    }
}
