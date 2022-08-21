package com.example.recipes.presentation.utils

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.paging.LoadState
import com.example.recipes.R
import com.example.recipes.presentation.ui.recipes.searchlist.PagingSourceException
import com.google.android.material.imageview.ShapeableImageView

object BindingAdapters {

    @BindingAdapter("app:loadImageRecipeItem")
    @JvmStatic fun loadImageRecipeItem(view: ShapeableImageView, url: String) {
        ImagesUtil.setImage(url, view)
    }

//    @BindingAdapter("app:setLoadState")
//    @JvmStatic fun setLoadState(view: ConstraintLayout, loadState: LoadState) {
//        if (loadState is LoadState.Error) {
//            when (loadState.error) {
//                is PagingSourceException.Response429Exception -> {
//                    tvErrorLoading.text = view.context.getText(R.string.too_fast)
//                }
//                is PagingSourceException.EndOfListException -> {
//                    progressBarPaging.visibility = setVisibility(false)
//                    tvErrorLoading.visibility = setVisibility(false)
//                    buttonRetry.visibility = setVisibility(false)
//                    return
//                }
//                else -> {
//                    tvErrorLoading.text = loadState.error.message
//                }
//            }
//        }
//    }
//    private fun setVisibility(value: Boolean): Int = if (value) {
//        View.VISIBLE
//    } else {
//        View.GONE
//    }
}