package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import android.net.Uri
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObject
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.LocalDailyMenu
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Fetches the menu information from the API
 *
 * @param facilityId The ID of the facility
 * @param forWeek The week for which the menu should be fetched
 * @return Returns a Result object with the menu information
 */
private suspend fun fetchMenuJson(
    facilityId: Int,
    forWeek: LocalDate = LocalDate.now()
): Result<JsonObject> {
    val startOfWeek = forWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = forWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val requestUri = Uri.Builder()
        .scheme("https")
        .authority("idapps.ethz.ch")
        .path("cookpit-pub-services/v1/weeklyrotas")
        .appendQueryParameter("client-id", "ethz-wcms")
        .appendQueryParameter("lang", "de")
        .appendQueryParameter("rs-first", "0")
        .appendQueryParameter("rs-size", "50")
        .appendQueryParameter("valid-after", dateFormatter.format(startOfWeek))
        .appendQueryParameter("valid-before", dateFormatter.format(endOfWeek))
        .appendQueryParameter("facility", facilityId.toString())
        .build()

    val jsonObject = try {
        Fuel.get(requestUri.toString())
            .awaitObject(object : ResponseDeserializable<JsonObject> {
                override fun deserialize(content: String): JsonObject {
                    return JsonParser.parseString(content).asJsonObject
                }
            })
    } catch (e: Exception) {
        return Result.failure(
            // Work around for Log.e not printing stack traces if exception is a UnknownHostException
            when (e) {
                is FuelError ->
                    if (e.exception is UnknownHostException) IOException(e.message) else e

                else -> e
            }
        )
    }

    return Result.success(jsonObject)
}

/**
 * Parses the menu information from the API
 *
 * @param facilityId The ID of the facility
 * @param forWeek The week for which the menu should be fetched
 * @return Returns a list of LocalDailyMenu objects
 */
private suspend fun parseMenuJson(facilityId: Int, forWeek: LocalDate): List<LocalDailyMenu> {
    val menuJson = fetchMenuJson(facilityId, forWeek).getOrNull() ?: return emptyList()
    val returnList = mutableListOf<LocalDailyMenu>()
    val facilityId =
        menuJson.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("facility-id").asJsonPrimitive.asInt
    val weekStart =
        menuJson.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("valid-from").asJsonPrimitive.asString.toString()
            .let { LocalDate.parse(it) }
    val weekDayArray =
        menuJson.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("day-of-week-array").asJsonArray
    for (day in weekDayArray) {
        val date =
            weekStart.plusDays((day.asJsonObject.get("day-of-week-code").asJsonPrimitive.asInt - 1).toLong())
        val dailyMenu = LocalDailyMenu(facilityId, date)
        val menus = try {
            day.asJsonObject.get("opening-hour-array").asJsonArray.get(0).asJsonObject.get("meal-time-array").asJsonArray.get(
                0
            ).asJsonObject.get("line-array").asJsonArray
        } catch (e: NullPointerException) {
            // TODO: Only for debugging
            // Log.w("MenuApiUtils", "No menu for $date in facility $facilityId")
            continue
        }
        for (menuItem in menus) {
            val menuName = menuItem.asJsonObject.get("name").asJsonPrimitive.asString
            val meal = menuItem.asJsonObject.get("meal").asJsonObject
            val mealName = meal.get("name").asJsonPrimitive.asString
            val mealDesc = meal.get("description").asJsonPrimitive.asString
            val imageUrl = meal.get("image-url")?.asJsonPrimitive?.asString
            val localMenuItem = LocalDailyMenu.LocalMenuOption(
                name = menuName,
                mealName = mealName,
                mealDescription = mealDesc,
                // TODO: Add image URL
                imageUrl = imageUrl
            )
            for (priceCategory in meal.get("meal-price-array").asJsonArray) {
                val customerGroupId =
                    priceCategory.asJsonObject.get("customer-group-code").asJsonPrimitive.asInt
                val customerGroupDesc =
                    priceCategory.asJsonObject.get("customer-group-desc").asJsonPrimitive.asString
                val customerGroupDescShort =
                    priceCategory.asJsonObject.get("customer-group-desc-short").asJsonPrimitive.asString
                val price = priceCategory.asJsonObject.get("price").asJsonPrimitive.asDouble
                val localPrice = LocalDailyMenu.LocalMenuOption.LocalMenuPrice(
                    price = Price(price),
                    customerGroupId = customerGroupId,
                    customerGroupDesc = customerGroupDesc,
                    customerGroupDescShort = customerGroupDescShort
                )
                localMenuItem.pricing = localMenuItem.pricing.plus(localPrice)
            }
            dailyMenu.menuOptions = dailyMenu.menuOptions.plus(localMenuItem)
        }
        returnList.add(dailyMenu)
    }
    return returnList
}

/**
 * Fetches the menu information from the API and saves it in the database
 *
 * @param context Application context
 * @param facilityId The ID of the facility
 * @param forWeek The week for which the menu should be fetched
 */
suspend fun saveDailyMenusForFacilityDateToDB(
    context: Context,
    facilityId: Int,
    forWeek: LocalDate
) = withContext(
    Dispatchers.IO
) {
    val db = MensaDatabase.getInstance(context)
    val dailyMenuDao = db.menuDao()
    val localDailyMenus = parseMenuJson(facilityId, forWeek)
    for (localDailyMenu in localDailyMenus) {
        val dailyOffer = DailyOffer(
            id = 0,
            facilityId = localDailyMenu.facilityId!!,
            date = localDailyMenu.date!!
        )
        val dailyOfferId = dailyMenuDao.insertOffer(dailyOffer)
        for (localMenuOption in localDailyMenu.menuOptions) {
            val menu = DailyOffer.Menu(
                id = 0,
                offerId = dailyOfferId,
                name = localMenuOption.name!!,
                mealName = localMenuOption.mealName!!,
                mealDescription = localMenuOption.mealDescription!!,
                imageUrl = localMenuOption.imageUrl
            )
            val menuId = dailyMenuDao.insertMenu(menu)
            for (localMenuPrice in localMenuOption.pricing) {
                val price = DailyOffer.Menu.MenuPrice(
                    id = 0,
                    menuId = menuId,
                    price = localMenuPrice.price!!,
                    customerGroupId = localMenuPrice.customerGroupId!!,
                    customerGroupDesc = localMenuPrice.customerGroupDesc!!,
                    customerGroupDescShort = localMenuPrice.customerGroupDescShort!!
                )
                dailyMenuDao.insertPrice(price)
            }
        }
    }
    // TODO: Delete old entries
    //dailyMenuDao.deleteOlderThan(LocalDate.now())
}

suspend fun saveAllDailyMenusToDBConcurrent(context: Context, forWeek: LocalDate) =
    withContext(Dispatchers.IO) {
        val mensaIds =
            context.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        coroutineScope {
            for (facilityId in mensaIds) {
                launch { saveDailyMenusForFacilityDateToDB(context, facilityId, forWeek) }
            }
        }
    }

@Deprecated(
    "Use saveAllDailyMenusToDBConcurrent instead",
    replaceWith = ReplaceWith("saveAllDailyMenusToDBConcurrent(context, forWeek)")
)
suspend fun saveAllDailyMenusToDBSerial(context: Context, forWeek: LocalDate) =
    withContext(Dispatchers.IO) {
        val mensaIds =
            context.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        for (facilityId in mensaIds) {
            saveDailyMenusForFacilityDateToDB(context, facilityId, forWeek)
        }
    }