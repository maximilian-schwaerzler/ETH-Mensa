package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Embedded
import androidx.room.Relation

data class FacilityWithCustomerGroups(
    @Embedded val facility: Facility,
    @Relation(
        parentColumn = "facilityId",
        entityColumn = "facilityId"
    )
    val customerGroups: List<CustomerGroup>
)
