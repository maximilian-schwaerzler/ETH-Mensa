package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class MenuWithPrices(
    @Embedded val menu: DailyOffer.Menu,
    @Relation(
        parentColumn = "id",
        entityColumn = "menuId"
    )
    val prices: List<DailyOffer.Menu.MenuPrice>
)
