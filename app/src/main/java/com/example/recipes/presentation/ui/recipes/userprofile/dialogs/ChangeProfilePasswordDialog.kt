package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.recipes.databinding.DialogChangeProfilePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeProfilePasswordDialog : DialogFragment() {

    private var _binding: DialogChangeProfilePasswordBinding? = null
    private val binding: DialogChangeProfilePasswordBinding
        get() = _binding!!

    private val viewModelChangePassword: ChangeProfilePasswordViewModel by viewModels()

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
        _binding = DialogChangeProfilePasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModelChangePassword

        viewModelChangePassword.exitFromDialog.observe(viewLifecycleOwner) {
            if (it){
                requireActivity().onBackPressed()
            }
        }

        viewModelChangePassword.messageForUser.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), resources.getText(it), Toast.LENGTH_LONG).show()
        }

        return binding.root
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}