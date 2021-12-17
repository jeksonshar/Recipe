package com.example.recipes.ui.recipes.favoritelist

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase

class FavoriteListViewModelFactory(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    owner: SavedStateRegistryOwner
): AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return FavoriteListViewModel(getFavoriteRecipesUseCase, handle) as T
    }
}