package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.recipes.databinding.DialogChangeProfileNameBinding


class ChangeProfileNameDialog : DialogFragment() {

    private var _binding: DialogChangeProfileNameBinding? = null
    private val binding: DialogChangeProfileNameBinding
        get() = _binding!!

    private val vmChangeProfileName: ChangeProfileNameViewModel by viewModels()

    private val args: ChangeProfileNameDialogArgs by navArgs()

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        return dialog
//    }

//    override fun getTheme() = R.style.DialogChangeProfileTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangeProfileNameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vmChangeProfileName

        vmChangeProfileName.setAction(args.action)

//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        vmChangeProfileName.exitFromDialog.observe(viewLifecycleOwner) {
            if (it) {
                vmChangeProfileName.exitFromDialog.value = false
                requireActivity().onBackPressed()
            }
        }

        return binding.root
    }

//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    }

}