package com.example.recipes.business.usecases

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.recipes.business.usecases.interfaces.CheckConnectionUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class CheckConnectionUseCaseImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : CheckConnectionUseCase {

    // Третья реализация проверки (на лету)
    override fun isConnected(): Flow<Boolean> = callbackFlow {

        val networkRequest: NetworkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(checkNetConnection())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(checkNetConnection())
            }
        }

        trySend(checkNetConnection())
        connectivityManager.registerNetworkCallback(networkRequest, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private fun checkNetConnection(): Boolean {
        return try {
            Socket().use {
                it.connect(InetSocketAddress("8.8.8.8", 53))
            }
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }


    // первая реализация проверки
/*//    fun isNetConnected(context: Context): Boolean {
//        val cm = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
//
//        val network = cm?.activeNetwork ?: return false
//        val activeNetwork = cm.getNetworkCapabilities(network) ?: return false
//        return when {
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            else -> false
//        }
//    }*/

    // Вторая реализация проверки
//    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
//        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//        .build()
//
//    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
//        // network is available for use
//        override fun onAvailable(network: Network) {
//            super.onAvailable(network)
//            try {
//                Socket().use {
//                    it.connect(InetSocketAddress("8.8.8.8", 53))
//                }
//                NetworkStatusSingleton.isNetworkConnected = true
//            } catch (e: Exception) {
//                e.printStackTrace()
//                NetworkStatusSingleton.isNetworkConnected = false
//            }
//        }
//
//        // lost network connection
//        override fun onLost(network: Network) {
//            super.onLost(network)
//            NetworkStatusSingleton.isNetworkConnected = false
//        }
//    }
//
//    fun registerNetConnection(context: Context) {
//        val connectivityManager = getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
//        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
//    }
//
//    fun unregisterNetConnection(context: Context) {
//        val connectivityManager = getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
//        connectivityManager.unregisterNetworkCallback(networkCallback)
//    }
}