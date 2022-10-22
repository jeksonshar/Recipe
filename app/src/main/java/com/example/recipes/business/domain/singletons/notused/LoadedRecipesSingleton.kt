package com.example.recipes.business.domain.singletons.notused

object LoadedRecipesSingleton {

    var isDataLoaded: Boolean? = null

    fun clear() {
        isDataLoaded = null
    }
}