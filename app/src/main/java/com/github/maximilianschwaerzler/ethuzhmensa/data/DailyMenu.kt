package com.github.maximilianschwaerzler.ethuzhmensa.data

import java.time.LocalDate

data class DailyMenu(
    val facilityId: Int,
    val date: LocalDate,
    val menuOptions: List<MenuOption>
) {
    data class MenuOption(
        val name: String,
        val mealName: String,
        val mealDescription: String,
        val pricing: List<MenuPrice>,
        val imageUrl: String? = null
    ) {
        data class MenuPrice(
            val price: Double,
            val customerGroupID: Int
        )
    }
}
