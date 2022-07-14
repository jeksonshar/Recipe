package com.example.recipes.presentation.ui.registration

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.FirebaseUserSingleton
import com.example.recipes.presentation.base.BaseViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    application: Application       // как без application выполнять логику VM в которой приходится обращаться к строкам?
) : BaseViewModel(application) {

    val messageForUser = MutableLiveData<String?>()

    private val email = MutableLiveData<CharSequence>()
    private val userName = MutableLiveData<CharSequence>()
    private val password = MutableLiveData<CharSequence>()
    private val confirmPassword = MutableLiveData<CharSequence>()

    private val _isSignUpPage = MutableLiveData(false)
    val isSignUpPage: LiveData<Boolean> = _isSignUpPage

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _isETLoginError = MutableLiveData<Boolean?>()
    val isETLoginError: LiveData<Boolean?> = _isETLoginError

    private val _isETUserNameError = MutableLiveData<Boolean?>()
    val isETUserNameError: LiveData<Boolean?> = _isETUserNameError

    private val _etPasswordError = MutableLiveData<Boolean?>()
    val etPasswordError: LiveData<Boolean?> = _etPasswordError

    private val _etConfirmPasswordError = MutableLiveData<Boolean?>()
    val etConfirmPasswordError: LiveData<Boolean?> = _etConfirmPasswordError

    private val _progressSingingVisibility = MutableLiveData(false)
    val progressSingingVisibility: LiveData<Boolean> = _progressSingingVisibility

    private val _btnSignInVisibility = MutableLiveData(true)
    val btnSignInVisibility: LiveData<Boolean> = _btnSignInVisibility

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

    private fun signUp(auth: FirebaseAuth) {
        if (checkForClickEmail() && checkForClickPasswordsSingUp() && checkForClickUserName()) {
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

    private fun logIn(auth: FirebaseAuth) {
        if (checkForClickEmail() && checkForClickPasswordSingIn()) {
            auth.signInWithEmailAndPassword(
                email.value.toString(),
                password.value.toString()
            )      // перенести в юзкейс
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress()

                    taskResultLogIn(task, auth)
                }
        }
    }

    fun switchLogReg() {
        _isSignUpPage.value = !isSignUpPage.value!!
    }

    fun sendPasswordResetEmail(auth: FirebaseAuth) {
        if (checkForClickEmail()) {
            auth.sendPasswordResetEmail(email.value.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        messageForUser.value = context.getString(R.string.send_something_on_email)
                    } else {
                        task.exception?.localizedMessage?.let { message ->
                            messageForUser.value = message
                        }
                    }
                }
        }
    }

    private fun setName(auth: FirebaseAuth) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {                                 // перенести в юзкейс
            displayName = userName.value.toString()
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                _user.value = auth.currentUser
            }
        }
    }

    private fun taskResultSingUp(task: Task<AuthResult>) {
        when (task.exception) {
            is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    messageForUser.value = message
                }
                _user.value = null
            }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth) {
        when {
            task.isSuccessful -> {
                _user.value = auth.currentUser
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    messageForUser.value = message
                }
                _user.value = null
            }
        }
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

    private fun checkForClickPasswordsSingUp(): Boolean {
        return when {
            password.value.isNullOrEmpty() || confirmPassword.value.isNullOrEmpty() -> {
                messageForUser.value = context.getString(R.string.passwords_not_filled)
                _user.value = null
                false
            }
            password.value!!.length < 8 -> {
                messageForUser.value = context.getString(R.string.password_length_eight)
                _user.value = null
                false
            }
            password.value.toString() != confirmPassword.value.toString() -> {
                messageForUser.value = context.getString(R.string.passwords_does_not_match)
                _user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickPasswordSingIn(): Boolean {
        return when {
            password.value.isNullOrEmpty() -> {
                messageForUser.value = context.getString(R.string.passwords_not_filled)
                _user.value = null
                false
            }
            password.value!!.length < 8 -> {
                messageForUser.value = context.getString(R.string.password_length_eight)
                _user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickEmail(): Boolean {
        val message: String
        return when {
            email.value.isNullOrEmpty() -> {
                message = context.getString(R.string.email_not_filled)
                messageForUser.value = message
                _user.value = null
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches() -> {
                message = context.getString(R.string.email_does_not_match)
                messageForUser.value = message
                _user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickUserName(): Boolean {
        return when {
            userName.value.isNullOrEmpty() -> {
                messageForUser.value = context.getString(R.string.user_name_not_filled)
                _user.value = null
                false
            }
            else -> true
        }
    }

    fun changeVisibilityAtProgress() {
        _progressSingingVisibility.postValue(!progressSingingVisibility.value!!)
        _btnSignInVisibility.postValue(!btnSignInVisibility.value!!)
    }

    fun setFirebaseUser(user: FirebaseUser) {
        FirebaseUserSingleton.user = user                                                           // перенести в юзкейс
    }

}