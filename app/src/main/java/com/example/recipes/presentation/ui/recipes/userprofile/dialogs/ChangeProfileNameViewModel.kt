package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.presentation.ui.recipes.userprofile.Action
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ChangeProfileNameViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val newName = MutableLiveData<String>()
    private val newPassword = MutableLiveData<String>()

    private var _exitFromDialog = MutableLiveData(false)
    val exitFromDialog: LiveData<Boolean> = _exitFromDialog

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
                        _exitFromDialog.value = true
                        BackPressedSingleton.isBackPressClick.value = true
                    }
                } else {
                    _exitFromDialog.value = true
                }
            }
            Action.CHANGE_PASSWORD -> {
                if (newPassword.value?.isNotEmpty() == true) {
                    auth.currentUser?.updatePassword(newPassword.value ?: "")
                        ?.addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Log.d("TAG", "submit: change password not done!!")
                            }
                            _exitFromDialog.value = true
                        }
                } else {
                    _exitFromDialog.value = true
                }
            }
            else -> {
                throw Exception("Nothing Action Enum in setValue ChangeProfileNameViewModel")
            }
        }
    }

    fun cancel() {
        _exitFromDialog.value = true
    }

}