package com.example.recipes.ui.recipes.list

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.*
import com.example.recipes.business.usecases.GetRecipeListUseCase
import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.RecipeDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val getRecipeListUseCase: GetRecipeListUseCase,
    private val recipeDataStore: RecipeDataStore,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchIsOpened = MutableLiveData<Int>()

    private val queryHandler = recipeDataStore.getLastQuery()
    private var newPagingSource: PagingSource<*, *>? = null

    val recipes: Flow<PagingData<Recipe>> = queryHandler
        .map(::newPager)
        .flatMapLatest { pager -> pager.flow }
        .cachedIn(viewModelScope)

    private fun newPager(query: String): Pager<String, Recipe> {
        return Pager(PagingConfig(pageSize = 3)) {
            getRecipeListUseCase.invoke(query).also {
                newPagingSource = it
            }
        }
    }

    fun liveSearchByQuery(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query[query.length - 1] == ' ' && query.length > 2) {
                viewModelScope.launch {
                    recipeDataStore.setLastQuery(query.toString())
                }
            }
        }
    }

    fun searchByTouch(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query.length > 1) {
                viewModelScope.launch {
                    recipeDataStore.setLastQuery(query.toString())
                }
            }
        }
    }

    fun changeSearchIsOpenedValue(currentVisibility: Int) {
        if (currentVisibility == VIEW_VISIBLE) {
            searchIsOpened.value = VIEW_GONE
        } else {
            searchIsOpened.value = VIEW_VISIBLE
        }
    }

    companion object {
        const val VIEW_VISIBLE = 0
        const val VIEW_GONE = 8
    }
}