package com.example.recipes.presentation.ui.recipes.searchlist

object LoadingRecipesSingleton {

    var isDataLoaded: Boolean? = null

    fun clear() {
        isDataLoaded = null
    }
}