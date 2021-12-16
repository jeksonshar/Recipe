package com.example.recipes.ui.recipes.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.business.usecases.GetRecipeListUseCase
import com.example.recipes.databinding.FragmentDetailRecipeBinding
import com.example.recipes.datasouce.RecipeDataStore
import com.example.recipes.datasouce.network.RetrofitModule
import com.example.recipes.ui.recipes.MyViewModelFactory

class RecipeDetailsFragment : Fragment() {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!

    private val apiService = RetrofitModule.RECIPES_API_SERVICE

    private val viewModel: RecipeDetailsViewModel by viewModels {
        MyViewModelFactory(
            GetRecipeListUseCase(apiService),
            GetRecipeUseCase(apiService),
            RecipeDataStore(requireContext()),
            this
        )
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idRecipe = requireArguments().getString(RECIPE_KEY)
        Log.d("TAG", "onCreateDetailFragment: $idRecipe")

        viewModel.getRecipe(idRecipe ?: "")
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
            buttonRetryDetail.setOnClickListener {
                val idRecipe = requireArguments().getString(RECIPE_KEY)
                viewModel.getRecipe(idRecipe ?: "")
            }
            isFavorite.setOnClickListener {
//                viewModel.currentRecipe.value
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.currentRecipe.observe(viewLifecycleOwner, { recipe ->
            Log.d("TAG", "onViewCreated: Recipe = ${recipe.label} ")
            binding.apply {
                ivRecipeDetail.let { Glide.with(requireContext()).load(recipe.image).into(it) }
                tvDetailRecipeName.text = recipe.label
                if (recipe.isFavorite) {
                    favoriteImage.setImageResource(R.drawable.like_on)
                } else {
                    favoriteImage.setImageResource(R.drawable.like_off)
                }
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

    companion object {
        private const val RECIPE_KEY = "Recipe item"

        fun newInstance(id: String): RecipeDetailsFragment {
            val args = Bundle()
            args.putString(RECIPE_KEY, id)
            val fragment = RecipeDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}