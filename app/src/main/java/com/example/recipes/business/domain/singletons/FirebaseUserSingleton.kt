package com.example.recipes.business.domain.singletons

import com.google.firebase.auth.FirebaseUser

object FirebaseUserSingleton {
    var user: FirebaseUser? = null

    fun cancel() {
        user = null
    }
}