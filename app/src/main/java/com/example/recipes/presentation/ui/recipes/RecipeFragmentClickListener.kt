package com.example.recipes.presentation.ui.recipes

import com.example.recipes.business.domain.models.Recipe

interface RecipeFragmentClickListener {

    fun openRecipeDetailsFragment(recipe: Recipe)

//    fun deleteRecipeOnLongClick(recipe: Recipe)

}