package com.example.recipes.business.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.example.recipes.business.domain.singletons.NetworkStatusSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object CheckConnectionUtils {

//    fun isNetConnected(context: Context): Boolean {
//        val cm = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
//
//        val network = cm?.activeNetwork ?: return false
//        val activeNetwork = cm.getNetworkCapabilities(network) ?: return false
//        return when {
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            else -> false
//        }
//    }

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            try {
                Socket().use {
                    it.connect(InetSocketAddress("8.8.8.8", 53))
                }
                NetworkStatusSingleton.isNetworkConnected = true
            } catch (e: Exception) {
                e.printStackTrace()
                NetworkStatusSingleton.isNetworkConnected = false
            }
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            NetworkStatusSingleton.isNetworkConnected = false
        }
    }

    fun getNetConnection(context: Context) {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}