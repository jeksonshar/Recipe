package com.example.recipes.presentation.ui.registration

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.business.domain.singletons.FirebaseUserSingleton
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {

    val messageForUser = MutableLiveData<String?>()
    val user = MutableLiveData<FirebaseUser?>()
    val progressSingingVisibility = MutableLiveData(false)
    val btnSignInVisibility = MutableLiveData(true)

    private val _email = MutableLiveData<CharSequence>()
    val email: LiveData<CharSequence> = _email

    private val _userName = MutableLiveData<CharSequence>()
    val userName: LiveData<CharSequence> = _userName

    private val _password = MutableLiveData<CharSequence>()
    val password: LiveData<CharSequence> = _password

    private val _confirmPassword = MutableLiveData<CharSequence>()
    val confirmPassword: LiveData<CharSequence> = _confirmPassword

    private val _isSignUpPage = MutableLiveData(false)
    val isSignUpPage: LiveData<Boolean> = _isSignUpPage

    private val _isETLoginError = MutableLiveData<Boolean?>()
    val isETLoginError: LiveData<Boolean?> = _isETLoginError

    private val _isETUserNameError = MutableLiveData<Boolean?>()
    val isETUserNameError: LiveData<Boolean?> = _isETUserNameError

    private val _etPasswordError = MutableLiveData<Boolean?>()
    val etPasswordError: LiveData<Boolean?> = _etPasswordError

    private val _etConfirmPasswordError = MutableLiveData<Boolean?>()
    val etConfirmPasswordError: LiveData<Boolean?> = _etConfirmPasswordError

    private fun setEmail(value: CharSequence?) {
        value?.let { _email.value = it }
    }

    private fun setUserName(value: CharSequence?) {
        value.let {
            if (it?.toString() != "") {
                _userName.value = it
            }
        }
    }

    private fun setPassword(value: CharSequence?) {
        value?.let { _password.value = it }
    }

    private fun setConfirmPassword(value: CharSequence?) {
        value?.let { _confirmPassword.value = it }
    }

    fun onLoginTextChanged(it: CharSequence) {
        if (checkForWatcherEmail(it.toString())) {
            _isETLoginError.value = null
        } else {
            _isETLoginError.value = true
        }
        setEmail(it)
    }

    fun onNameTextChanged(it: CharSequence) {
        if (checkForWatcherUserName(it.toString())) {
            _isETUserNameError.value = null
        } else {
            _isETUserNameError.value = true
        }
        setUserName(it)
    }

    fun etPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherPassword(it.toString())) {
            _etPasswordError.value = null
        } else {
            _etPasswordError.value = true
        }
        setPassword(it)
    }

    fun etConfirmPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherConfirmPassword(password.value.toString(), it.toString())) {
            _etConfirmPasswordError.value = null
        } else {
            _etConfirmPasswordError.value = true
        }
        setConfirmPassword(it)
    }

    fun switchLogReg() {
        _isSignUpPage.value = !isSignUpPage.value!!
    }

    private fun checkForWatcherEmail(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkForWatcherUserName(userName: String): Boolean {
        return userName.isEmpty()
    }

    private fun checkForWatcherPassword(password: String): Boolean {
        return password.isEmpty() || password.length > 7
    }

    private fun checkForWatcherConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun changeVisibilityAtProgress() {
        progressSingingVisibility.postValue(!progressSingingVisibility.value!!)
        btnSignInVisibility.postValue(!btnSignInVisibility.value!!)
    }

    fun setFirebaseUser(user: FirebaseUser) {
        FirebaseUserSingleton.user = user                                                           // перенести в юзкейс
    }

}