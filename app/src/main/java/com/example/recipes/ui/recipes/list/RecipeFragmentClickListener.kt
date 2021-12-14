package com.example.recipes.ui.recipes.list

import com.example.recipes.data.Recipe

interface RecipeFragmentClickListener {

    fun openRecipeDetailsFragment(recipe: Recipe)

}