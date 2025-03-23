package com.github.maximilianschwaerzler.ethuzhmensa.data2.dto

import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

data class OfferDto(
    val facilityId: Int? = null,
    val date: LocalDate? = null,
    var menuOptions: List<MenuDto>? = null
)

data class MenuDto(
    val name: String? = null,
    val mealName: String? = null,
    val mealDescription: String? = null,
    var pricing: List<MenuPriceDto>? = null,
    val imageUrl: String? = null
)

data class MenuPriceDto(
    val price: Price? = null,
    val customerGroupId: Int? = null,
    val customerGroupDesc: String? = null,
    val customerGroupDescShort: String? = null,
)