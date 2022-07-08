package com.example.recipes.presentation.ui.viewpager

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.example.recipes.R
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    private val recipeDataStore: RecipeDataStore,
    application: Application
) : BaseViewModel(application) {

    companion object {
        val slides = intArrayOf(
            R.layout.slide_one,
            R.layout.slide_two,
            R.layout.slide_three,
            R.layout.slide_four
        )
    }

    val isLastDotPosition = MutableLiveData(false)
    val isMovingToRecipe = MutableLiveData(false)

    fun setVisibility(position: Int) {
        isLastDotPosition.value = position == slides.size - 1
    }

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            recipeDataStore.setNotFirstLaunch()
        }
    }

    fun skipClick() {
        moveToRecipe()
    }

//    fun onNextPageClick(viewPager: ViewPager2) {
//        val nextPage = viewPager.currentItem + 1
//        if (nextPage < slides.size) {
//            viewPager.currentItem = nextPage
//        } else {
//            moveToRecipe()
//        }
//    }

    fun moveToRecipe() {
        isMovingToRecipe.value = true
    }

    fun setViewPagerAdapter(): ViewPagerAdapter {   //так норм или лучше через @BindingAdapter? Не разберусь как сделать
        return ViewPagerAdapter(slides)
    }

}