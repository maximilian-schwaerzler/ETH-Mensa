/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.dto

import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

/**
 * Data Transfer Object (DTO) representing an offer with its associated menus and prices.
 */
data class OfferDto(
    val facilityId: Int? = null,
    val date: LocalDate? = null,
    var menuOptions: List<MenuDto> = emptyList()
) {
    data class MenuDto(
        val name: String? = null,
        val mealName: String? = null,
        val mealDescription: String? = null,
        val imageUrl: String? = null,
        var pricing: List<MenuPriceDto> = emptyList()
    ) {
        data class MenuPriceDto(
            val price: Price? = null,
            val customerGroupId: Int? = null,
            val customerGroupDesc: String? = null,
            val customerGroupDescShort: String? = null
        )
    }
}