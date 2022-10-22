package com.example.recipes.presentation.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object SystemUtil {

    fun Context.hideKeyboard(view: View?) {
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}