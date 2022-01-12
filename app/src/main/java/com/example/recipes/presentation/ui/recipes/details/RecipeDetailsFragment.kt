package com.example.recipes.presentation.ui.recipes.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.databinding.FragmentDetailRecipeBinding
import com.example.recipes.presentation.utils.ImagesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment(R.layout.fragment_detail_recipe) {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeDetailsViewModel by viewModels()

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.moveToRecipe()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRecipeBinding.inflate(inflater, container, false)
        binding.apply {
            rvIngredients.layoutManager = LinearLayoutManager(requireContext())
            rvIngredients.adapter = adapter
            isFavorite.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.currentRecipeIsFavorite.value = !viewModel.currentRecipeIsFavorite.value!!
                    viewModel.saveOrDeleteRecipeToFavorite()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.currentRecipe.observe(viewLifecycleOwner, { recipe ->
            Log.d("TAG", "onViewCreated: Recipe = ${recipe.label} ")
            binding.apply {
                ivRecipeDetail.let {
                    ImagesUtil.setImage(recipe.image, it)
                }
                tvDetailRecipeName.text = recipe.label
                viewModel.recipeIsFavorite(recipe.uri)
            }
        })

        viewModel.currentRecipeIsFavorite.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteImage.setImageResource(R.drawable.like_on)
            } else {
                binding.favoriteImage.setImageResource(R.drawable.like_off)
            }
        })

        viewModel.currentRecipeIngredients.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.progressVisibilityLiveData.observe(viewLifecycleOwner, {
            binding.progressBarDetail.isVisible = it
        })

        viewModel.errorMassageLiveData.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                binding.apply {
                    tvErrorDetail.text = it
                    tvErrorDetail.isVisible = true
                    buttonRetryDetail.isVisible = true
                }
            } else {
                binding.apply {
                    tvErrorDetail.isVisible = false
                    buttonRetryDetail.isVisible = false
                    tvDetailIngredient.isVisible = true
                }
            }
        })

        viewModel.retryVisibilityLiveData.observe(viewLifecycleOwner, {
            binding.buttonRetryDetail.isVisible = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}