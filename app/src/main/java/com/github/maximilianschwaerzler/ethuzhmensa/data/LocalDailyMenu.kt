package com.github.maximilianschwaerzler.ethuzhmensa.data

import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

data class LocalDailyMenu(
    val facilityId: Int? = null,
    val date: LocalDate? = null,
    var menuOptions: List<LocalMenuOption> = emptyList()
) {
    data class LocalMenuOption(
        val name: String? = null,
        val mealName: String? = null,
        val mealDescription: String? = null,
        var pricing: List<LocalMenuPrice> = emptyList(),
        val imageUrl: String? = null
    ) {
        data class LocalMenuPrice(
            val price: Price? = null,
            val customerGroupId: Int? = null,
            val customerGroupDesc: String? = null,
            val customerGroupDescShort: String? = null,
        )
    }
}
