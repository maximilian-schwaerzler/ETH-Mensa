package com.github.maximilianschwaerzler.ethuzhmensa.network.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CookpitFacilityService {
    @GET("/cookpit-pub-services/v1/facilities/{id}")
    suspend fun fetchFacility(
        @Path("id") facilityId: Int,
        @Query("lang") language: String = "de",
    ): Response<JsonObject>
}