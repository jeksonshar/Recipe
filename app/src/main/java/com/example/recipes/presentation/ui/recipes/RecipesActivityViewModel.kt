package com.example.recipes.presentation.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipesActivityViewModel @Inject constructor() : ViewModel() {

    private val _isBtnBackVisible = MutableLiveData<Boolean>()
    val isBtnBackVisible: LiveData<Boolean> = _isBtnBackVisible

    private val _titleOfListText = MutableLiveData<Int>()
    val titleOfListText: LiveData<Int> = _titleOfListText

    private val _searchIconResource = MutableLiveData<Int>()
    val searchIconResource: LiveData<Int> = _searchIconResource

    private val _isSearchIconVisible = MutableLiveData<Boolean>()
    val isSearchIconVisible: LiveData<Boolean> = _isSearchIconVisible

    // использую во фрагментах, т.к. зависит от состояний их полей. Удалю позже
    private val _isSearchOpen = MutableLiveData<Boolean>()
    val isSearchOpen: LiveData<Boolean> = _isSearchOpen


    fun setBtnBackVisibility(value: Boolean) {
        _isBtnBackVisible.value = value
    }

    fun setTitleOfListText(value: Int) {
        _titleOfListText.value = value
    }

    fun setIsOpenSearchETImage(value: Int) {
        _searchIconResource.value = value
    }

    fun setOpenSearchETVisibility(value: Boolean) {
        _isSearchIconVisible.value = value
    }

}