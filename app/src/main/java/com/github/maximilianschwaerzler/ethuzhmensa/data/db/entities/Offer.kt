/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

/**
 * Data class representing an offer entity in the database.
 */
@Entity
data class Offer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val facilityId: Int,
    val date: LocalDate
) {
    /**
     * Data class representing a menu entity in the database.
     */
    @Entity(
        foreignKeys = [
            ForeignKey(
                entity = Offer::class,
                parentColumns = ["id"],
                childColumns = ["offerId"],
                onDelete = ForeignKey.CASCADE
            )
        ],
        indices = [Index("offerId")]
    )
    data class Menu(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val offerId: Long,
        val name: String,
        val mealName: String,
        val mealDescription: String,
        val imageUrl: String? = null
    ) {
        /**
         * Data class representing a menu price entity in the database.
         */
        @Entity(
            foreignKeys = [
                ForeignKey(
                    entity = Menu::class,
                    parentColumns = ["id"],
                    childColumns = ["menuId"],
                    onDelete = ForeignKey.CASCADE
                )
            ],
            indices = [Index("menuId")]
        )
        data class MenuPrice(
            @PrimaryKey(autoGenerate = true) val id: Long,
            val menuId: Long,
            val price: Price,
            val customerGroupId: Int,
            val customerGroupDesc: String,
            val customerGroupDescShort: String,
        )
    }
}
