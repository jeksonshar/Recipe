package com.example.recipes.presentation.ui.recipes.viewpager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ViewsSliderViewModel @Inject constructor(
    private val recipeDataStore: RecipeDataStore
) : ViewModel() {

    private val isFirstLaunch = MutableLiveData<Boolean>()

    fun getLaunch(): Boolean {
        val result: Boolean
        if (isFirstLaunch.value == null) {
            result = runBlocking { recipeDataStore.checkNotFistLaunch().first() }
            isFirstLaunch.value = result
        } else {
            result = isFirstLaunch.value ?: false
        }
        return result
    }

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            recipeDataStore.setNotFirstLaunch()
        }
    }
}