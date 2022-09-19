package com.example.recipes.presentation.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetFavoriteRecipeUseCase
import com.example.recipes.business.usecases.ManageFavoriteRecipeUseCase
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getFavoriteRecipeUseCase: GetFavoriteRecipeUseCase,
    private val manageFavoriteRecipeUseCase: ManageFavoriteRecipeUseCase,
    private val getRecipeUseCase: GetRecipeUseCase
//    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _currentRecipe = MutableLiveData<Recipe>()
    val currentRecipe: LiveData<Recipe> = _currentRecipe

    private val _currentRecipeIngredients = MutableLiveData<List<Ingredient>>()
    val currentRecipeIngredients: LiveData<List<Ingredient>> = _currentRecipeIngredients

    val progressVisibilityLiveData = MutableLiveData<Boolean>()
    val errorMassageLiveData = MutableLiveData<String?>()
    val retryVisibilityLiveData = MutableLiveData<Boolean>()
    var currentRecipeIsFavorite = MutableLiveData(false)

    private val userId = Firebase.auth.currentUser?.uid ?: ""

    fun moveToRecipe(recipeLink: String?) {
        val recipe = RecipeSingleton.recipe
        if (recipe == null && !recipeLink.isNullOrBlank()) {
            val recipeId = recipeLink
                .substringAfter("http://www.edamam.com/recipe/")
                .substringBefore("/")
                .substringAfterLast("-")
            getRecipe(recipeId)
        } else {
            _currentRecipe.value = RecipeSingleton.recipe
            _currentRecipeIngredients.value = recipe?.ingredients
        }
        progressVisibilityLiveData.value = false
        errorMassageLiveData.value = null
        retryVisibilityLiveData.value = false
    }

    fun recipeIsFavorite(uri: String) {
        viewModelScope.launch {
            val favoriteRecipe = getFavoriteRecipeUseCase.getFavoriteRecipe(uri, userId)
            currentRecipeIsFavorite.value = favoriteRecipe != null
        }
    }

    private fun getRecipe(recipeId: String) {
        viewModelScope.launch {
            val recipeEntity = getRecipeUseCase.getRecipe(recipeId)
            val recipe = NetWorkEntitiesMappers.mapToRecipe(recipeEntity.data)
            _currentRecipe.value = recipe
            _currentRecipeIngredients.value = recipe.ingredients
        }
    }

    fun saveOrDeleteRecipeToFavorite() {
        currentRecipeIsFavorite.value = !currentRecipeIsFavorite.value!!
        viewModelScope.launch {
            currentRecipe.value?.let {
                manageFavoriteRecipeUseCase.manageRecipe(it, userId)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        RecipeSingleton.clear()
        BackPressedSingleton.clear()
    }
}