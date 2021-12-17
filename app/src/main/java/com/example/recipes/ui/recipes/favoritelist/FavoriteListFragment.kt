package com.example.recipes.ui.recipes.favoritelist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.R
import com.example.recipes.business.usecases.GetFavoriteRecipesUseCase
import com.example.recipes.data.Recipe
import com.example.recipes.databinding.FragmentRecipeSearchListBinding
import com.example.recipes.datasouce.room.RecipeDataBase
import com.example.recipes.ui.recipes.RecipeFragmentClickListener
import com.example.recipes.ui.recipes.details.RecipeDetailsFragment

class FavoriteListFragment : Fragment() {

    private var _binding: FragmentRecipeSearchListBinding? = null
    private val binding get() = _binding!!

    private val db = RecipeDataBase.create(requireContext())

    val viewModelFavorite: FavoriteListViewModel by viewModels {
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
                .replace(R.id.fragmentRecipesContainer, RecipeDetailsFragment.newInstance(recipe.uri.substringAfter('_')))
                .commit()
        }
    }

    private val adapter by lazy {
        clickListener?.let { FavoriteListAdapter(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

            viewModelFavorite.getFavoriteRecipes()
        }
        return binding.root
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