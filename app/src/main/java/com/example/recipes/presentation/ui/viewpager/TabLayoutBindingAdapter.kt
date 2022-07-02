package com.example.recipes.presentation.ui.viewpager

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

object TabLayoutBindingAdapter {

    @JvmStatic
    @BindingAdapter("viewPager")
    fun TabLayout.setViewPager(viewPager: ViewPager2) {
        viewPager.viewTreeObserver?.addOnGlobalLayoutListener {
            TabLayoutMediator(this@setViewPager, viewPager) { _, _ ->
            }.attach()
        }
    }
}