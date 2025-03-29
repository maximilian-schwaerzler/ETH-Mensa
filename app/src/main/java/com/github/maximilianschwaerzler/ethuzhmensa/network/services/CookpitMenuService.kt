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
import retrofit2.http.Query

interface CookpitMenuService {
    @GET("/cookpit-pub-services/v1/weeklyrotas?rs-first=0&rs-size=50")
    suspend fun fetchMenus(
        @Query("facility") facilityId: Int,
        @Query("valid-after") dateStart: String,
        @Query("lang") language: String = "de",
        @Query("valid-before") dateEnd: String? = null,
    ): Response<JsonObject>
}