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