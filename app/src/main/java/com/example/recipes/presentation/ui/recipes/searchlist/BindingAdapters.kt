package com.example.recipes.presentation.ui.recipes.searchlist

import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isEmpty
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import androidx.paging.LoadState
import com.example.recipes.R
import com.example.recipes.presentation.ui.recipes.searchlist.enums.FiltersGroups
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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

    @BindingAdapter("inflateChips", "listener", "selectedChips")
    @JvmStatic fun ChipGroup.inflateFiltersGroupsList(
        filtersGroupsList: List<FiltersGroups>?,
        listener: CompoundButton.OnCheckedChangeListener?,
        selectedFiltersGroupsList: List<FiltersGroups>?
    ) {
        if (filtersGroupsList.isNullOrEmpty() || listener == null || !this.isEmpty()) return
        val layoutInflater = LayoutInflater.from(this.context)
        filtersGroupsList.forEach { filtersGroupsItem ->
                val chip = layoutInflater.inflate(R.layout.chip_filter, this, false)
                (chip as Chip).apply {
                    text = filtersGroupsItem.getValue()
                    tag = filtersGroupsItem.getValue()
                    isChecked = selectedFiltersGroupsList?.any { filtersGroupsItem == it } ?: false
                    setOnCheckedChangeListener(listener)
                }
                this.addView(chip)
        }
    }

    private fun setVisibility(value: Boolean): Int = if (value) {
        View.VISIBLE
    } else {
        View.GONE
    }
}