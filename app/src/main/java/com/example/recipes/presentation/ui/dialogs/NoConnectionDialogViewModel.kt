package com.example.recipes.presentation.ui.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.recipes.business.usecases.CheckConnectionUseCaseImpl
import javax.inject.Inject

class NoConnectionDialogViewModel @Inject constructor(
    checkConnectionUseCase: CheckConnectionUseCaseImpl,
) : ViewModel() {
    val isNetConnected = checkConnectionUseCase.isConnected().asLiveData()
}