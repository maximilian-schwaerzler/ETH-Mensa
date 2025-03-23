package com.github.maximilianschwaerzler.ethuzhmensa.network.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CookpitMenuService {
    @GET("/cookpit-pub-services/v1/weeklyrotas")
    suspend fun fetchMenus(
        @Query("facility") facilityId: Int,
        @Query("valid-after") date: Long,
        @Query("lang") language: String = "de"
    ): Response<JsonObject>
}