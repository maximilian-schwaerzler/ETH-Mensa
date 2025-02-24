package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObject
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.IOException
import java.net.UnknownHostException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class Facility(private val facilityId: Int) {
    var facilityInfo: FacilityInformation? = null

    /**
     * @return Returns a Result<JsonObject>. If failure is indicated, an IOError is to be treated as a network exception (e.g. no internet connection)
     */
    suspend fun fetchMenuJson(
        forDate: LocalDate = LocalDate.now()
    ): Result<JsonObject> {
        val startOfWeek = forDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = forDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val requestUri = Uri.Builder()
            .scheme("https")
            .authority("idapps.ethz.ch")
            .path("cookpit-pub-services/v1/weeklyrotas")
            .appendQueryParameter("client-id", "ethz-wcms")
            .appendQueryParameter("lang", "de")
            .appendQueryParameter("rs-first", "0")
            .appendQueryParameter("rs-size", "50")
            .appendQueryParameter("valid-after", dateFormatter.format(startOfWeek))
            .appendQueryParameter("valid-before", dateFormatter.format(endOfWeek))
            .appendQueryParameter("facility", facilityId.toString())
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

    suspend fun fetchFacilityInformation(): FacilityInformation? {
        val requestUri = Uri.Builder()
            .scheme("https")
            .authority("idapps.ethz.ch")
            .path("cookpit-pub-services/v1/facilities")
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
            when (e) {
                is FuelError -> {
                    Log.e("MensaMenuManager", e.message.toString(), e)
                }

                else -> Log.e("MensaMenuManager", "Exception in fetchFacilityInformation", e)
            }
            return null
        }

        val facilityName = jsonObject.get("facility-name").asJsonPrimitive.asString
        val facilityLocationByBuilding = StringBuilder()
            .append(jsonObject.get("building")?.asJsonPrimitive?.asString.orEmpty())
            .append(" ")
            .append(jsonObject.get("floor")?.asJsonPrimitive?.asString.orEmpty())
            .append(jsonObject.get("room-nr")?.asJsonPrimitive?.asString.orEmpty())
            .toString()

        val facilityCustomerGroupsArray = jsonObject.get("customer-group-array").asJsonArray
        val facilityCustomerGroupsMap = HashMap<Int, String>()
        for (customerGroup in facilityCustomerGroupsArray) {
            val groupId = customerGroup.asJsonObject.get("code").asJsonPrimitive.asInt
            val groupDesc = customerGroup.asJsonObject.get("desc-short").asJsonPrimitive.asString
            facilityCustomerGroupsMap[groupId] = groupDesc
        }


        facilityInfo = FacilityInformation(
            facilityId,
            facilityName,
            facilityLocationByBuilding,
            facilityCustomerGroupsMap
        )

        return facilityInfo
    }
}