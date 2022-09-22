package com.example.recipes.presentation.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipes.R

object ImagesUtil {

    fun setImage(image: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(image)
            .apply(RequestOptions())
            .placeholder(R.drawable.dish_not_loading)
            .into(imageView)
    }

    fun setImage(image: Uri, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(image)
            .apply(RequestOptions())
            .into(imageView)
    }
}