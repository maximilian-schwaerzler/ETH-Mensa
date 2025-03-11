package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Facility(
    @PrimaryKey val id: Int,
    val name: String,
    val location: String,
)