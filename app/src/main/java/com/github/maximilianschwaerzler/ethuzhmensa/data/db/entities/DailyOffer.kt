package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class DailyOffer(
    val facilityId: Int,
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: LocalDate
) {
    @Entity(
        foreignKeys = [
            ForeignKey(
                entity = DailyOffer::class,
                parentColumns = ["id"],
                childColumns = ["offerId"],
                onDelete = ForeignKey.CASCADE
            )
        ]
    )
    data class Menu(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(index = true) val offerId: Long,
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
            ]
        )
        data class MenuPrice(
            @PrimaryKey(autoGenerate = true) val id: Long,
            @ColumnInfo(index = true) val menuId: Long,
            val price: Double,
            val customerGroupId: Int,
            val customerGroupDesc: String,
            val customerGroupDescShort: String,
        )
    }
}
