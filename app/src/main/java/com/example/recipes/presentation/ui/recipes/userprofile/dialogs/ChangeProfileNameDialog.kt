package com.example.recipes.presentation.ui.recipes.userprofile.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.recipes.databinding.DialogChangeProfileNameBinding
import com.example.recipes.presentation.ui.recipes.userprofile.Action


class ChangeProfileNameDialog : DialogFragment() {

    private var _binding: DialogChangeProfileNameBinding? = null
    private val binding: DialogChangeProfileNameBinding
        get() = _binding!!

    private val vmChangeProfileName: ChangeProfileNameViewModel by viewModels()

    private val args: ChangeProfileNameDialogArgs by navArgs()
//    val args = this.arguments


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
        binding.vm = vmChangeProfileName

//        val action: Action = when (args?.get("action")) {
//            "CHANGE_PASSWORD" ->  Action.CHANGE_PASSWORD
//            else -> Action.CHANGE_NAME
//        }
        vmChangeProfileName.setAction(args.action)
//        vmChangeProfileName.setAction(action)

        vmChangeProfileName.exitFromDialog.observe(viewLifecycleOwner) {
            if (it) {
                vmChangeProfileName.exitFromDialog.value = false
                requireActivity().onBackPressed()
            }
        }

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    }

}