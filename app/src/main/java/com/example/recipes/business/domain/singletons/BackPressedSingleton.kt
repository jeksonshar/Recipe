package com.example.recipes.business.domain.singletons

import androidx.lifecycle.MutableLiveData

object BackPressedSingleton {

    var isBackPressClick = MutableLiveData<Boolean?>(null)

    fun clear() {
        isBackPressClick.value = null
    }

}