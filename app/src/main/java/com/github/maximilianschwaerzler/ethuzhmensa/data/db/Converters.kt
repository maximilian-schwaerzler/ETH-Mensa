package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.TypeConverter
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDateTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromPriceInt(value: Int?): Price? {
        return value?.let {
            Price(
                BigDecimal(value).divide(
                    BigDecimal(100),
                    2,
                    RoundingMode.UNNECESSARY
                )
            )
        }
    }

    @TypeConverter
    fun priceToInt(price: Price?): Int? {
        return price?.toBigDecimal()?.multiply(BigDecimal(100))?.toInt()
    }
}