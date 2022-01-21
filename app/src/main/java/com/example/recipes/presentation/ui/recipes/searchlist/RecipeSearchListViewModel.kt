package com.example.recipes.presentation.ui.recipes.searchlist

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetRecipesBySearchUseCase
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.presentation.ui.recipes.BackPressedSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSearchListViewModel @Inject constructor(
    private val getRecipesBySearchUseCase: GetRecipesBySearchUseCase,
    private val recipeDataStore: RecipeDataStore,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchIsOpened = MutableLiveData<Int>()

    val queryHandler = recipeDataStore.getLastQuery()
    private var newPagingSource: PagingSource<*, *>? = null

    fun recipes(query: String?): Flow<PagingData<Recipe>>? {
        return if (query != null) {
            queryHandler
                .map(::newPager)
                .flatMapLatest { pager -> pager!!.flow }
                .cachedIn(viewModelScope)
        } else {
            null
        }
    }

    private fun newPager(query: String?): Pager<String, Recipe>? {
        return if (query != null) {
            Pager(PagingConfig(pageSize = 3)) {
                getRecipesBySearchUseCase.invoke(query).also {
                    newPagingSource = it
                }
            }
        } else {
            null
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

    fun setRecipeToSingleton(recipe: Recipe) {
        RecipeSingleton.recipe = recipe
    }

    override fun onCleared() {
        BackPressedSingleton.clear()
        super.onCleared()
    }

    companion object {
        const val VIEW_VISIBLE = 0
        const val VIEW_GONE = 8
    }
}