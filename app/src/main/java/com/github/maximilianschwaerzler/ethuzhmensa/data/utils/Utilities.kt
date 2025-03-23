package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer

fun pricesToString(prices: List<Offer.Menu.MenuPrice>): String {
    return prices.joinToString(" / ") { "${it.price} CHF" }
}