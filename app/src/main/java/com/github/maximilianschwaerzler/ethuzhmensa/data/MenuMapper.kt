package com.github.maximilianschwaerzler.ethuzhmensa.data

import com.github.maximilianschwaerzler.ethuzhmensa.data.dto.OfferDto
import com.github.maximilianschwaerzler.ethuzhmensa.data.dto.OfferDto.MenuDto
import com.github.maximilianschwaerzler.ethuzhmensa.data.dto.OfferDto.MenuDto.MenuPriceDto
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import com.google.gson.JsonObject
import java.time.LocalDate

fun mapJsonObjectToOffers(jsonObject: JsonObject): List<OfferDto>? {
    if (jsonObject.isEmpty) {
        return null
    }
    val returnList = mutableListOf<OfferDto>()

    val facilityId =
        jsonObject.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("facility-id").asJsonPrimitive.asInt
    val weekStart =
        jsonObject.get("weekly-rota-array").asJsonArray.get(0).asJsonObject.get("valid-from").asJsonPrimitive.asString.toString()
            .let { LocalDate.parse(it) }
    val weekArray = jsonObject.get("weekly-rota-array").asJsonArray
    for (week in weekArray) {
        val weekDayArray =
            week.asJsonObject.get("day-of-week-array").asJsonArray
        for (day in weekDayArray) {
            val date =
                weekStart.plusDays((day.asJsonObject.get("day-of-week-code").asJsonPrimitive.asInt - 1).toLong())
            val offerDto = OfferDto(facilityId, date)
            val menus = try {
                day.asJsonObject.get("opening-hour-array").asJsonArray.get(0).asJsonObject.get("meal-time-array").asJsonArray.get(
                    0
                ).asJsonObject.get("line-array").asJsonArray
            } catch (_: NullPointerException) {
//            Log.w("MenuMapper", "No menu for $date in facility $facilityId", e)
                continue
            }
            for (menuItem in menus) {
                val menuName = menuItem.asJsonObject.get("name").asJsonPrimitive.asString
                val meal = try {
                    menuItem.asJsonObject.get("meal").asJsonObject
                } catch (_: Exception) {
//                Log.w("MenuMapper", "No meal for $menuName in facility $facilityId on $date")
                    continue
                }
                val mealName = meal.get("name").asJsonPrimitive.asString
                val mealDesc = meal.get("description").asJsonPrimitive.asString
                val imageUrl = meal.get("image-url")?.asJsonPrimitive?.asString
                val menuDto = MenuDto(
                    name = menuName,
                    mealName = mealName,
                    mealDescription = mealDesc,
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
                    val localPrice = MenuPriceDto(
                        price = Price(price),
                        customerGroupId = customerGroupId,
                        customerGroupDesc = customerGroupDesc,
                        customerGroupDescShort = customerGroupDescShort
                    )
                    menuDto.pricing += localPrice
                }
                offerDto.menuOptions += menuDto
            }
            returnList.add(offerDto)
        }
    }

    return returnList
}

