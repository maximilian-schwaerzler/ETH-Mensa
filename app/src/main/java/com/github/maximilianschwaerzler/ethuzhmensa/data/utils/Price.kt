/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Represents a price with at most 2 decimal places.
 */
@JvmInline
value class Price(private val amount: BigDecimal) : Comparable<Price> {

    init {
        require(amount.scale() <= 2) { "Price must have at most 2 decimal places." }
    }

    constructor(value: String) : this(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))
    constructor(value: Double) : this(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))
    constructor(value: Int) : this(BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY))

//    operator fun plus(other: Price): Price = Price(amount + other.amount)
//    operator fun minus(other: Price): Price = Price(amount - other.amount)
//    operator fun times(multiplier: BigDecimal): Price = Price(amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP))
//    operator fun div(divisor: BigDecimal): Price = Price(amount.divide(divisor, 2, RoundingMode.HALF_UP))

    override fun compareTo(other: Price): Int = amount.compareTo(other.amount)
    override fun toString(): String = amount.toString()

    fun toBigDecimal(): BigDecimal = amount
}
