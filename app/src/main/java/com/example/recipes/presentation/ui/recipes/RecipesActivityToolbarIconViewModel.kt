package com.example.recipes.presentation.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipesActivityToolbarIconViewModel @Inject constructor(): ViewModel() {

    private var _searchIsOpened = MutableLiveData<Boolean>()
    val searchIsOpened: LiveData<Boolean> = _searchIsOpened


    fun setSearchIsOpened(value: Boolean) {
        _searchIsOpened.value = value
    }

    fun changeSearchIsOpenedValue() {
        _searchIsOpened.value = searchIsOpened.value != true
    }
}