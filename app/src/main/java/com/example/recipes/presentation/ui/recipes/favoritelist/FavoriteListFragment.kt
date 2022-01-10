package com.example.recipes.presentation.ui.recipes.favoritelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.presentation.ui.recipes.RecipeFragmentClickListener

class FavoriteListFragment : Fragment(R.layout.fragment_recipe_list) {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: RecipeDataBase

    private val viewModelFavorite: FavoriteListViewModel by viewModels {
        FavoriteListViewModelFactory(
            GetFavoriteRecipesUseCase(db),
            this
        )
    }

    private var clickListener: RecipeFragmentClickListener? = object : RecipeFragmentClickListener {
        override fun openRecipeDetailsFragment(recipe: Recipe) {
            viewModelFavorite.setRecipeToSingleton(recipe)
            findNavController().navigate(R.id.action_favoriteListFragment_to_recipeDetailsFragment)
        }
    }

    private val adapter by lazy {
        clickListener?.let { FavoriteListAdapter(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = RecipeDataBase.create(context)
        if (context is RecipeFragmentClickListener) {
            clickListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        binding.apply {
            recyclerRecipe.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerRecipe.adapter = adapter

            buttonRetry.isVisible = false
            tvErrorLoading.isVisible = false
            progressBarPaging.isVisible = false
            progressBarWhileListEmpty.isVisible = false

            bottomNavigation.selectedItemId = R.id.favoriteListFragment
//            bottomNavigation.setupWithNavController(findNavController()) // при нажатии назад selectedItem остается на предидущем значении
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.recipeSearchListFragment -> {
                        findNavController().navigate(R.id.action_favoriteListFragment_to_recipeSearchListFragment)
                        true
                    }
                    else -> false
                }
            }

            viewModelFavorite.getFavoriteRecipes()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFavorite.favoriteRecipes.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }
}