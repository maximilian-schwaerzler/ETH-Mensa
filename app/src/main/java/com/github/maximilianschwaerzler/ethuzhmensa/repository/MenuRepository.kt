package com.github.maximilianschwaerzler.ethuzhmensa.repository

import android.content.Context
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.mapJsonObjectToOffers
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
    @ApplicationContext private val appContext: Context
) {
    private suspend fun fetchOfferForFacility(
        facilityId: Int, date: LocalDate = LocalDate.now(), language: Language = Language.GERMAN
    ): Response<JsonObject> {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        return menuService.fetchMenus(
            facilityId,
            dateFormatter.format(startOfWeek),
            language.lang,
            dateFormatter.format(endOfWeek)
        )
    }

    private suspend fun saveAllMenusToDB(date: LocalDate = LocalDate.now()) {
        val validFacilityIds =
            appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        supervisorScope {
            for (facilityId in validFacilityIds) {
                launch {
                    val apiResponse = fetchOfferForFacility(facilityId)
                    if (!apiResponse.isSuccessful) {
                        return@launch
                    }
                    val apiResponseBody = apiResponse.body()!!
                    val offers = mapJsonObjectToOffers(apiResponseBody)
                    if (offers == null) {
                        return@launch
                    }

                    for (offer in offers) {
                        if (offer.menuOptions.isEmpty()) {
                            return@launch
                        }
                        try {
                            menuDao.getOfferForFacilityDate(facilityId, offer.date!!)
                            Log.d("MenuRepository2", "Offer for facility $facilityId already exists, skipping")
                            continue
                        } catch (_: IllegalStateException) {
                        }

                        val dbOffer = Offer(
                            id = 0, facilityId = offer.facilityId!!, date = offer.date!!
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

    private suspend fun getOffer(facilityId: Int, date: LocalDate?): OfferWithPrices {
        return try {
            date?.let { menuDao.getOfferForFacilityDate(facilityId, it) }
                ?: menuDao.getOfferForFacilityDate(facilityId, LocalDate.now())
        } catch (e: IllegalStateException) {
            Log.w(
                "MenuRepository2", "No offer found for facility $facilityId, updating from API", e
            )
            val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
            if (lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != (date?.get(
                    IsoFields.WEEK_OF_WEEK_BASED_YEAR
                ) ?: LocalDate.MIN)
            ) {
                saveAllMenusToDB()
                dataStoreManager.updateLastMenuFetchDate()
            }
            try {
                menuDao.getOfferForFacilityDate(facilityId, date ?: LocalDate.now())
            } catch (e: IllegalStateException) {
                Log.e(
                    "MenuRepository2", "Failed to fetch offer for facility $facilityId", e
                )
                throw e
            }
        }
    }

    suspend fun getOffersForDate(date: LocalDate): List<OfferWithPrices> {
        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
        var offers = menuDao.getAllOffersForDate(date)
        if (
            offers.isEmpty() && lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != date.get(
                IsoFields.WEEK_OF_WEEK_BASED_YEAR
            )
//            true
        ) {
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

    enum class Language(val lang: String) {
        GERMAN("de"), ENGLISH("en")
    }
}