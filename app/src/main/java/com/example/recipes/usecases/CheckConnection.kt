package com.example.recipes.usecases

import android.content.Context

interface CheckConnection {

    fun isNetConnected(context: Context): Boolean
}