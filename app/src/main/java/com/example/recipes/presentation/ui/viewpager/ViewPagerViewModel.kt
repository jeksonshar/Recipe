package com.example.recipes.presentation.ui.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.R
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    private val recipeDataStore: RecipeDataStore,
) : ViewModel() {

    companion object {
        val slides = intArrayOf(
            R.layout.slide_one,
            R.layout.slide_two,
            R.layout.slide_three,
            R.layout.slide_four
        )
    }

    private val _isLastDotPosition = MutableLiveData(false)
    val isLastDotPosition: LiveData<Boolean> = _isLastDotPosition

    private val _isMovingToRecipe = MutableLiveData(false)
    val isMovingToRecipe: LiveData<Boolean> = _isMovingToRecipe

    fun setVisibility(position: Int) {
        _isLastDotPosition.value = position == slides.size - 1
    }

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            recipeDataStore.setNotFirstLaunch()
        }
    }

    fun skipClick() {
        moveToRecipe()
    }

    fun moveToRecipe() {
        _isMovingToRecipe.value = true
    }

    fun setViewPagerAdapter(): ViewPagerAdapter {                           //     лучше тут или через @BindingAdapter?
        return ViewPagerAdapter(slides)
    }

}