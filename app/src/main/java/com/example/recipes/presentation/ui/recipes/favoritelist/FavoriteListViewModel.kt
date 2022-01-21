package com.example.recipes.presentation.ui.recipes.favoritelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.presentation.ui.recipes.BackPressedSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val favoriteRecipes = MutableLiveData<List<Recipe>>()

    fun getFavoriteRecipes() {
        viewModelScope.launch {
            favoriteRecipes.value = getFavoriteRecipesUseCase.getRecipesFromRoom()
        }
    }

    fun setRecipeToSingleton(recipe: Recipe) {
        RecipeSingleton.recipe = recipe
    }

    override fun onCleared() {
        super.onCleared()
        BackPressedSingleton.clear()
    }
}