package com.example.recipes.ui.recipes

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.business.usecases.GetRecipesBySearchUseCase
import com.example.recipes.datasouce.RecipeDataStore
import com.example.recipes.ui.recipes.details.RecipeDetailsViewModel
import com.example.recipes.ui.recipes.searchlist.RecipeSearchListViewModel

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory(
    private val getRecipesBySearchUseCase: GetRecipesBySearchUseCase,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val recipeDataStore: RecipeDataStore,
    owner: SavedStateRegistryOwner
    ): AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {

        return when {
            modelClass.isAssignableFrom(RecipeSearchListViewModel::class.java) -> RecipeSearchListViewModel(getRecipesBySearchUseCase, recipeDataStore, handle) as T
            modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java) -> RecipeDetailsViewModel(getRecipeUseCase, handle) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}