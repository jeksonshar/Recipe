package com.example.recipes.presentation.ui.registration

import android.text.Editable
import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.business.domain.singletons.FirebaseUserSingleton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.lang.Exception

//test remote удалить
//test2 remote удалить
class RegistrationViewModel : ViewModel() {

    private val _signUpOrLogIn = MutableLiveData(LOG_IN)
    val signUpOrLogIn: LiveData<Int> = _signUpOrLogIn

    private val email = MutableLiveData<Editable>()
    private val userName = MutableLiveData<Editable>()
    private val password = MutableLiveData<Editable>()
    private val confirmPassword = MutableLiveData<Editable>()

    val user = MutableLiveData<FirebaseUser?>()
    val messageForUser = MutableLiveData<String>(null)

    fun changeConfirmPasswordVisibility(value: Int) {
        _signUpOrLogIn.value = value
    }

    fun setEmail(value: Editable?) {
        value?.let { email.value = it }
    }

    fun setUserName(value: Editable?) {
        value.let { userName.value = it }
    }

    fun setPassword(value: Editable?) {
        value?.let { password.value = it }
    }

    fun setConfirmPassword(value: Editable?) {
        value?.let { confirmPassword.value = it }
    }

    fun signUp(auth: FirebaseAuth) {
        if (checkForClickPasswordsSingUp() && checkForClickEmail() && checkForClickUserName()) {
            auth.createUserWithEmailAndPassword(email.value.toString(), password.value.toString())          // перенести в юзкейс
                .addOnCompleteListener { task ->
                    taskResultSingUp(task)
                    setName(auth)
                }
        }
    }

    fun logIn(auth: FirebaseAuth) {
        if (checkForClickEmail() && checkForClickPasswordSingIn()) {
            auth.signInWithEmailAndPassword(email.value.toString(), password.value.toString())              // перенести в юзкейс
                .addOnCompleteListener { task ->
                    taskResultLogIn(task, auth)
                }
        }
    }

    fun sendPasswordResetEmail(auth: FirebaseAuth) {
        if (checkForClickEmail()) {
            auth.sendPasswordResetEmail(email.value.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        messageForUser.value = "На указанную почту было что-то отправлено"
                    } else {
                        task.exception?.localizedMessage?.let { message ->
                            messageForUser.value = message
                        }
                    }
                }
        }
    }

    private fun setName(auth: FirebaseAuth) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {                                           // перенести в юзкейс
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
                task.exception?.localizedMessage?.let { message -> messageForUser.value = message }
                user.value = null
            }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth) {
        when {
            task.isSuccessful -> {
                user.value = auth.currentUser
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message -> messageForUser.value = message }
                user.value = null
            }
        }
    }

    fun checkForWatcherEmail(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkForWatcherUserName(userName: String): Boolean {
        return userName.isNotEmpty()
    }

    fun checkForWatcherPassword(password: String): Boolean {
        return password.isEmpty() || password.length > 7
    }

    fun checkForWatcherConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun checkForClickPasswordsSingUp(): Boolean {
        return when {
            password.value.isNullOrEmpty() || confirmPassword.value.isNullOrEmpty() -> {
                messageForUser.value = "Пароли не заполнены"
                user.value = null
                false
            }
            password.value!!.length < 8 -> {
                messageForUser.value = "Длина пароля должна быть не менее 8 символов"
                user.value = null
                false
            }
            password.value.toString() != confirmPassword.value.toString() -> {
                messageForUser.value = "Пароли не совпадают"
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickPasswordSingIn(): Boolean {
        return when {
            password.value.isNullOrEmpty() -> {
                messageForUser.value = "Пароль не заполнен"
                user.value = null
                false
            }
            password.value!!.length < 8 -> {
                messageForUser.value = "Длина пароля должна быть не менее 8 символов"
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickEmail(): Boolean {
        val message: String
        return when {
            email.value.isNullOrEmpty() -> {
                message = "email не заполнен"
                messageForUser.value = message
                user.value = null
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches() -> {
                message = "email не соответзтвует"
                messageForUser.value = message
                user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForClickUserName(): Boolean {
        return when {
            userName.value.isNullOrEmpty() -> {
                messageForUser.value = "имя пользователя не заполнено"
                user.value = null
                false
            }
            else -> true
        }
    }

    fun setFirebaseUser(user: FirebaseUser) {
        FirebaseUserSingleton.user = user                                                                   // перенести в юзкейс
    }

    companion object {
        const val SIGN_UP = View.VISIBLE
        const val LOG_IN = View.GONE
    }

}