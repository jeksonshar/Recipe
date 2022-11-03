package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.recipes.databinding.DialogChangeProfileNameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeProfileNameDialog(private val userProfileDelegate: UserProfileDelegate) : DialogFragment() {

    private var _binding: DialogChangeProfileNameBinding? = null
    private val binding: DialogChangeProfileNameBinding
        get() = _binding!!

    private val viewModelChangeProfileName: ChangeProfileNameViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangeProfileNameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelChangeProfileName

        viewModelChangeProfileName.exitFromDialog.observe(viewLifecycleOwner) {
            if (it) {
                dismiss()
                userProfileDelegate.getUserName()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(userProfileDelegate: UserProfileDelegate): ChangeProfileNameDialog {
            return ChangeProfileNameDialog(userProfileDelegate)
        }
    }

}