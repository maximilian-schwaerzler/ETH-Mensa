package com.github.maximilianschwaerzler.ethuzhmensa.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the client-id to the request.
 * @see [Interceptor]
 */
internal class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("client-id", "ethz-wcms")
            .build()

//        Log.d("RequestInterceptor", "New url: $url")

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}