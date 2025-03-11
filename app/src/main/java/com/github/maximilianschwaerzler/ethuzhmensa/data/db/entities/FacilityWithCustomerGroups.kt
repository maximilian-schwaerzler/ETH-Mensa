package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FacilityWithCustomerGroups(
    @Embedded val facility: Facility,
    @Relation(
        parentColumn = "id",
        entityColumn = "facilityId"
    )
    val customerGroups: List<CustomerGroup>
)