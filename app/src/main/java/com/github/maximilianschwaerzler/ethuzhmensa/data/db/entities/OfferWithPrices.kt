/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OfferWithPrices(
    @Embedded val offer: Offer,
    @Relation(
        entity = Offer.Menu::class,
        parentColumn = "id",
        entityColumn = "offerId"
    )
    val menus: List<MenuWithPrices>,
)