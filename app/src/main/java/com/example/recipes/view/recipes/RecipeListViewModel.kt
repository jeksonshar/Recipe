package com.example.recipes.view.recipes

import android.text.Editable
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
    val showUpdateProgress = MutableLiveData<Boolean>()
    val searchIsOpened = MutableLiveData<Int>()
    val queryLiveData = MutableLiveData("chicken")

    init {
        viewModelScope.launch {
            showUpdateProgress.value = true
            val request = RetrofitModule.RECIPES_API_SERVICE.getRecipesByQuery(queryLiveData.value ?: "")
            Log.d("TAG", "request: $request")
            recipesRequest.value = request
            showUpdateProgress.value = false
        }
    }

    fun liveSearchByQuery(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query[query.length - 1] == ' ' && query.length > 2) {
                makeRequest(query.toString())
            }
        }
    }

    fun searchByTouch(query: Editable?) {
        if (query != null && query.length > 1) {
            makeRequest(query.toString())
        }
    }

    private fun makeRequest(query: String) {
        queryLiveData.value = query
        viewModelScope.launch {
            recipesRequest.value = RetrofitModule.RECIPES_API_SERVICE.getRecipesByQuery(query)
        }
    }

    fun makeNextRequest() {
        val href = ConverterModels.getHrefNextRecipes((recipesRequest.value ?: RecipeSearchEntity()))
        val contID = href.substringAfter("_cont=").substringBefore("&")
        viewModelScope.launch {
            recipesRequest.value = RetrofitModule.RECIPES_API_SERVICE
                .getNextRecipesByQuery(queryLiveData.value ?: "", contID).body()
        }
    }

    fun entityToData(entity: RecipeSearchEntity): List<Recipe> {
        return ConverterModels.convertToRecipes(entity)
    }

    fun changeSearchIsOpenedValue(currentVisibility: Int) {
        if (currentVisibility == 0) {
            searchIsOpened.value = 8
        } else {
            searchIsOpened.value = 0
        }

    }
}