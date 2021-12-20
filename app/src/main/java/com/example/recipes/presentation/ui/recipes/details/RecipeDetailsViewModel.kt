package com.example.recipes.presentation.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
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
        val recipe = RecipeDetailsSingleton.recipe
        _currentRecipe.value = RecipeDetailsSingleton.recipe
        _currentRecipeIngredients.value = recipe?.ingredients
        progressVisibilityLiveData.value = false
        errorMassageLiveData.value = ""
        retryVisibilityLiveData.value = false
    }

    fun recipeIsFavorite(getRecipes: GetFavoriteRecipesUseCase) {
        viewModelScope.launch {
            val favoriteRecipes = getRecipes.getRecipesFromRoom()
            for (rec in favoriteRecipes) {
                if (rec.uri == currentRecipe.value?.uri) {
                    currentRecipeIsFavorite.value = true
                    return@launch
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        RecipeDetailsSingleton.clear()
    }
}