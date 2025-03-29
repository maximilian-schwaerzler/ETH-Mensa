/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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