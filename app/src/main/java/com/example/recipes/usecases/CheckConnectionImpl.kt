package com.example.recipes.usecases

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat

class CheckConnectionImpl{

    fun isNetConnected(context: Context): Boolean {
        val cm = ContextCompat.getSystemService(context, ConnectivityManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm?.activeNetwork ?: return false
            val activeNetwork = cm.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                cm?.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
//        return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo?.isConnected ?:false
    }
}