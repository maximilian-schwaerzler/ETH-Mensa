package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObject
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.LocalFacilityInfo
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

/**
 * Fetches the facility information from the API
 *
 * @param facilityId The ID of the facility
 * @return Returns a Result object with the facility information
 */
private suspend fun fetchFacilityJson(facilityId: Int): Result<JsonObject> {
    val requestUri = Uri.Builder()
        .scheme("https")
        .authority("idapps.ethz.ch")
        .path("cookpit-pub-services/v1/facilities/")
        .appendPath(facilityId.toString())
        .appendQueryParameter("client-id", "ethz-wcms")
        .appendQueryParameter("lang", "de")
        .build()

    val jsonObject = try {
        Fuel.get(requestUri.toString())
            .awaitObject(object : ResponseDeserializable<JsonObject> {
                override fun deserialize(content: String): JsonObject {
                    return JsonParser.parseString(content).asJsonObject
                }
            })
    } catch (e: Exception) {
        return Result.failure(
            // Work around for Log.e not printing stack traces if exception is a UnknownHostException
            when (e) {
                is FuelError ->
                    if (e.exception is UnknownHostException) IOException(e.message) else e

                else -> e
            }
        )
    }

    return Result.success(jsonObject)
}

/**
 * Parses the facility information from the API
 *
 * @param facilityId The ID of the facility
 * @return Returns a LocalFacilityInfo object
 */
private suspend fun parseFacilityInformation(facilityId: Int): LocalFacilityInfo? {
    val facilityJson = fetchFacilityJson(facilityId).getOrNull() ?: return null

    val facilityName = facilityJson.get("facility-name").asJsonPrimitive.asString
    val facilityLocationByBuilding = StringBuilder()
        .append(facilityJson.get("building")?.asJsonPrimitive?.asString.orEmpty())
        .append(" ")
        .append(facilityJson.get("floor")?.asJsonPrimitive?.asString.orEmpty())
        .append(facilityJson.get("room-nr")?.asJsonPrimitive?.asString.orEmpty())
        .toString()

    return LocalFacilityInfo(
        facilityId,
        facilityName,
        facilityLocationByBuilding
    )
}

/**
 * Fetches the facility information from the API and saves it in the database
 *
 * @param context Application context
 */
suspend fun saveFacilityInfoToDB(context: Context) = withContext(Dispatchers.IO) {
    val db = MensaDatabase.getInstance(context)
    val facilityDao = db.facilityDao()

    // Get the facility information from the API
    val mensaIds = context.resources.getIntArray(R.array.id_mensas_with_customer_groups)
    for (facilityId in mensaIds) {
        val localFacilityInfo = parseFacilityInformation(facilityId)

        // If localFacilityInfo is null, skip this facility
        if (localFacilityInfo == null) {
            Log.w("updateFacilityInfoDB", "Facility with ID=$facilityId could not be fetched")
            continue
        }

        Log.d("updateFacilityInfoDB", "Facility with ID=$facilityId fetched\n$localFacilityInfo")

        // Save the facility information in the database
        facilityDao.insertAll(
            Facility(
                id = localFacilityInfo.facilityId,
                name = localFacilityInfo.name,
                location = localFacilityInfo.locationByBuilding
            )
        )

        Log.d("updateFacilityInfoDB", "Facility with ID=$facilityId saved in the database")
    }
}