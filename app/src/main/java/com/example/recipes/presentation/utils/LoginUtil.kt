package com.example.recipes.presentation.utils

import android.util.Log

object LoginUtil {
    fun logD(tag: String = "TAG", startMsg: String = "logD: ", parameter: String) {
        Log.d(tag, "$startMsg $parameter")
    }
}