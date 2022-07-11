package com.example.recipes.presentation.ui.viewpager

import android.view.ViewTreeObserver
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

object TabLayoutBindingAdapter {

    @JvmStatic
    @BindingAdapter("setViewPager")
    fun TabLayout.setViewPager(viewPager: ViewPager2) {
//        viewPager.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                TabLayoutMediator(this@setViewPager, viewPager) { _, _ -> }.attach()
//                viewPager.viewTreeObserver?.removeOnGlobalLayoutListener(this)
//            }
//        })
        viewPager.post {
            TabLayoutMediator(this@setViewPager, viewPager) { _, _ -> }.attach()
        }
    }
}