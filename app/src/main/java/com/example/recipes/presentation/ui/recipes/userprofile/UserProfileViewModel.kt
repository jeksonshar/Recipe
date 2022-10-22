package com.example.recipes.presentation.ui.recipes.userprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val recipeDataStore: RecipeDataStore,
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
) : ViewModel() {

    private val currentUser = Firebase.auth.currentUser
    val email = currentUser?.email ?: "--"

    val onBackPressed = BackPressedSingleton.isBackPressClick

    private val _favoriteRecipesCount = MutableLiveData<Int>()
    val favoriteRecipesCount: LiveData<Int> = _favoriteRecipesCount

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri


    fun getFavoriteRecipes() {
        viewModelScope.launch {
            _favoriteRecipesCount.value = currentUser?.uid?.let {
                getFavoriteRecipesUseCase.getUserFavoriteRecipesFromRoom(it).size
            }
        }
    }

    fun getName() {
        _userName.value = currentUser?.displayName ?: "--"
    }

    fun getPhoto() {
        currentUser?.photoUrl?.let {
            _photoUri.value = it
        }
    }

    fun resetQueryInDatastore() {
        viewModelScope.launch {
            recipeDataStore.setLastQuery("")
        }
    }

    override fun onCleared() {
        // разобраться, вроде не нужно тут
//        BackPressedSingleton.clear()
        super.onCleared()
    }

}