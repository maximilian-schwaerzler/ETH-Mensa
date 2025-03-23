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