package com.example.recipes.presentation.ui.recipes.favoritelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.usecases.SynchronizeFavoriteRecipesUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    private val synchronizedFavoriteRecipesUseCase: SynchronizeFavoriteRecipesUseCase,
//    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> = _favoriteRecipes

    private val userId = Firebase.auth.currentUser?.uid ?: ""

    fun getUserFavoriteRecipes() {
        viewModelScope.launch {
            _favoriteRecipes.value = getFavoriteRecipesUseCase.getUserFavoriteRecipesFromRoom(userId)
            synchronizeFavoriteRecipesWithFirebase()
        }
    }

    private fun synchronizeFavoriteRecipesWithFirebase() {
        var favoriteRecipesFirebase: MutableList<Recipe>
        val favoriteFirebaseRecipesRef = Firebase.database
            .getReference("favorite").child("favorite_recipes")
        val gson = GsonBuilder()/*.disableHtmlEscaping()*/.create()
        favoriteFirebaseRecipesRef.get().addOnCompleteListener {
            if (it.isSuccessful && it.result.value != null) {
                favoriteRecipesFirebase = arrayListOf()
                val result: HashMap<String, String> = it.result.value as HashMap<String, String>
                    result.forEach { (_, recipeString) ->
                        val recipe = gson.fromJson(recipeString, Recipe::class.java)
                        favoriteRecipesFirebase.add(recipe)
                    }
                Log.d("TAG111", "favorites 5 всего перед синхронизаией: ${favoriteRecipesFirebase.size}")
                viewModelScope.launch {
                    val favoriteRecipesSynchronized = synchronizedFavoriteRecipesUseCase
                        .synchronizeFavoriteRecipesWithFirebase(favoriteRecipesFirebase, userId)

// oldRecipeList != newRecipeList - проверка, которая не дает обновлять список, если старое и новое значение совпадают
                    val oldRecipeList: ArrayList<String> = arrayListOf()
                    favoriteRecipes.value?.forEach { recipe ->
                        oldRecipeList.add(recipe.label)
                    }
                    val newRecipeList: ArrayList<String> = arrayListOf()
                    favoriteRecipesSynchronized.forEach { recipe ->
                        newRecipeList.add(recipe.label)
                    }

                    if ( favoriteRecipesSynchronized.isNotEmpty() && oldRecipeList != newRecipeList ) {
                        _favoriteRecipes.value = favoriteRecipesSynchronized
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