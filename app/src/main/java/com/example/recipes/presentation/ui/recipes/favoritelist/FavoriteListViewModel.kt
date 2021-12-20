package com.example.recipes.presentation.ui.recipes.favoritelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.domain.models.Recipe
import kotlinx.coroutines.launch

class FavoriteListViewModel(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val favoriteRecipes = MutableLiveData<List<Recipe>>()

    fun getFavoriteRecipes() {
        viewModelScope.launch {
            favoriteRecipes.value = getFavoriteRecipesUseCase.getRecipesFromRoom()
        }
    }


    companion object {
        const val VIEW_VISIBLE = 0
        const val VIEW_GONE = 8
    }
}