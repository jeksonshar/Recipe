package com.example.recipes.presentation.ui.recipes

import com.example.recipes.business.domain.models.Recipe

interface RecipeClickListener {

    fun openRecipeDetailsFragment(recipe: Recipe)
}