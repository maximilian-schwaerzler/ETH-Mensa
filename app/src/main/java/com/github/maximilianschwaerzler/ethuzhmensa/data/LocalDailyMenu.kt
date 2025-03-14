package com.github.maximilianschwaerzler.ethuzhmensa.data

import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

data class LocalDailyMenu(
    val facilityId: Int,
    val date: LocalDate,
    val menuOptions: List<LocalMenuOption>
) {
    data class LocalMenuOption(
        val name: String,
        val mealName: String,
        val mealDescription: String,
        val pricing: List<LocalMenuPrice>,
        val imageUrl: String? = null
    ) {
        data class LocalMenuPrice(
            val price: Price,
            val customerGroupId: Int,
            val customerGroupDesc: String,
            val customerGroupDescShort: String,
        )
    }
}
