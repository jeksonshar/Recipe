package com.example.recipes.ui.recipes.favoritelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import kotlinx.coroutines.launch

class FavoriteListViewModel(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun getFavoriteRecipes() {
        viewModelScope.launch {
            getFavoriteRecipesUseCase.getRecipesFromRoom()
        }
    }


    companion object {
        const val VIEW_VISIBLE = 0
        const val VIEW_GONE = 8
    }
}