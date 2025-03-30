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
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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

    /**
     * Fetches all menus from the API and saves them to the database.
     *
     * @param date The date for which to fetch the menus. Defaults to today.
     * @param overrideLanguage If not null, forces the use of this language for the menu update.
     */
    private suspend fun saveAllMenusToDB(
        date: LocalDate = LocalDate.now(),
        overrideLanguage: MenuLanguage? = null
    ) {

        val validFacilityIds =
            appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        supervisorScope {
            for (facilityId in validFacilityIds) {
                async {
                    val apiResponse = fetchOfferForFacility(
                        facilityId,
                        date,
                        overrideLanguage ?: dataStoreManager.menuLanguage.first()
                    )

                    if (!apiResponse.isSuccessful) throw IllegalStateException(
                        "Failed to fetch menus for facility $facilityId: ${apiResponse.code()} ${apiResponse.message()}"
                    )

                    val offers = mapJsonObjectToOffers(
                        apiResponse.body()!!,
                        appContext.getString(R.string.cookpit_client_id)
                    ) ?: return@async

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

    /**
     * Tries to update the menus for the given date. If the date is null, it will use the current date.
     * If force is true, it will always update the menus, even if they are already up to date.
     *
     * @param date The date for which to update the menus.
     * @param force If true, forces a menu update even if the last update was within the same week.
     * @param language If not null, forces the use of this language for the menu update.
     * @return A [Result] containing the updated date of the last menu fetch or an error if the update failed.
     */
    private suspend fun tryUpdatingMenus(
        date: LocalDate,
        force: Boolean = false,
        language: MenuLanguage? = null
    ): Result<LocalDate> {

        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
        val currentWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

        if (force || lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != currentWeek) {
            runCatching {
                saveAllMenusToDB(overrideLanguage = language)
                dataStoreManager.updateLastMenuFetchDate()
            }.onFailure { exception ->
                Log.w("MenuRepository", "Failed to update the menu DB")
                return Result.failure(exception)
            }
        }
        return Result.success(dataStoreManager.lastMenuFetchDate.first())
    }

    suspend fun getOffer(facilityId: Int, date: LocalDate?): OfferWithPrices? {
        val targetDate = date ?: LocalDate.now()
        return runCatching {
            menuDao.getOfferForFacilityDate(facilityId, targetDate)
        }.getOrElse {
            Log.w(
                "MenuRepository",
                "No offer found for facility $facilityId, updating from API",
                it
            )
            val result = tryUpdatingMenus(targetDate)
            if (result.isSuccess) {
                menuDao.getOfferForFacilityDate(facilityId, targetDate)
            } else {
                null
            }
        }
    }

    suspend fun getOffersForDate(date: LocalDate): List<OfferWithPrices> {
        tryUpdatingMenus(date)
        return menuDao.getAllOffersForDate(date)
    }

    /**
     * Rebuilds the database by deleting all existing offers and fetching new ones from the API. Fails with an [IllegalStateException] if the underlying network request fails.
     */
    suspend fun tryRebuildDatabase(language: MenuLanguage): Result<Unit> {
        menuDao.deleteAllOffers()
        tryUpdatingMenus(LocalDate.now(), force = true, language = language)
            .onFailure { exception ->
                Log.w("MenuRepository", "Failed to rebuild the menu DB")
                dataStoreManager.updateLastMenuFetchDate(LocalDate.MIN)
                return Result.failure(exception)
            }
        return Result.success(Unit)
    }
}
