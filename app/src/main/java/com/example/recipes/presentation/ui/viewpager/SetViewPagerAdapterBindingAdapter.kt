package com.example.recipes.presentation.ui.viewpager

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

object SetViewPagerAdapterBindingAdapter {

    @JvmStatic
    @BindingAdapter("setViewPagerAdapter")
    fun ViewPager2.setAdapter(view: View) {                                         // не пойму как использовать в xml
        this.adapter = ViewPagerAdapter(ViewPagerViewModel.slides)
    }
}