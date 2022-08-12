package com.example.recipes.presentation.ui.registration

import android.util.Patterns
import com.example.recipes.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

object RegistrationUIModel {

//    @JvmStatic
//    fun sendPasswordResetEmail(vm: RegistrationViewModel) {
//        val auth = Firebase.auth
//        if (checkForEmailMatch(vm)) {
//            auth.sendPasswordResetEmail(vm.email.value.toString())
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        vm.resMessageForUser.value = R.string.send_something_on_email
//                    } else {
//                        task.exception?.localizedMessage?.let { message ->
//                            vm.stringMessageForUser.value = message
//                        }
//                    }
//                }
//        }
//    }
//
//    @JvmStatic
//    fun chooseEnter(vm: RegistrationViewModel) {
//        val auth = Firebase.auth
//        if (vm.isSignUpPage.value == true) signUp(vm, auth)
//        else logIn(vm, auth)
//    }
//
//    private fun logIn(vm: RegistrationViewModel, auth: FirebaseAuth) {
//        if (checkForEmailMatch(vm) && checkForPasswordMatchSingIn(vm)) {
//            auth.signInWithEmailAndPassword(
//                vm.email.value.toString(),
//                vm.password.value.toString()
//            )      // перенести в юзкейс
//                .addOnCompleteListener { task ->
//                    // запустить прогресс бар
//                    changeVisibilityAtProgress(vm)
//
//                    taskResultLogIn(task, auth, vm)
//                }
//        }
//    }
//
//    private fun taskResultLogIn(task: Task<AuthResult>, auth: FirebaseAuth, vm: RegistrationViewModel) {
//        when {
//            task.isSuccessful -> {
//                vm.user.value = auth.currentUser
//            }
//            task.exception is Exception -> {
//                task.exception?.localizedMessage?.let { message ->
//                    vm.stringMessageForUser.value = message
//                }
//                vm.user.value = null
//            }
//        }
//    }
//
//    private fun checkForPasswordMatchSingIn(vm: RegistrationViewModel): Boolean {
//        return when {
//            vm.password.value.isNullOrEmpty() -> {
//                vm.resMessageForUser.value = R.string.passwords_not_filled
//                vm.user.value = null
//                false
//            }
//            vm.password.value!!.length < 8 -> {
//                vm.resMessageForUser.value = R.string.password_length_eight
//                vm.user.value = null
//                false
//            }
//            else -> true
//        }
//    }
//
//    private fun signUp(vm: RegistrationViewModel, auth: FirebaseAuth) {
//        if (checkForEmailMatch(vm) &&
//            checkForPasswordsMatchSingUp(vm) &&
//            checkForUserNameEnter(vm)
//        ) {
//            auth.createUserWithEmailAndPassword(
//                vm.email.value.toString(),
//                vm.password.value.toString()
//            )  // перенести в юзкейс
//                .addOnCompleteListener { task ->
//                    // запустить прогресс бар
//                    changeVisibilityAtProgress(vm)
//
//                    taskResultSingUp(task, vm)
//                    setName(auth, vm)
//                }
//        }
//    }
//
//    private fun setName(auth: FirebaseAuth, vm: RegistrationViewModel) {
//        auth.currentUser?.updateProfile(userProfileChangeRequest {                             // перенести в юзкейс
//            displayName = vm.userName.value.toString()
//        })?.addOnCompleteListener {
//            if (it.isSuccessful) {
//                vm.user.value = auth.currentUser
//            }
//        }
//    }
//
//    private fun taskResultSingUp(task: Task<AuthResult>, vm: RegistrationViewModel) {
//        when (task.exception) {
//            is Exception -> {
//                task.exception?.localizedMessage?.let { message ->
//                    vm.stringMessageForUser.value = message
//                }
//                vm.user.value = null
//            }
//        }
//    }
//
//    private fun changeVisibilityAtProgress(vm: RegistrationViewModel) {
//        vm.progressSingingVisibility.postValue(!vm.progressSingingVisibility.value!!)
//        vm.btnSignInVisibility.postValue(!vm.btnSignInVisibility.value!!)
//    }
//
//    private fun checkForUserNameEnter(vm: RegistrationViewModel): Boolean {
//        return when {
//            vm.userName.value.isNullOrEmpty() -> {
//                vm.resMessageForUser.value = R.string.user_name_not_filled
//                vm.user.value = null
//                false
//            }
//            else -> true
//        }
//    }
//
//    private fun checkForPasswordsMatchSingUp(vm: RegistrationViewModel): Boolean {
//        return when {
//            vm.password.value.isNullOrEmpty() || vm.confirmPassword.value.isNullOrEmpty() -> {
//                vm.resMessageForUser.value = R.string.passwords_not_filled
//                vm.user.value = null
//                false
//            }
//            vm.password.value!!.length < 8 -> {
//                vm.resMessageForUser.value = R.string.password_length_eight
//                vm.user.value = null
//                false
//            }
//            vm.password.value.toString() != vm.confirmPassword.value.toString() -> {
//                vm.resMessageForUser.value = R.string.passwords_does_not_match
//                vm.user.value = null
//                false
//            }
//            else -> true
//        }
//    }
//
//    private fun checkForEmailMatch(vm: RegistrationViewModel): Boolean {
//        return when {
//            vm.email.value.isNullOrEmpty() -> {
//                vm.resMessageForUser.value = R.string.email_not_filled
//                vm.user.value = null
//                false
//            }
//            !Patterns.EMAIL_ADDRESS.matcher(vm.email.value!!).matches() -> {
//                vm.resMessageForUser.value = R.string.email_does_not_match
//                vm.user.value = null
//                false
//            }
//            else -> true
//        }
//    }

}