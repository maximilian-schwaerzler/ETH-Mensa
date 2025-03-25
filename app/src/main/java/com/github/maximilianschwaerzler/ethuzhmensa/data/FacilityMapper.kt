package com.github.maximilianschwaerzler.ethuzhmensa.data

import com.github.maximilianschwaerzler.ethuzhmensa.data.dto.FacilityDto
import com.google.gson.JsonObject

fun mapJsonObjectToFacility(jsonObject: JsonObject): FacilityDto {
    val facilityId = jsonObject.get("facility-id").asJsonPrimitive.asInt
    val facilityName = jsonObject.get("facility-name").asJsonPrimitive.asString
    val facilityLocationByBuilding = StringBuilder()
        .append(jsonObject.get("building")?.asJsonPrimitive?.asString.orEmpty())
        .append(" ")
        .append(jsonObject.get("floor")?.asJsonPrimitive?.asString.orEmpty())
        .append(jsonObject.get("room-nr")?.asJsonPrimitive?.asString.orEmpty())
        .toString()

    return FacilityDto(
        facilityId,
        facilityName,
        facilityLocationByBuilding
    )
}