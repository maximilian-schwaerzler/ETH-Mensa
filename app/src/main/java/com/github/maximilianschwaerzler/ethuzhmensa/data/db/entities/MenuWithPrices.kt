/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a menu along with its associated prices.
 */
data class MenuWithPrices(
    @Embedded val menu: Offer.Menu,
    @Relation(
        parentColumn = "id",
        entityColumn = "menuId"
    )
    val prices: List<Offer.Menu.MenuPrice>
)
