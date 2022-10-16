package com.example.recipes.presentation.ui.recipes.details

import android.os.Bundle
import androidx.lifecycle.*
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.usecases.*
import com.example.recipes.business.utils.StatesManageFavoriteRecipes
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getFavoriteRecipeUseCase: GetFavoriteRecipeUseCase,
    private val getStateManageFavoriteRecipeUseCase: GetStateManageFavoriteRecipeUseCaseImpl,
    private val saveFavoriteRecipeUseCase: SaveFavoriteRecipeUseCaseImpl,
    private val deleteFavoriteRecipeUseCase: DeleteFavoriteRecipeUseCaseImpl,
    private val getRecipeUseCase: GetRecipeUseCase,
    checkConnectionUseCase: CheckConnectionUseCaseImpl,
    private val sendEventToAnalyticsUseCase: SendEventToAnalyticsUseCaseImpl
//    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val isNetConnected = checkConnectionUseCase.isConnected().asLiveData()

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
        val favoriteFirebaseRecipesRef = Firebase.database
            .getReference("favorite")
            .child("favorite_recipes")
        viewModelScope.launch {
            currentRecipe.value?.let { recipe ->
                when (getStateManageFavoriteRecipeUseCase.getStateManageFavoriteRecipes(recipe, userId)) {
                    StatesManageFavoriteRecipes.SAVE -> {
                        saveFavoriteRecipeUseCase.saveFavoriteRecipe(
                            recipe,
                            userId,
                            favoriteFirebaseRecipesRef
                        )
                        // add event to analytics (first implementation)
                        val bundle = Bundle()
                        bundle.putString(ADDED_RECIPE_NAME_ANALYTICS_PARAMETER, recipe.label)

                        sendEventToAnalytics(ADD_TO_FAVORITE_EVENT_NAME, bundle)
                    }
                    StatesManageFavoriteRecipes.DELETE -> {
                        deleteFavoriteRecipeUseCase.deleteFavoriteRecipe(
                            recipe,
                            userId,
                            favoriteFirebaseRecipesRef
                        )
                        // add event to analytics (second implementation)
                        val bundle = Bundle()
                        bundle.putString(REMOVED_RECIPE_NAME_ANALYTICS_PARAMETER, recipe.label)
                        sendEventToAnalytics(REMOVE_FROM_FAVORITE_EVENT_NAME, bundle)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendEventToAnalytics(eventName: String, bundle: Bundle) {
        sendEventToAnalyticsUseCase.sendEventToAnalytics(eventName, bundle)
    }

    override fun onCleared() {
        super.onCleared()
        RecipeSingleton.clear()
        BackPressedSingleton.clear()
    }

    companion object {
        const val REMOVED_RECIPE_NAME_ANALYTICS_PARAMETER = "removed_favorite_recipe_name"
        const val ADDED_RECIPE_NAME_ANALYTICS_PARAMETER = "added_favorite_recipe_name"

        const val REMOVE_FROM_FAVORITE_EVENT_NAME = "remove_from_favorite"
        const val ADD_TO_FAVORITE_EVENT_NAME = "add_to_favorite"
    }
}