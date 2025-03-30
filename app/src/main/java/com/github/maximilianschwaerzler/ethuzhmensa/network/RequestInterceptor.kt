/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.network

import android.content.Context
import com.github.maximilianschwaerzler.ethuzhmensa.R
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the client-id to the request.
 * @see [Interceptor]
 */
internal class RequestInterceptor(
    private val appContext: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("client-id", appContext.getString(R.string.cookpit_client_id))
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}