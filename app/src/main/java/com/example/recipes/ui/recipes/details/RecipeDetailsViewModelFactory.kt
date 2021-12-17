package com.example.recipes.ui.recipes.details

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.business.usecases.GetRecipeUseCase

class RecipeDetailsViewModelFactory(
    private val getRecipeUseCase: GetRecipeUseCase,
    owner: SavedStateRegistryOwner
): AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RecipeDetailsViewModel(getRecipeUseCase, handle) as T
    }
}