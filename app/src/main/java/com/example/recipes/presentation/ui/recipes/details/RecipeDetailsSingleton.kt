package com.example.recipes.presentation.ui.recipes.details

import com.example.recipes.business.domain.models.Recipe

object RecipeDetailsSingleton {
    var recipe: Recipe? = null

    fun clear() {
        recipe = null
    }
}