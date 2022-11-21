package com.example.recipes.presentation.ui.recipes.searchlist

import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import androidx.paging.LoadState
import com.example.recipes.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

object BindingAdapters {

    @BindingAdapter("app:setErrorLoading")
    @JvmStatic fun setErrorLoading(view: MaterialTextView, loadState: LoadState) {
        if (loadState is LoadState.Error) {
            when (loadState.error) {
                is PagingSourceException.Response429Exception -> {
                    view.text = view.context.getText(R.string.too_fast)
                }
                is PagingSourceException.EndOfListException -> {
                    view.visibility = setVisibility(false)
                    return
                }
                else -> {
                    view.text = loadState.error.message
                }
            }
        }
        view.visibility = setVisibility(loadState is LoadState.Error)
    }

    @BindingAdapter("app:setProgressBarPaging")
    @JvmStatic fun setProgressBarPaging(view: ContentLoadingProgressBar, loadState: LoadState) {
        if (loadState is LoadState.Error && loadState.error is PagingSourceException.EndOfListException) {
            view.visibility = setVisibility(false)
            return
        }
        view.visibility = setVisibility(loadState is LoadState.Loading)
    }

    @BindingAdapter("app:setButtonRetryVisibility")
    @JvmStatic fun setButtonRetryVisibility(view: MaterialButton, loadState: LoadState) {
        if (loadState is LoadState.Error && loadState.error is PagingSourceException.EndOfListException) {
            view.visibility = setVisibility(false)
            return
        }
        view.visibility = setVisibility(loadState is LoadState.Error)
    }


    private fun setVisibility(value: Boolean): Int = if (value) {
        View.VISIBLE
    } else {
        View.GONE
    }
}