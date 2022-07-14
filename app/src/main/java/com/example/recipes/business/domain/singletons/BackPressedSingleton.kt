package com.example.recipes.business.domain.singletons

object BackPressedSingleton {

    var isBackPressClick: Boolean? = null

    fun clear() {
        isBackPressClick = null
    }

}