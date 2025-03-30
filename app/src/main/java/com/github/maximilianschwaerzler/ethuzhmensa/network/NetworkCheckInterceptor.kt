/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.network

import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Exception thrown when there is no internet connection.
 */
class OfflineException : IOException("No internet connection")

/**
 * Interceptor that checks if the device is connected to the internet.
 * If not, it throws an [OfflineException].
 */
internal class NetworkCheckInterceptor(
    private val connMgr: ConnectivityManager,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connMgr.isConnected()) {
            throw OfflineException()
        }

        return chain.proceed(chain.request())
    }
}