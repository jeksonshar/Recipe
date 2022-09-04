package com.example.recipes.presentation.ui.recipes.searchlist

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.RecipeSingleton
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.presentation.ui.recipes.searchlist.enums.CuisineTypes
import com.example.recipes.presentation.ui.recipes.searchlist.enums.Diet
import com.example.recipes.presentation.ui.recipes.searchlist.enums.Health
import com.example.recipes.presentation.ui.recipes.searchlist.enums.MealTypes
import com.example.recipes.presentation.ui.recipes.searchlist.paging.RecipesPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSearchListViewModel @Inject constructor(
//    private val getRecipesBySearchUseCase: GetRecipesBySearchUseCase,
    private val apiService: RecipesApiService,
    private val recipeDataStore: RecipeDataStore,
//    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val queryHandler = recipeDataStore.getLastQuery().asLiveData()

    val loadState = MutableLiveData<CombinedLoadStates?>()
    val searchIsOpened = MutableLiveData<Boolean>()

    private val _filterIsOpen = MutableLiveData(false)
    val filterIsOpen: LiveData<Boolean> = _filterIsOpen

    val isEmptyListImageViewVisible = MutableLiveData<Boolean>()
    val isProgressBarWhileListEmptyVisible = MutableLiveData<Boolean>()

    private val filtersDiet = arrayListOf<String>()
    private val filtersHealth = arrayListOf<String>()
    private val filtersCuisineType = arrayListOf<String>()
    private val filtersMealType = arrayListOf<String>()

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
                    filtersDiet,
                    filtersHealth,
                    filtersCuisineType,
                    filtersMealType,
                    fetchQuery = { queryHandler.value ?: "" }
                )
            }
        )
    }

    fun changeFilterVisibility() {
        Log.d("TAG", "changeFilterVisibility: 1111111111111")
        _filterIsOpen.value = filterIsOpen.value != true
    }

    fun changeCheckedStateDietFilter(chipId: Int, isChecked: Boolean) {
        var value = ""
        when (chipId) {
            R.id.chipBalanced -> value = Diet.BALANCED.value
            R.id.chipHighFiber -> value = Diet.HIGH_FIBER.value
            R.id.chipHighProtein -> value = Diet.HIGH_PROTEIN.value
            R.id.chipLowCarb -> value = Diet.LOW_CARB.value
            R.id.chipLowFat -> value = Diet.LOW_FAT.value
            R.id.chipLowSodium -> value = Diet.LOW_SODIUM.value
        }
        if (isChecked) {
            filtersDiet.add(value)
        } else {
            filtersDiet.remove(value)
        }
    }

    fun changeCheckedStateHealthFilter(chipId: Int, isChecked: Boolean) {
        var value = ""
        when (chipId) {
            R.id.chipAlcoholCocktail -> value = Health.ALCOHOL_COCKTAIL.value
            R.id.chipAlcoholFree -> value = Health.ALCOHOL_FREE.value
            R.id.chipCeleryFree -> value = Health.CELERY_FREE.value
            R.id.chipCrustceanFree -> value = Health.CRUSTACEAN_FREE.value
            R.id.chipDairyFree -> value = Health.DAIRY_FREE.value
            R.id.chipEggFree -> value = Health.EGG_FREE.value
            R.id.chipFishFree -> value = Health.FISH_FREE.value
            R.id.chipGlutenFree -> value = Health.GLUTEN_FREE.value
            R.id.chipImmunoSupportive -> value = Health.IMMUNE_SUPPORTIVE.value
            R.id.chipKosher -> value = Health.KOSHER.value
            R.id.chipLowSugar -> value = Health.LOW_SUGAR.value
            R.id.chipMolluskFree -> value = Health.MOLLUSK_FREE.value
            R.id.chipVegetarian -> value = Health.VEGETARIAN.value
        }
        if (isChecked) {
            filtersHealth.add(value)
        } else {
            filtersHealth.remove(value)
        }
    }

    fun changeCheckedStateCuisineTypeFilter(chipId: Int, isChecked: Boolean) {
        var value = ""
        when (chipId) {
            R.id.chipAmerican -> value = CuisineTypes.AMERICAN.value
            R.id.chipAsian -> value = CuisineTypes.ASIAN.value
            R.id.chipBritish -> value = CuisineTypes.BRITISH.value
            R.id.chipChinese -> value = CuisineTypes.CHINESE.value
            R.id.chipFrench -> value = CuisineTypes.FRENCH.value
            R.id.chipGreek -> value = CuisineTypes.GREEK.value
            R.id.chipIndian -> value = CuisineTypes.INDIAN.value
            R.id.chipItalian -> value = CuisineTypes.ITALIAN.value
            R.id.chipKorean -> value = CuisineTypes.KOREAN.value
            R.id.chipMexican -> value = CuisineTypes.MEXICAN.value
        }
        if (isChecked) {
            filtersCuisineType.add(value)
        } else {
            filtersCuisineType.remove(value)
        }
    }

    fun changeCheckedStateMealTypeFilter(chipId: Int, isChecked: Boolean) {
        var value = ""
        when (chipId) {
            R.id.chipBreakfast -> value = MealTypes.BREAKFAST.value
            R.id.chipBrunch -> value = MealTypes.BRUNCH.value
            R.id.chipLunch -> value = MealTypes.LUNCH.value
            R.id.chipDinner -> value = MealTypes.DINNER.value
            R.id.chipSnack -> value = MealTypes.SNACK.value
            R.id.chipTeatime -> value = MealTypes.TEA_TIME.value
        }
        if (isChecked) {
            filtersMealType.add(value)
        } else {
            filtersMealType.remove(value)
        }
    }

    fun resetLastQuery() {
        viewModelScope.launch {
            setQueryToDatastore("")
        }
    }

    fun setQueryToDatastore(query: String?) {
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

}