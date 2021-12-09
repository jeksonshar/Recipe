package com.example.recipes.ui.recipes

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipes.data.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeListViewModel(
    private val recipesUseCase: RecipesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //    val recipesRequest = MutableLiveData<RecipeSearchEntity>()
//    val showUpdateProgress = MutableLiveData<Boolean>()
    val searchIsOpened = MutableLiveData<Int>()

    //    val queryLiveData = MutableLiveData("chicken")

    fun recipes(query: String): Flow<PagingData<Recipe>> {
        return Pager(PagingConfig(pageSize = 3)) {
            recipesUseCase.invoke(query)
        }
            .flow
            .cachedIn(viewModelScope)
    }

    fun liveSearchByQuery(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query[query.length - 1] == ' ' && query.length > 2) {
                recipes(query.toString())
            }
        }
    }

    fun changeSearchIsOpenedValue(currentVisibility: Int) {
        if (currentVisibility == 0) {
            searchIsOpened.value = 8
        } else {
            searchIsOpened.value = 0
        }
    }
}