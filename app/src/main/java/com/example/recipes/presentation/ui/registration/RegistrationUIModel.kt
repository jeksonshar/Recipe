package com.example.recipes.presentation.ui.registration

import android.content.Context
import android.util.Patterns
import com.example.recipes.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

object RegistrationUIModel {

    @JvmStatic
    fun sendPasswordResetEmail(context: Context, vm: RegistrationViewModel) {
        val auth = Firebase.auth
        if (checkForEmailMatch(context, vm)) {
            auth.sendPasswordResetEmail(vm.email.value.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        vm.messageForUser.value = context.getString(R.string.send_something_on_email)
                    } else {
                        task.exception?.localizedMessage?.let { message ->
                            vm.messageForUser.value = message
                        }
                    }
                }
        }
    }

    @JvmStatic
    fun chooseEnter(context: Context, vm: RegistrationViewModel) {
        val auth = Firebase.auth
        if (vm.isSignUpPage.value == true) signUp(context, vm, auth)
        else logIn(context, vm, auth)
    }

    private fun logIn(context: Context, vm: RegistrationViewModel, auth: FirebaseAuth) {
        if (checkForEmailMatch(context, vm) && checkForPasswordMatchSingIn(context, vm)) {
            auth.signInWithEmailAndPassword(
                vm.email.value.toString(),
                vm.password.value.toString()
            )      // перенести в юзкейс
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress(vm)

                    taskResultLogIn(task, auth, vm)
                }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth, vm: RegistrationViewModel) {
        when {
            task.isSuccessful -> {
                vm.user.value = auth.currentUser
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    vm.messageForUser.value = message
                }
                vm.user.value = null
            }
        }
    }

    private fun checkForPasswordMatchSingIn(context: Context, vm: RegistrationViewModel): Boolean {
        return when {
            vm.password.value.isNullOrEmpty() -> {
                vm.messageForUser.value = context.getString(R.string.passwords_not_filled)
                vm.user.value = null
                false
            }
            vm.password.value!!.length < 8 -> {
                vm.messageForUser.value = context.getString(R.string.password_length_eight)
                vm.user.value = null
                false
            }
            else -> true
        }
    }

    private fun signUp(context: Context, vm: RegistrationViewModel, auth: FirebaseAuth) {
        if (checkForEmailMatch(context, vm) &&
            checkForPasswordsMatchSingUp(context, vm) &&
            checkForUserNameEnter(context, vm)
        ) {
            auth.createUserWithEmailAndPassword(
                vm.email.value.toString(),
                vm.password.value.toString()
            )  // перенести в юзкейс
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress(vm)

                    taskResultSingUp(task, vm)
                    setName(auth, vm)
                }
        }
    }

    private fun setName(auth: FirebaseAuth, vm: RegistrationViewModel) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {                             // перенести в юзкейс
            displayName = vm.userName.value.toString()
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                vm.user.value = auth.currentUser
            }
        }
    }

    private fun taskResultSingUp(task: Task<AuthResult>, vm: RegistrationViewModel) {
        when (task.exception) {
            is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    vm.messageForUser.value = message
                }
                vm.user.value = null
            }
        }
    }

    private fun changeVisibilityAtProgress(vm: RegistrationViewModel) {
        vm.progressSingingVisibility.postValue(!vm.progressSingingVisibility.value!!)
        vm.btnSignInVisibility.postValue(!vm.btnSignInVisibility.value!!)
    }

    private fun checkForUserNameEnter(context: Context, vm: RegistrationViewModel): Boolean {
        return when {
            vm.userName.value.isNullOrEmpty() -> {
                vm.messageForUser.value = context.getString(R.string.user_name_not_filled)
                vm.user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForPasswordsMatchSingUp(context: Context, vm: RegistrationViewModel): Boolean {
        return when {
            vm.password.value.isNullOrEmpty() || vm.confirmPassword.value.isNullOrEmpty() -> {
                vm.messageForUser.value = context.getString(R.string.passwords_not_filled)
                vm.user.value = null
                false
            }
            vm.password.value!!.length < 8 -> {
                vm.messageForUser.value = context.getString(R.string.password_length_eight)
                vm.user.value = null
                false
            }
            vm.password.value.toString() != vm.confirmPassword.value.toString() -> {
                vm.messageForUser.value = context.getString(R.string.passwords_does_not_match)
                vm.user.value = null
                false
            }
            else -> true
        }
    }

    private fun checkForEmailMatch(context: Context, vm: RegistrationViewModel): Boolean {
        return when {
            vm.email.value.isNullOrEmpty() -> {
                vm.messageForUser.value = context.getString(R.string.email_not_filled)
                vm.user.value = null
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(vm.email.value!!).matches() -> {
                vm.messageForUser.value = context.getString(R.string.email_does_not_match)
                vm.user.value = null
                false
            }
            else -> true
        }
    }

}