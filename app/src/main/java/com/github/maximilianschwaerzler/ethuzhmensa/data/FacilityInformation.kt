package com.github.maximilianschwaerzler.ethuzhmensa.data

data class FacilityInformation(
    val facilityId: Int,
    val name: String,
    val locationByBuilding: String,
    val customerGroups: HashMap<Int, String>
)
