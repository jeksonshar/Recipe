package com.example.recipes.presentation.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetFavoriteRecipeUseCase
import com.example.recipes.business.usecases.ManageFavoriteRecipeUseCase
import com.example.recipes.presentation.ui.recipes.BackPressedSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getFavoriteRecipeUseCase: GetFavoriteRecipeUseCase,
    private val manageFavoriteRecipeUseCase: ManageFavoriteRecipeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _currentRecipe = MutableLiveData<Recipe>()
    val currentRecipe: LiveData<Recipe> = _currentRecipe

    private val _currentRecipeIngredients = MutableLiveData<List<Ingredient>>()
    val currentRecipeIngredients: LiveData<List<Ingredient>> = _currentRecipeIngredients

    val progressVisibilityLiveData = MutableLiveData<Boolean>()
    val errorMassageLiveData = MutableLiveData<String?>()
    val retryVisibilityLiveData = MutableLiveData<Boolean>()
    var currentRecipeIsFavorite = MutableLiveData(false)

    fun moveToRecipe() {
        val recipe = RecipeSingleton.recipe
        _currentRecipe.value = RecipeSingleton.recipe
        _currentRecipeIngredients.value = recipe?.ingredients
        progressVisibilityLiveData.value = false
        errorMassageLiveData.value = null
        retryVisibilityLiveData.value = false
    }

    fun recipeIsFavorite(uri: String) {
        viewModelScope.launch {
            val favoriteRecipe = getFavoriteRecipeUseCase.getFavoriteRecipe(uri)
            if (favoriteRecipe != null) {
                currentRecipeIsFavorite.value = true
            }
        }
    }

    fun saveOrDeleteRecipeToFavorite() {
        viewModelScope.launch {
            currentRecipe.value?.let { manageFavoriteRecipeUseCase.manageRecipe(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        RecipeSingleton.clear()
        BackPressedSingleton.clear()
    }
}