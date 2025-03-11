package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(primaryKeys = ["facilityId", "groupId"], foreignKeys = [
    ForeignKey(
        entity = Facility::class,
        parentColumns = ["id"],
        childColumns = ["facilityId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class CustomerGroup(
    val facilityId: Int,
    val groupId: Int,
    val name: String,
)