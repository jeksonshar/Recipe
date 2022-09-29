package com.example.recipes.business.domain.singletons

object NetworkStatusSingleton {

    var isNetworkConnected = false

    fun clear() {
        isNetworkConnected = false
    }
}