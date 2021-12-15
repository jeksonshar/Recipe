package com.example.recipes.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.data.Ingredient
import com.example.recipes.data.Recipe
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val useCase: GetRecipeUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _currentRecipe = MutableLiveData<Recipe>()
    val currentRecipe: LiveData<Recipe> = _currentRecipe
    private val _currentRecipeIngredients = MutableLiveData<List<Ingredient>>()
    val currentRecipeIngredients: LiveData<List<Ingredient>> = _currentRecipeIngredients

    fun getRecipe(id: String) {
        viewModelScope.launch {
            useCase.getRecipe(id).also {
                _currentRecipe.value = it
                _currentRecipeIngredients.value = it.ingredients
            }
        }
    }
}