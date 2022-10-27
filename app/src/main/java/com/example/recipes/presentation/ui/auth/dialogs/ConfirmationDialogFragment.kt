package com.example.recipes.presentation.ui.auth.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.recipes.presentation.ui.auth.ConfirmationListener
import java.lang.ClassCastException

class ConfirmationDialogFragment : DialogFragment() {

    private lateinit var listener: ConfirmationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            // Instantiate the ConfirmationListener so we can send events to the host
            listener = activity as ConfirmationListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement ConfirmationListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage("Please, confirm the action")
            .setPositiveButton("Confirm") { _, _ ->
                listener.confirmButtonClicked()
            }
            .setNegativeButton("Cancel") { _, _ ->
                listener.cancelButtonClicked()
            }
            .create()
    }
}