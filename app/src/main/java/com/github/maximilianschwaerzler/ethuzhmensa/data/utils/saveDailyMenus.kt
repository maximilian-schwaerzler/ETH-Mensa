package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import com.github.maximilianschwaerzler.ethuzhmensa.data.LocalDailyMenu
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

fun parseMenuJson(menuJson: JsonObject): List<LocalDailyMenu> {
    val weekStart = menuJson.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("valid-from").asJsonPrimitive.asString.toString().let { LocalDate.parse(it) }
    val weekDayArray =
        menuJson.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("day-of-week-array").asJsonArray
    for (day in weekDayArray) {
        val date = weekStart.plusDays((day.asJsonObject.get("day-of-week-code").asJsonPrimitive.asInt - 1).toLong())
        val menus = day.asJsonObject.get("opening-hour-array").asJsonArray.get(0).asJsonObject.get("meal-time-array").asJsonArray.get(0).asJsonObject.get("line-array").asJsonArray
        for (menu in menus) {
            val menuName = menu.asJsonObject.get("name").asJsonPrimitive.asString
            val mealName = menu.asJsonObject.get("meal").asJsonObject.get("name").asJsonPrimitive.asString
            val mealDesc = menu.asJsonObject.get("meal").asJsonObject.get("description").asJsonPrimitive.asString
        }
    }
    return emptyList()
}

suspend fun saveDailyMenu(context: Context, facility: Facility, weekOf: LocalDate) =
    withContext(Dispatchers.IO) {
        val db = MensaDatabase.getInstance(context)
        val facilityDao = db.facilityDao()

        val menuJson = fetchMenuJson(facility.id, weekOf)
    }