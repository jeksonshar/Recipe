package com.example.recipes.ui.recipes.searchlist

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.business.usecases.GetRecipesBySearchUseCase
import com.example.recipes.datasouce.RecipeDataStore

class RecipeSearchViewModelFactory(
    private val getRecipesBySearchUseCase: GetRecipesBySearchUseCase,
    private val recipeDataStore: RecipeDataStore,
    owner: SavedStateRegistryOwner
): AbstractSavedStateViewModelFactory(owner, null)  {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RecipeSearchListViewModel(getRecipesBySearchUseCase, recipeDataStore, handle) as T
    }
}