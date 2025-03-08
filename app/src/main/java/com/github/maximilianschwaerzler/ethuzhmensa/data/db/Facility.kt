package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Facility(
    @PrimaryKey val facilityId: Int,
    val name: String,
    val location: String,
    // TODO: val customerGroups: HashMap<Int, String>
)
