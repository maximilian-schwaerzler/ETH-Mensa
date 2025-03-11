package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DailyOfferWithPrices(
    @Embedded val dailyOffer: DailyOffer,
    @Relation(
        entity = DailyOffer.Menu::class,
        parentColumn = "id",
        entityColumn = "offerId"
    )
    val menus: List<MenuWithPrices>,
)