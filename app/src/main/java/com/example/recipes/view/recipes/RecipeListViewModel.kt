package com.example.recipes.view.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.recipes.data.Recipe
import com.example.recipes.db.network.loadRecipesByQuery

class RecipeListViewModel : ViewModel() {

    fun loadRecipes(): List<Recipe> = liveData {
            val data = loadRecipesByQuery("chicken")
            emit(data)
        }.value ?: emptyList()


}