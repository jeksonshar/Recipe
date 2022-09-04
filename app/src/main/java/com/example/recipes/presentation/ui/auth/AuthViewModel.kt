package com.example.recipes.presentation.ui.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.FirebaseUserSingleton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val exceptionMessageForUser = MutableStateFlow<String?>(null)
    val resMessageForUser = MutableStateFlow<Int?>(null)
    val user = MutableStateFlow<FirebaseUser?>(null)
    val btnSignInVisibility = MutableLiveData(true)

    private val email = MutableLiveData<CharSequence>()
    private val userName = MutableLiveData<CharSequence>()
    private val password = MutableLiveData<CharSequence>()
    private val confirmPassword = MutableLiveData<CharSequence>()

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

    fun sendPasswordResetEmail() {
        val auth = Firebase.auth
        if (checkForEmailMatch()) {
            auth.sendPasswordResetEmail(email.value.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resMessageForUser.value = R.string.send_something_on_email
                    } else {
                        task.exception?.localizedMessage?.let { message ->
                            exceptionMessageForUser.value = message
                        }
                    }
                }
        }
    }

    fun chooseEnter() {
        val auth = Firebase.auth
        if (isSignUpPage.value == true) signUp(auth)
        else logIn(auth)
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

    fun changeVisibilityAtProgress() {
        btnSignInVisibility.postValue(!(btnSignInVisibility.value ?: false))
    }

    fun setFirebaseUser(user: FirebaseUser) {
        FirebaseUserSingleton.user =
            user                                                           // перенести в юзкейс
    }

    private fun logIn(auth: FirebaseAuth) {
        if (checkForEmailMatch() && checkForPasswordMatchSingIn()) {
            auth.signInWithEmailAndPassword(
                email.value.toString(),
                password.value.toString()
            )                                                              // перенести в юзкейс
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress()

                    taskResultLogIn(task, auth)
                }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth) {
        when {
            task.isSuccessful -> {
                user.value = auth.currentUser
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    exceptionMessageForUser.value = message
                }
                user.value = null
            }
        }
    }

    private fun checkForPasswordMatchSingIn(): Boolean {
        return when {
            password.value.isNullOrEmpty() -> {
                resMessageForUser.value = R.string.passwords_not_filled
                user.value = null
                false
            }
            password.value!!.length < 8 -> {
                resMessageForUser.value = R.string.password_length_eight
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun signUp(auth: FirebaseAuth) {
        if (checkForEmailMatch() &&
            checkForPasswordsMatchSingUp() &&
            checkForUserNameEnter()
        ) {
            auth.createUserWithEmailAndPassword(
                email.value.toString(),
                password.value.toString()
            )  // перенести в юзкейс
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress()

                    taskResultSingUp(task)
                    setName(auth)
                }
        }
    }

    private fun setName(auth: FirebaseAuth) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {     // перенести в юзкейс
            displayName = userName.value.toString()
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                user.value = auth.currentUser
            }
        }
    }

    private fun taskResultSingUp(task: Task<AuthResult>) {
        when (task.exception) {
            is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    exceptionMessageForUser.value = message
                }
                user.value = null
            }
        }
    }

    private fun checkForUserNameEnter(): Boolean {
        return when {
            userName.value.isNullOrEmpty() -> {
                resMessageForUser.value = R.string.user_name_not_filled
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForPasswordsMatchSingUp(): Boolean {
        return when {
            password.value.isNullOrEmpty() || confirmPassword.value.isNullOrEmpty() -> {
                resMessageForUser.value = R.string.passwords_not_filled
                user.value = null
                false
            }
            password.value!!.length < 8 -> {
                resMessageForUser.value = R.string.password_length_eight
                user.value = null
                false
            }
            password.value.toString() != confirmPassword.value.toString() -> {
                resMessageForUser.value = R.string.passwords_does_not_match
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForEmailMatch(): Boolean {
        return when {
            email.value.isNullOrEmpty() -> {
                resMessageForUser.value = R.string.email_not_filled
                user.value = null
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches() -> {
                resMessageForUser.value = R.string.email_does_not_match
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun setEmail(value: CharSequence?) {
        value?.let { email.value = it }
    }

    private fun setUserName(value: CharSequence?) {
        value.let {
            if (it?.toString() != "") {
                userName.value = it
            }
        }
    }

    private fun setPassword(value: CharSequence?) {
        value?.let { password.value = it }
    }

    private fun setConfirmPassword(value: CharSequence?) {
        value?.let { confirmPassword.value = it }
    }

    private fun checkForWatcherEmail(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkForWatcherUserName(userName: String): Boolean {
        return userName.isNotEmpty()
    }

    private fun checkForWatcherPassword(password: String): Boolean {
        return password.isEmpty() || password.length > 7
    }

    private fun checkForWatcherConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}