package com.example.recipes.ui.recipes.searchlist

import com.example.recipes.data.Recipe

interface RecipeFragmentClickListener {

    fun openRecipeDetailsFragment(recipe: Recipe)

}