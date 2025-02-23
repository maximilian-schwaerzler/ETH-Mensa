package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.Result
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class MensaMenuManager(private val facilityId: Int) {
    suspend fun fetchMenuJson(
        forDate: LocalDate = LocalDate.now()
    ): JsonObject? {
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

        val result = Fuel.get(requestUri.toString()).awaitStringResult()

        if (result is Result.Failure) {
            Log.e("MensaMenuManager", null, result.getException())
            return null
        }

        // Just to be safe...
        assert(result is Result.Success)

        val data = result.get()
        val tree = JsonParser.parseString(data).asJsonObject
        return tree
    }
}