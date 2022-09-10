package com.example.recipes.presentation.ui.recipes.favoritelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.usecases.SynchronizeFavoriteRecipesUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    private val synchronizedFavoriteRecipesUseCase: SynchronizeFavoriteRecipesUseCase,
//    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val favoriteRecipes = MutableLiveData<List<Recipe>>()

    private val userId = Firebase.auth.currentUser?.uid ?: ""

    private val ref = Firebase.database.getReference("favorite")
    private val favoriteFirebaseRecipesRef = ref.child("favorite_recipes")

    fun getUserFavoriteRecipes() {
        viewModelScope.launch {
            favoriteRecipes.value = getFavoriteRecipesUseCase.getUserFavoriteRecipesFromRoom(userId)
            synchronizeFavoriteRecipesWithFirebase()
        }
    }

    private fun synchronizeFavoriteRecipesWithFirebase() {
        var favorites: HashMap<String, List<String>>
        favoriteFirebaseRecipesRef.get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                favorites = hashMapOf()
                val result: HashMap<String, List<String>> = it.result.value as HashMap<String, List<String>>
                result.forEach { (recipeID, listUsersId) ->
                    favorites[recipeID] = listUsersId
                }
                viewModelScope.launch {
                    val favoriteRecipesSynchronized =
                    synchronizedFavoriteRecipesUseCase.synchronizeFavoriteRecipesWithFirebase(favorites, userId)
                    if (favoriteRecipesSynchronized.isNotEmpty()) {
                        favoriteRecipes.value = favoriteRecipesSynchronized
                    }
                }
            }
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