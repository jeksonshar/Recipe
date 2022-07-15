package com.example.recipes.presentation.ui.registration

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ui.res.stringResource
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import com.example.recipes.R

@BindingAdapter("app:setProposeToLogin")
fun setProposeToLogin(context: Context, textView: AppCompatTextView, visible: Int) {
    textView.text = if (visible == View.VISIBLE) {
        context.getString(R.string.sign_up_to_continue)
    } else {
        context.getString(R.string.log_in_to_continue)
    }
}

//@BindingAdapter("app:showConfirmationDialog")
//fun showConfirmationDialog(supportFragmentManager: FragmentManager) {
//    ConfirmationDialogFragment().show(supportFragmentManager, "ConfirmationDialogFragmentTag")
//}