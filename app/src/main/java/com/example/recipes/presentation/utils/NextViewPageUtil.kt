package com.example.recipes.presentation.utils

import androidx.viewpager2.widget.ViewPager2

object NextViewPageUtil {

    fun ViewPager2.onNextPageClick(slidesSize: Int, move: () -> Unit) {
        val nextPage = this.currentItem + 1
        if (nextPage < slidesSize) {
            this.currentItem = nextPage
        } else {
            move.invoke()
        }
    }
}