package com.example.recipes.presentation.ui.recipes.details

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.business.usecases.GetFavoriteRecipeUseCase
import com.example.recipes.business.usecases.ManageFavoriteRecipeUseCase

@Suppress("UNCHECKED_CAST")
class RecipeDetailsViewModelFactory(
    private val getFavoriteRecipeUseCase: GetFavoriteRecipeUseCase,
    private val manageFavoriteRecipeUseCase: ManageFavoriteRecipeUseCase,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RecipeDetailsViewModel(getFavoriteRecipeUseCase, manageFavoriteRecipeUseCase, handle) as T
    }
}