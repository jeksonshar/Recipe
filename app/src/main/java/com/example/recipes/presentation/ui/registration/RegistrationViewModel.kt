package com.example.recipes.presentation.ui.registration

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {

    val confirmPasswordVisibility = MutableLiveData<Int>()
    val email = MutableLiveData<Editable>()
    val password = MutableLiveData<Editable>()
    val confirmPassword = MutableLiveData<Editable>()

    fun changeConfirmPasswordVisibility(value: Int) {
        confirmPasswordVisibility.value = value
    }

    fun setEmail(value: Editable?) {
        value?.let { email.value = it }
    }

    fun setPassword(value: Editable?) {
        value?.let { password.value = it }
    }

    fun setConfirmPassword(value: Editable?) {
        value?.let { confirmPassword.value = it }
    }

}