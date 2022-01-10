package com.example.recipes.business.domain.singletons

import com.example.recipes.business.domain.models.Recipe

object RecipeSingleton {
    var recipe: Recipe? = null

    fun clear() {
        recipe = null
    }
}