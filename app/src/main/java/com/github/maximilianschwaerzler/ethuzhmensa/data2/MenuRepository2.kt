package com.github.maximilianschwaerzler.ethuzhmensa.data2

import android.content.Context
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
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
        return menuService.fetchMenus(
            facilityId, date.let {
                DateTimeFormatter.ISO_LOCAL_DATE.format(it)
            },
            language.lang
        )
    }

    private suspend fun saveAllMenusToDB(date: LocalDate = LocalDate.now()) {
        val validFacilityIds =
            appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        for (facilityId in validFacilityIds) {
            val apiResponse = fetchOfferForFacility(facilityId)
            if (!apiResponse.isSuccessful) {
                Log.e("MenuRepository2", "API call to facility $facilityId failed",
                    RuntimeException(apiResponse.errorBody().toString()))
                continue
            }
            val apiResponseBody = apiResponse.body()!!

        }
    }

    fun getOffersForDate() = flow<List<OfferWithPrices>> {
        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
        if (lastMenuFetchDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) != LocalDate.now().get(
                IsoFields.WEEK_OF_WEEK_BASED_YEAR
            )
        ) {

        }
    }.flowOn(Dispatchers.IO)

    enum class Language(val lang: String) {
        GERMAN("de"),
        ENGLISH("en")
    }
}