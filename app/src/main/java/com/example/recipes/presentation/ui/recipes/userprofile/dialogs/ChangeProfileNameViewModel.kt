package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.presentation.ui.recipes.userprofile.Action
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ChangeProfileNameViewModel : ViewModel() {

    private val auth = Firebase.auth

    val exitFromDialog = MutableLiveData(false)
    private val newName = MutableLiveData<String>()
    private val newPassword = MutableLiveData<String>()

    private val _action = MutableLiveData<Action>()
    val action: LiveData<Action>
        get() = _action


    fun setAction(action: Action) {
        _action.value = action
    }

    fun setValue(value: Editable?) {
        when (action.value) {
            Action.CHANGE_NAME -> {
                if (value.isNullOrEmpty()) {
                    newName.value = ""
                } else {
                    newName.value = value.toString()
                }
            }
            Action.CHANGE_PASSWORD -> {
                if (value.isNullOrEmpty()) {
                    newPassword.value = ""
                } else {
                    newPassword.value = value.toString()
                }
            }
            else -> {
                throw Exception("Nothing Action Enum in setValue ChangeProfileNameViewModel")
            }
        }
    }

    fun submit() {
        when (action.value) {
            Action.CHANGE_NAME -> {
                if (newName.value?.isNotEmpty() == true) {
                    auth.currentUser?.updateProfile(userProfileChangeRequest {
                        displayName = newName.value
                    })?.addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Log.d("TAG", "submit: change name not done!!")
                        }
                        exitFromDialog.value = true
                    }
                } else {
                    exitFromDialog.value = true
                }
            }
            Action.CHANGE_PASSWORD -> {
                if (newPassword.value?.isNotEmpty() == true) {
                    auth.currentUser?.updatePassword(newPassword.value ?: "")
                        ?.addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Log.d("TAG", "submit: change password not done!!")
                            }
                            exitFromDialog.value = true
                        }
                } else {
                    exitFromDialog.value = true
                }
            }
            else -> {
                throw Exception("Nothing Action Enum in setValue ChangeProfileNameViewModel")
            }
        }
    }

    fun cancel() {
        exitFromDialog.value = true
    }

}