package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.Price
import java.time.LocalDate

@Entity
data class Offer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val facilityId: Int,
    val date: LocalDate
) {
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
