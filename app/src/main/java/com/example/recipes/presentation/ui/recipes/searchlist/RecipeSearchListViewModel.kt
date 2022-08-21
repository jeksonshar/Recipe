package com.example.recipes.presentation.ui.recipes.searchlist

import android.text.Editable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.*
import androidx.paging.*
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.usecases.GetRecipesBySearchUseCase
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.presentation.ui.recipes.searchlist.paging.RecipesPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeSearchListViewModel @Inject constructor(
//    private val getRecipesBySearchUseCase: GetRecipesBySearchUseCase,
    private val apiService: RecipesApiService,
    private val recipeDataStore: RecipeDataStore,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val queryHandler = recipeDataStore.getLastQuery().asLiveData()

    val loadState = MutableLiveData<CombinedLoadStates?>()
    val searchIsOpened = MutableLiveData<Boolean>()

    val isEmptyListImageViewVisible = MutableLiveData<Boolean>()
    val isProgressBarWhileListEmptyVisible = MutableLiveData<Boolean>()
//    val isErrorLoadingVisible = MutableLiveData<Boolean>()
//    val isButtonRetryVisible = MutableLiveData<Boolean>()
//    val isProgressBarPagingVisible = MutableLiveData<Boolean>()
//    val loadingError = MutableLiveData<String>()

    fun loadRecipes(query: String?): Flow<PagingData<Recipe>> {
        setQueryToDatastore(query)
        return newPager().flow.cachedIn(viewModelScope)
    }

    private fun newPager(): Pager<String, Recipe> {
        val pagingConfig = PagingConfig(
            pageSize = 3,
//            enablePlaceholders = true,
        )
        return Pager(
            pagingConfig,
            pagingSourceFactory = {
                RecipesPagingSource(
                    apiService,
                    fetchQuery = { queryHandler.value ?: "" }
                )
            }
        )
    }

    fun resetLastQuery() {
        viewModelScope.launch {
            setQueryToDatastore("")
        }
    }

    private fun setQueryToDatastore(query: String?) {
        viewModelScope.launch {
            recipeDataStore.setLastQuery(query ?: "")
        }
    }

    fun changeSearchIsOpenedValue() {
        searchIsOpened.value = searchIsOpened.value != true
    }

    fun setRecipeToSingleton(recipe: Recipe) {
        RecipeSingleton.recipe = recipe
    }

    override fun onCleared() {
        super.onCleared()
        BackPressedSingleton.clear()
    }

    companion object {
        const val VIEW_VISIBLE = 0
        const val VIEW_GONE = 8
    }
}