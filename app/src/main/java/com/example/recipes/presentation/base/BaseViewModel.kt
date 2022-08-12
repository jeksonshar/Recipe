package com.example.recipes.presentation.base

import android.app.usage.UsageEvents
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _snackBarText = MutableLiveData<String>()
    val snackBarText: LiveData<String> = _snackBarText


    @Composable
    fun showErrorMessage(@StringRes stringId: Int) {
        _snackBarText.postValue(UiText.StringResource(stringId).asString())
    }

}