package com.example.recipes.presentation.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetFavoriteRecipeUseCase
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.usecases.GetRecipeUseCase
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val getFavoriteRecipeUseCase: GetFavoriteRecipeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //    private val id = savedStateHandle.get<String>(RECIPE_KEY)
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
        errorMassageLiveData.value = ""
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

    override fun onCleared() {
        super.onCleared()
        RecipeSingleton.clear()
    }
}