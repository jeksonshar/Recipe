package com.example.recipes.presentation.ui.viewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    private val recipeDataStore: RecipeDataStore
) : ViewModel() {

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            recipeDataStore.setNotFirstLaunch()
        }
    }
}