package com.github.maximilianschwaerzler.ethuzhmensa.network.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CookpitMenuService {
    @GET("/cookpit-pub-services/v1/weeklyrotas")
    suspend fun fetchMenus(
        @Query("facility") facilityId: Int,
        @Query("valid-after") dateStart: String,
        @Query("lang") language: String = "de",
        @Query("valid-before") dateEnd: String? = null,
    ): Response<JsonObject>
}