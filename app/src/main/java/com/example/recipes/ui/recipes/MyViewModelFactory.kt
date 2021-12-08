package com.example.recipes.ui.recipes

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.datasouce.network.RecipesPagingSource

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory(
    private val recipesUseCase: RecipesUseCase,
    owner: SavedStateRegistryOwner
    ): AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RecipeListViewModel(recipesUseCase, handle) as T
    }

}