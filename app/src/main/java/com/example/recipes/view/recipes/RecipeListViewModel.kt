package com.example.recipes.view.recipes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.ConverterModels
import com.example.recipes.data.Recipe
import com.example.recipes.db.network.RetrofitModule
import com.example.recipes.db.network.entities.RecipeSearchEntity
import kotlinx.coroutines.launch

class RecipeListViewModel : ViewModel() {

    var recipesRequest = MutableLiveData<RecipeSearchEntity>()
    val showApdateProgress = MutableLiveData<Boolean>()

    fun initRequest() {
        viewModelScope.launch {
            showApdateProgress.value = true
            val request = RetrofitModule.recipesApi.getRecipesByQuery("chicken")
            Log.d("TAG", "request: $request")
            recipesRequest.value = request
            showApdateProgress.value = false
        }
    }

    fun entityToData(entity: RecipeSearchEntity): List<Recipe> {
        return ConverterModels.convertToRecipes(entity)
    }
}