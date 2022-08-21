package com.example.recipes.business.usecases

import com.example.recipes.presentation.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val vm: AuthViewModel
) {

//    fun signUp(email: String, password: String, userName: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                taskResultSingUp(it)
//                setName(userName)
//            }
//    }

//    private fun taskResultSingUp(task: Task<AuthResult>) {
//        when (task.exception) {
//            is Exception -> {
//                task.exception?.localizedMessage?.let { message ->
//                    vm.messageForUser.value = message
//                }
////                _user.value = null
//            }
//        }
//    }

//    private fun setName(userName: String) {
//        auth.currentUser?.updateProfile(userProfileChangeRequest {
//            displayName = userName
//        })?.addOnCompleteListener {
//            if (it.isSuccessful) {
////                _user.value = auth.currentUser
//            }
//        }
//    }


}