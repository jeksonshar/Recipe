package com.example.recipes.presentation.base

import androidx.fragment.app.Fragment
import com.example.recipes.databinding.FragmentDetailRecipeBinding

class BaseFragment : Fragment() {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}