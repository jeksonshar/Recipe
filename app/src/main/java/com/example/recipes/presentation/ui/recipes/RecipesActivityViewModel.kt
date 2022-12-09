package com.example.recipes.presentation.ui.recipes

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.recipes.business.usecases.interfaces.CheckConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipesActivityViewModel @Inject constructor(
    checkConnectionUseCase: CheckConnectionUseCase,
) : ViewModel() {

    val isNetConnectedLiveData = checkConnectionUseCase.isConnected().asLiveData()

    private val _isBtnBackVisible = MutableLiveData<Boolean>()
    val isBtnBackVisible: LiveData<Boolean> = _isBtnBackVisible

    private val _titleOfListText = MutableLiveData<Int>()
    val titleOfListText: LiveData<Int> = _titleOfListText

    private val _isSearchIconVisible = MutableLiveData<Boolean>()
    val isSearchIconVisible: LiveData<Boolean> = _isSearchIconVisible

    private val _currentIconResource = MutableLiveData<Int>()
    val currentIconResource: LiveData<Int> = _currentIconResource

    private val _isBottomNavigationVisible = MutableLiveData<Boolean>()
    val isBottomNavigationVisible: LiveData<Boolean> = _isBottomNavigationVisible

    fun setBottomNavigationVisibility(value: Boolean) {
        _isBottomNavigationVisible.value = value
    }

    fun setCurrentIcon(@DrawableRes value: Int) {
        _currentIconResource.value = value
    }

    fun setBtnBackVisibility(value: Boolean) {
        _isBtnBackVisible.value = value
    }

    fun setTitleOfListText(value: Int) {
        _titleOfListText.value = value
    }

    fun setOpenSearchETVisibility(value: Boolean) {
        _isSearchIconVisible.value = value
    }

}