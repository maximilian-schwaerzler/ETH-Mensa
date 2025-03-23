package com.github.maximilianschwaerzler.ethuzhmensa.data2

import android.content.Context
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
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

class MenuRepository2 @Inject constructor(
    private val menuService: CookpitMenuService,
    private val menuDao: MenuDao,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val appContext: Context
) {
    private suspend fun fetchOfferForFacility(
        facilityId: Int,
        date: LocalDate = LocalDate.now(),
        language: Language = Language.GERMAN
    ): Response<JsonObject> {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        return menuService.fetchMenus(
            facilityId,
            dateFormatter.format(startOfWeek)
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
//                        Log.e(
//                            "MenuRepository2", "API call to facility $facilityId failed",
//                            RuntimeException(apiResponse.errorBody()?.string())
//                        )
                        return@launch
                    }
                    val apiResponseBody = apiResponse.body()!!
//                    Log.d(
//                        "MenuRepository2",
//                        "API response for facility $facilityId: $apiResponseBody"
//                    )
                    val offers = mapJsonObjectToOffers(apiResponseBody)
                    if (offers == null) {
//                        Log.d(
//                            "MenuRepository2",
//                            "Failed to map JSON object to offers for facility $facilityId"
//                        )
                        return@launch
                    }

                    for (offer in offers) {
                        val dbOffer = Offer(
                            id = 0,
                            facilityId = offer.facilityId!!,
                            date = offer.date!!
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

    suspend fun getOffersForDate(date: LocalDate): List<OfferWithPrices> {
        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
//        Log.d("MenuRepository2", "Last menu fetch date: $lastMenuFetchDate")
        if (lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != date.get(
                IsoFields.WEEK_OF_WEEK_BASED_YEAR
            )
        ) {
//            Log.d("MenuRepository2", "Fetching new menus from API")
            saveAllMenusToDB()
            dataStoreManager.updateLastMenuFetchDate()
        }
        return menuDao.getAllOffersForDate(date)
    }

    enum class Language(val lang: String) {
        GERMAN("de"),
        ENGLISH("en")
    }
}