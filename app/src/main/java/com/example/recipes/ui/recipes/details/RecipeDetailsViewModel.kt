package com.example.recipes.ui.recipes.details

import androidx.lifecycle.*
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.example.recipes.business.Status
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.data.Ingredient
import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.RecipeDataStore
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val useCase: GetRecipeUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _currentRecipe = MutableLiveData<Recipe>()
    val currentRecipe: LiveData<Recipe> = _currentRecipe
    private val _currentRecipeIngredients = MutableLiveData<List<Ingredient>>()
    val currentRecipeIngredients: LiveData<List<Ingredient>> = _currentRecipeIngredients
    val progressVisibilityLiveData = MutableLiveData<Boolean>()
    val errorMassageLiveData = MutableLiveData<String?>()
    val retryVisibilityLiveData = MutableLiveData<Boolean>()

    fun getRecipe(id: String) {
        viewModelScope.launch {
            useCase.getRecipe(id).also {
                val recipe = NetWorkEntitiesMappers.mapToRecipe(it.data)
                when (it.status) {
                    Status.SUCCESS -> {
                        _currentRecipe.value = recipe
                        _currentRecipeIngredients.value = recipe.ingredients
                        progressVisibilityLiveData.value = false
                        errorMassageLiveData.value = ""
                        retryVisibilityLiveData.value = false
                    }
                    Status.ERROR -> {
                        progressVisibilityLiveData.value = false
                        errorMassageLiveData.value = it.message
                        retryVisibilityLiveData.value = true
                    }
                    else -> {
                        progressVisibilityLiveData.value = true
                        errorMassageLiveData.value = ""
                        retryVisibilityLiveData.value = false
                    }
                }
            }
        }
    }

    fun changeIsFavorite(recipe: Recipe, dataStore: RecipeDataStore) {
        viewModelScope.launch {
            if (!recipe.isFavorite) {
                dataStore.setFavoriteRecipe(recipe)
            } else {
                dataStore.delFavoriteRecipe(recipe)
            }
        }
    }

    fun isContainFavoriteRecipe(recipe: Recipe, dataStore: RecipeDataStore): LiveData<Boolean> {
        return dataStore.isContainFavoriteRecipe(recipe).asLiveData()
    }
}