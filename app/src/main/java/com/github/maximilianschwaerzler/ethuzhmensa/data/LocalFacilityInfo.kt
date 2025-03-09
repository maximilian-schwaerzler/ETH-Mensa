package com.github.maximilianschwaerzler.ethuzhmensa.data

data class LocalFacilityInfo(
    val facilityId: Int,
    val name: String,
    val locationByBuilding: String,
    val customerGroups: HashMap<Int, String>
)
