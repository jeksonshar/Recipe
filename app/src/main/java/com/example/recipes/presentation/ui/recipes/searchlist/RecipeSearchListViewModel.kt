package com.example.recipes.presentation.ui.recipes.searchlist

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.*
import androidx.paging.*
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.business.domain.singletons.SearchEnteredSingleton
import com.example.recipes.business.usecases.SaveInFileCacheLoadRecipesUseCaseImpl
import com.example.recipes.business.usecases.SendEventToAnalyticsUseCaseImpl
import com.example.recipes.business.usecases.interfaces.CheckConnectionUseCase
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.presentation.ui.recipes.searchlist.enums.CuisineTypes
import com.example.recipes.presentation.ui.recipes.searchlist.enums.Diet
import com.example.recipes.presentation.ui.recipes.searchlist.enums.Health
import com.example.recipes.presentation.ui.recipes.searchlist.enums.MealTypes
import com.example.recipes.presentation.ui.recipes.searchlist.paging.RecipesPagingSource
import com.example.recipes.presentation.utils.LoginUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RecipeSearchListViewModel @Inject constructor(
    private val apiService: RecipesApiService,
    private val recipeDataStore: RecipeDataStore,
    private val saveInFileCacheLoadRecipesUseCase: SaveInFileCacheLoadRecipesUseCaseImpl,
    checkConnectionUseCase: CheckConnectionUseCase,
    private val sendEventToAnalyticsUseCase: SendEventToAnalyticsUseCaseImpl
//    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val queryHandler = recipeDataStore.getLastQuery().asLiveData()
    val isNetConnected = checkConnectionUseCase.isConnected().asLiveData()

    private var _loadState = MutableLiveData<CombinedLoadStates?>()
    val loadState: LiveData<CombinedLoadStates?> = _loadState

    private var _isEmptyListImageViewVisible = MutableLiveData<Boolean>()
    val isEmptyListImageViewVisible: LiveData<Boolean> = _isEmptyListImageViewVisible

    private var _isProgressBarWhileListEmptyVisible = MutableLiveData<Boolean>()
    val isProgressBarWhileListEmptyVisible: LiveData<Boolean> = _isProgressBarWhileListEmptyVisible

    private val _isRefreshingProgressBarVisible = MutableLiveData<Boolean>()
    val isRefreshingProgressBarVisible: LiveData<Boolean> = _isRefreshingProgressBarVisible

    private val _filterIsOpened = MutableLiveData(false)
    val filterIsOpened: LiveData<Boolean> = _filterIsOpened

    // for filters CuisineType
    val filterListForCuisineTypes = buildList { enumValues<CuisineTypes>().forEach { cuisineTypes -> add(cuisineTypes) } }
    private val _clickedCuisineTypesChip = MutableLiveData<MutableList<CuisineTypes>>(mutableListOf())
    val clickedCuisineTypesChip: LiveData<MutableList<CuisineTypes>> = _clickedCuisineTypesChip
    private val filterListCuisineType = arrayListOf<String>()
    val listenerCuisineTypeSelection = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        filterListForCuisineTypes.find { it.getValue() == buttonView.tag }?.let { cuisineTypes ->
            if (isChecked) {
                _clickedCuisineTypesChip.value?.add(cuisineTypes)
                _clickedCuisineTypesChip.value = clickedCuisineTypesChip.value
                filterListCuisineType.add(cuisineTypes.getValue())
            } else {
                _clickedCuisineTypesChip.value?.remove(cuisineTypes)
                _clickedCuisineTypesChip.value = clickedCuisineTypesChip.value
                filterListCuisineType.remove(cuisineTypes.getValue())
            }
        }
    }

    // for filters Diet
    val filterListForDiet = buildList { enumValues<Diet>().forEach { diet -> add(diet) } }
    private val _clickedDietChip = MutableLiveData<MutableList<Diet>>(mutableListOf())
    val clickedDietChip: LiveData<MutableList<Diet>> = _clickedDietChip
    private val filterListDiet = arrayListOf<String>()
    val listenerDietSelection = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        filterListForDiet.find { it.getValue() == buttonView.tag }?.let { diet ->
            if (isChecked) {
                _clickedDietChip.value?.add(diet)
                _clickedDietChip.value = clickedDietChip.value
                filterListDiet.add(diet.getValue())
            } else {
                _clickedDietChip.value?.remove(diet)
                _clickedDietChip.value = clickedDietChip.value
                filterListDiet.remove(diet.getValue())
            }
        }
    }

    // for filters Health
    val filterListForHealth = buildList { enumValues<Health>().forEach { health -> add(health) } }
    private val _clickedHealthChip = MutableLiveData<MutableList<Health>>(mutableListOf())
    val clickedHealthChip: LiveData<MutableList<Health>> = _clickedHealthChip
    private val filterListHealth = arrayListOf<String>()
    val listenerHealthSelection = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        filterListForHealth.find { it.getValue() == buttonView.tag }?.let { health ->
            if (isChecked) {
                _clickedHealthChip.value?.add(health)
                _clickedHealthChip.value = clickedHealthChip.value
                filterListHealth.add(health.getValue())
            } else {
                _clickedHealthChip.value?.remove(health)
                _clickedHealthChip.value = clickedHealthChip.value
                filterListHealth.remove(health.getValue())
            }
        }
    }

    // for filters MealTypes
    val filterListForMealTypes = buildList { enumValues<MealTypes>().forEach { mealTypes -> add(mealTypes) } }
    private val _clickedMealTypesChip = MutableLiveData<MutableList<MealTypes>>(mutableListOf())
    val clickedMealTypesChip: LiveData<MutableList<MealTypes>> = _clickedMealTypesChip
    private val filterListMealTypes = arrayListOf<String>()
    val listenerMealTypesSelection = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        filterListForMealTypes.find { it.getValue() == buttonView.tag }?.let { mealTypes ->
            if (isChecked) {
                _clickedMealTypesChip.value?.add(mealTypes)
                _clickedMealTypesChip.value = clickedMealTypesChip.value
                filterListMealTypes.add(mealTypes.getValue())
            } else {
                _clickedMealTypesChip.value?.remove(mealTypes)
                _clickedMealTypesChip.value = clickedMealTypesChip.value
                filterListMealTypes.remove(mealTypes.getValue())
            }
        }
    }

    private val fileName = MutableLiveData<String>()

    fun loadRecipes(query: String): Flow<PagingData<Recipe>> {
        return newPager(query).flow.cachedIn(viewModelScope)
    }

    private fun newPager(query: String): Pager<String, Recipe> {
        val pagingConfig = PagingConfig(
            pageSize = 10,
            enablePlaceholders = true,
        )
        return Pager(
            pagingConfig,
            pagingSourceFactory = {
                RecipesPagingSource(
                    apiService,
                    saveInFileCacheLoadRecipesUseCase,
                    fileName.value ?: "",
                    filterListDiet,
                    filterListHealth,
                    filterListCuisineType,
                    filterListMealTypes,
                    fetchQuery = { query }
                )
            }
        )
    }

    fun setLoadState(state: CombinedLoadStates) {
        _loadState.value = state
    }

    fun setFilterIsOpened(value: Boolean) {
        _filterIsOpened.value = value
    }

    fun setEmptyListImageViewVisibility(value: Boolean) {
        _isEmptyListImageViewVisible.value = value
    }

    fun setProgressBarWhileListEmptyVisibility(value: Boolean) {
        _isProgressBarWhileListEmptyVisible.value = value
    }

    fun setRefreshingProgressVisibility(value: Boolean) {
        _isRefreshingProgressBarVisible.value = value
    }

    fun changeFilterVisibility() {
        _filterIsOpened.value = filterIsOpened.value != true
    }

    fun setQueryToDatastore(query: String) {
        viewModelScope.launch {
            recipeDataStore.setLastQuery(query)
        }
    }

    fun setRecipeToSingleton(recipe: Recipe) {
        RecipeSingleton.recipe = recipe
    }

    fun setSearchEntered() {
        SearchEnteredSingleton.isSearchEntered = true
    }

    fun clearSearchEntered() {
        SearchEnteredSingleton.isSearchEntered = false
    }

    fun getFromFileCacheOfLoadRecipes(fileName: String): List<Recipe> {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val recipeList: List<Recipe> = if (File(fileName).exists()) {
            gson.fromJson(File(fileName).readText(), Array<Recipe>::class.java).toList()
        } else {
            emptyList()
        }
        LoginUtil.logD("TAG", "getFromFileCacheOfLoadRecipes: ", recipeList.toString())
        return recipeList
    }

    fun setFileName(name: String) {
        fileName.value = name
    }

    fun sendEventToAnalytics(query: String?) {
        val bundle = Bundle()
        bundle.putString(REQUEST_NAME_ANALYTICS_PARAMETER, query)

        sendEventToAnalyticsUseCase.sendEventToAnalytics(SEARCH_EVENT_NAME, bundle)
    }

    companion object {
        const val REQUEST_NAME_ANALYTICS_PARAMETER = "request_name"

        const val SEARCH_EVENT_NAME = FirebaseAnalytics.Event.SEARCH
    }

}