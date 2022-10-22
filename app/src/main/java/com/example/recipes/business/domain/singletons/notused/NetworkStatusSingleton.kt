package com.example.recipes.business.domain.singletons.notused

object NetworkStatusSingleton {

    var isNetworkConnected = false

    fun clear() {
        isNetworkConnected = false
    }
}