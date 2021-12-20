package com.example.recipes.presentation.ui.recipes.favoritelist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.R
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeSearchListBinding
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.presentation.ui.recipes.RecipeFragmentClickListener
import com.example.recipes.presentation.ui.recipes.details.RecipeDetailsFragment
import com.example.recipes.presentation.ui.recipes.searchlist.RecipeSearchListFragment

class FavoriteListFragment : Fragment() {

    private var _binding: FragmentRecipeSearchListBinding? = null
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

            Log.d("TAG", "openRecipeDetailsFragment: ")
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentRecipesContainer, RecipeDetailsFragment.newInstance(recipe))
                .commit()
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
        _binding = FragmentRecipeSearchListBinding.inflate(inflater, container, false)
        binding.apply {
            recyclerRecipe.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerRecipe.adapter = adapter

            buttonRetry.isVisible = false
            tvErrorLoading.isVisible = false
            progressBarPaging.isVisible = false
            progressBarWhileListEmpty.isVisible = false

            bottomNavigation.selectedItemId = R.id.nav_favorite
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_search -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentRecipesContainer, RecipeSearchListFragment())
                            .commit()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }
}