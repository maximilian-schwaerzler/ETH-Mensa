/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer

/**
 * Converts a list of menu prices to a formatted string.
 *
 * @param prices The list of menu prices to convert.
 * @return A string representation of the prices, formatted as "price CHF" and separated by " / ".
 */
fun pricesToString(prices: List<Offer.Menu.MenuPrice>): String {
    return prices.joinToString(" / ") { "${it.price} CHF" }
}