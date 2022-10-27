package com.example.recipes.presentation.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _snackBarText = MutableLiveData<Int>()
    val snackBarText: LiveData<Int> = _snackBarText

    fun showErrorMessage(@StringRes stringId: Int) {
        _snackBarText.postValue(stringId)
    }

}