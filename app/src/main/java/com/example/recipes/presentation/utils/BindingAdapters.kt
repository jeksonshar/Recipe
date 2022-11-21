package com.example.recipes.presentation.utils

import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView

object BindingAdapters {

    @BindingAdapter("app:loadImageRecipeItem")
    @JvmStatic
    fun loadImageRecipeItem(view: ShapeableImageView, url: String) {
        ImagesUtil.setImage(url, view)
    }

    @BindingAdapter("app:loadImageProfileItem")
    @JvmStatic
    fun loadImageProfileItem(view: AppCompatImageView, uri: Uri?) {
        if (uri != null) ImagesUtil.setImage(uri, view)
    }

}