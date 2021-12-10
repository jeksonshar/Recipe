package com.example.recipes.ui.recipes

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.*
import com.example.recipes.data.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class RecipeListViewModel(
    private val recipesUseCase: RecipesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchIsOpened = MutableLiveData<Int>()
    private val _queryHandler = MutableStateFlow("chicken")
    private val queryHandler: StateFlow<String> = _queryHandler
    private var newPagingSource: PagingSource<*, *>? = null

    val recipes: Flow<PagingData<Recipe>> = queryHandler
        .map(::newPager)
        .flatMapLatest { pager -> pager.flow  }
        .cachedIn(viewModelScope)

    private fun newPager(query: String): Pager<String, Recipe> {
        return Pager(PagingConfig(pageSize = 3)) {
            recipesUseCase.invoke(query).also {
                newPagingSource = it
            }
        }
    }

    fun liveSearchByQuery(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query[query.length - 1] == ' ' && query.length > 2) {
                _queryHandler.tryEmit(query.toString())
            }
        }
    }

    fun searchByTouch(query: Editable?) {
        if (query != null && query.isNotEmpty()) {
            if (query.length > 1) {
                _queryHandler.tryEmit(query.toString())
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