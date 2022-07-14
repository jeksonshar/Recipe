package com.example.recipes.business.domain.singletons

object LoadedRecipesSingleton {

    var isDataLoaded: Boolean? = null

    fun clear() {
        isDataLoaded = null
    }
}