package com.example.recipes.business.domain.singletons

import androidx.lifecycle.MutableLiveData

object BackPressedSingleton {
// не использую
    var isBackPressClick = MutableLiveData<Boolean?>(null)

    fun clear() {
        isBackPressClick.value = null
    }

}