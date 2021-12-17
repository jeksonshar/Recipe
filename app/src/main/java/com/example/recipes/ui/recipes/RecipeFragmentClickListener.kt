package com.example.recipes.ui.recipes

import com.example.recipes.data.Recipe

interface RecipeFragmentClickListener {

    fun openRecipeDetailsFragment(recipe: Recipe)

}