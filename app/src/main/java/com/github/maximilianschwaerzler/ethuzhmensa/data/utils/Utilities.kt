package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer

fun pricesToString(prices: List<DailyOffer.Menu.MenuPrice>): String {
    return prices.joinToString(" / ") { "${it.price} CHF" }
}