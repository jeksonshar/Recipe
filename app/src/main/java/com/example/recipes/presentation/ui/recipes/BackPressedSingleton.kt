package com.example.recipes.presentation.ui.recipes

object BackPressedSingleton {

    var isBackPressClick: Boolean? = null

    fun clear() {
        isBackPressClick = null
    }

}