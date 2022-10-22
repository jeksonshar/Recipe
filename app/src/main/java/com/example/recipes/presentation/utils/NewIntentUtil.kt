package com.example.recipes.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

object NewIntentUtil {
    fun newIntent(context: Context, activity: AppCompatActivity): Intent {
        return Intent(context, activity::class.java)
    }
}