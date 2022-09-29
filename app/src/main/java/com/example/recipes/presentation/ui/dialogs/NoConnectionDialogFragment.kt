package com.example.recipes.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.recipes.databinding.DialogNoConnectionBinding
import com.example.recipes.business.utils.CheckConnectionUtils
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.NetworkStatusSingleton

class NoConnectionDialogFragment : DialogFragment() {

    private var _binding: DialogNoConnectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogNoConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.okButton.setOnClickListener {
            CheckConnectionUtils.getNetConnection(requireContext())
            if (NetworkStatusSingleton.isNetworkConnected) {
                requireActivity().onBackPressed()
                BackPressedSingleton.clear()
            }
        }
    }
}