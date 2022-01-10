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
import com.example.recipes.business.usecases.GetFavoriteRecipeUseCase
import com.example.recipes.business.usecases.ManageFavoriteRecipeUseCase
import com.example.recipes.databinding.FragmentDetailRecipeBinding
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.datasouce.network.RetrofitModule
import com.example.recipes.presentation.utils.ImagesUtil
import kotlinx.coroutines.launch

class RecipeDetailsFragment : Fragment(R.layout.fragment_detail_recipe) {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!

    private val apiService = RetrofitModule.RECIPES_API_SERVICE

    private val viewModel: RecipeDetailsViewModel by viewModels {
        RecipeDetailsViewModelFactory(
            GetFavoriteRecipeUseCase(RecipeDataBase.create(requireContext())), //  Костыль, вынести отсюда
            this,
            null
        )
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DetailAdapter()
    }

    private lateinit var dbManage: ManageFavoriteRecipeUseCase                                    //  Костыль, вынести отсюда

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.moveToRecipe()

        dbManage = ManageFavoriteRecipeUseCase(RecipeDataBase.create(requireContext()))           //  Костыль, вынести отсюда
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
                    dbManage.manageRecipe(viewModel.currentRecipe.value!!)                  //  Костыль, вынести отсюда
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