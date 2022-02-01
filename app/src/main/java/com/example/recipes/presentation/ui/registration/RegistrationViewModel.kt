package com.example.recipes.presentation.ui.registration

import android.text.Editable
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.business.domain.singletons.FirebaseUserSingleton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.lang.Exception

class RegistrationViewModel : ViewModel() {

    val signUpOrLogIn = MutableLiveData(LOG_IN)
    private val email = MutableLiveData<Editable>()
    private val password = MutableLiveData<Editable>()
    private val confirmPassword = MutableLiveData<Editable>()

    val user = MutableLiveData<FirebaseUser>()
    val toast = MutableLiveData<String>()

    fun changeConfirmPasswordVisibility(value: Int) {
        signUpOrLogIn.value = value
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

    fun signUp(auth: FirebaseAuth) {
        if (checkForClickPasswordsSingUp() && checkForClickEmail()) {
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

    private fun setName(auth: FirebaseAuth) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {                                           // для пробы, потом изменить
            displayName = "Jekson"
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                user.value = auth.currentUser
            }
        }

    }

    private fun taskResultSingUp(task: Task<AuthResult>) {
        when (task.exception) {
            is Exception -> {
                task.exception?.localizedMessage?.let { message -> toast.value = message }
            }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth) {
        when {
            task.isSuccessful -> {
                user.value = auth.currentUser
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message -> toast.value = message }
            }
        }
    }

    fun checkForWatcherEmail(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
                toast.value = "Пароли не заполнены"
                false
            }
            password.value!!.length < 8 -> {
                toast.value = "Длина пароля должна быть не менее 8 символов"
                false
            }
            password.value.toString() != confirmPassword.value.toString() -> {
                toast.value = "Пароли не совпадают"
                false
            }
            else -> true
        }
    }

    private fun checkForClickPasswordSingIn(): Boolean {
        return when {
            password.value.isNullOrEmpty() -> {
                toast.value = "Пароль не заполнен"
                false
            }
            password.value!!.length < 8 -> {
                toast.value = "Длина пароля должна быть не менее 8 символов"
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
                toast.value = message
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches() -> {
                message = "email не соответзтвует"
                toast.value = message
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