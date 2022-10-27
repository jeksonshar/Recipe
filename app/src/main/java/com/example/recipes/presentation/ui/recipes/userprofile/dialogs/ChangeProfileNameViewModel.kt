package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ChangeProfileNameViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val newName = MutableLiveData<String>()

    private var _exitFromDialog = MutableLiveData(false)
    val exitFromDialog: LiveData<Boolean> = _exitFromDialog

    fun setValue(value: Editable?) {
        if (value.isNullOrEmpty()) {
            newName.value = ""
        } else {
            newName.value = value.toString()
        }
    }

    fun submit() {
        if (newName.value?.isNotEmpty() == true) {
            auth.currentUser?.updateProfile(userProfileChangeRequest {
                displayName = newName.value
            })?.addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.d("TAG", "submit change name: change name not done!!")
                }
                _exitFromDialog.value = true
                BackPressedSingleton.isBackPressClick.value = true
            }
        } else {
            _exitFromDialog.value = true
        }
    }

    fun cancel() {
        _exitFromDialog.value = true
    }

}