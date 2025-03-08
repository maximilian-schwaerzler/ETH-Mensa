package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Entity

@Entity(primaryKeys = ["facilityId", "groupId"])
data class CustomerGroup(
    val facilityId: Int,
    val groupId: Int,
    val name: String,
)
