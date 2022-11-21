package com.example.recipes.business.domain.singletons

object SearchEnteredSingleton {

    var isSearchEntered: Boolean? = null

    fun clear() {
        isSearchEntered = null
    }
}