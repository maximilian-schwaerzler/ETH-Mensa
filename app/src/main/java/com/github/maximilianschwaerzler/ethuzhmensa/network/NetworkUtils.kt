/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Extension function to check if the device is connected to the internet.
 */
fun ConnectivityManager.isConnected(): Boolean {
    val network = activeNetwork
    val capabilities = getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}