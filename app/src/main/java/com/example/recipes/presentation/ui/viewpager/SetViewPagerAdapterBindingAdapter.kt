package com.example.recipes.presentation.ui.viewpager

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

object SetViewPagerAdapterBindingAdapter {

    @JvmStatic
    @BindingAdapter("setViewPagerAdapter")
    fun ViewPager2.setAdapter(viewModel: ViewPagerViewModel) {
        this.adapter = ViewPagerAdapter(ViewPagerViewModel.slides)
    }
}