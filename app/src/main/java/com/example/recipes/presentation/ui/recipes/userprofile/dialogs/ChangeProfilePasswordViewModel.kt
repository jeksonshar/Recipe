package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeProfilePasswordViewModel @Inject constructor(): ViewModel() {

    private val auth = Firebase.auth

    private val password = MutableLiveData<CharSequence>()
    private val confirmPassword = MutableLiveData<CharSequence>()

    private val _etPasswordError = MutableLiveData<Boolean?>()
    val etPasswordError: LiveData<Boolean?> = _etPasswordError

    private val _etConfirmPasswordError = MutableLiveData<Boolean?>()
    val etConfirmPasswordError: LiveData<Boolean?> = _etConfirmPasswordError

    private var _exitFromDialog = MutableLiveData<Boolean>()
    val exitFromDialog: LiveData<Boolean> = _exitFromDialog

    private val _messageForUser = MutableLiveData<Int>()
    val messageForUser: LiveData<Int> = _messageForUser


    fun setPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherPassword(it.toString())) {
            _etPasswordError.value = null
        } else {
            _etPasswordError.value = true
        }
        setPassword(it)
    }

    fun setConfirmPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherConfirmPassword(password.value.toString(), it.toString())) {
            _etConfirmPasswordError.value = null
        } else {
            _etConfirmPasswordError.value = true
        }
        setConfirmPassword(it)
    }

    fun submit() {
        if (password.value?.isNotBlank() == true) {
            auth.currentUser?.updatePassword(password.value.toString())?.addOnCompleteListener {
                if (it.isComplete) {
                    _messageForUser.value = R.string.password_is_updated
                } else {
                    _messageForUser.value = R.string.password_is_not_updated
                }
                _exitFromDialog.value = true
            }
        }
    }

    private fun checkForWatcherPassword(password: String): Boolean {
        return password.isEmpty() || password.length > 7
    }

    private fun setPassword(value: CharSequence?) {
        value?.let { password.value = it }
    }

    private fun checkForWatcherConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun setConfirmPassword(value: CharSequence?) {
        value?.let { confirmPassword.value = it }
    }

    fun cancel() {
        _exitFromDialog.value = true
    }

}