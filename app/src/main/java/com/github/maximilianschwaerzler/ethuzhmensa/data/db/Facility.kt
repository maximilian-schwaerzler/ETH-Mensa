package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Facility(
    @PrimaryKey val facilityId: Int,
    val name: String,
    val location: String,
)
