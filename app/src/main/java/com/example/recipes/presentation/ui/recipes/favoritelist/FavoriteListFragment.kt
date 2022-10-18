package com.example.recipes.presentation.ui.recipes.favoritelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.ActivityRecipesBinding
import com.example.recipes.databinding.FragmentFavoriteRecipeListBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteRecipeListBinding? = null
    private val binding: FragmentFavoriteRecipeListBinding
        get() = _binding!!

    private var _bindingActivity: ActivityRecipesBinding? = null
    private val bindingActivity: ActivityRecipesBinding
        get() = _bindingActivity!!


    private val viewModelFavorite: FavoriteListViewModel by viewModels()

    private var clickListener: RecipeClickListener? = object : RecipeClickListener {
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
        if (context is RecipeClickListener) {
            clickListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteRecipeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        _bindingActivity = (activity as RecipesActivity).bindingActivity
        bindingActivity.lifecycleOwner = this

        bindingActivity.apply {
            btnBack.visibility = View.VISIBLE
            tvTitleOfList.setText(R.string.favorite_recipes)
            ivOpenSearchET.visibility = View.INVISIBLE

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        binding.apply {

            recyclerRecipe.adapter = adapter

            bottomNavigation.selectedItemId = R.id.favoriteListFragment
//            bottomNavigation.setupWithNavController(findNavController()) // при нажатии назад selectedItem остается на предидущем значении
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.recipeSearchListFragment -> {
                        findNavController().navigate(R.id.action_favoriteListFragment_to_recipeSearchListFragment)
                        false
                    }
                    R.id.profileFragment -> {
                        findNavController().navigate(R.id.action_favoriteListFragment_to_userProfileFragment)
                        false
                    }
                    else -> false
                }
            }

            viewModelFavorite.getUserFavoriteRecipes()

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFavorite.favoriteRecipes.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
            if (it.isEmpty()) {
                binding.ivEmptyList.setImageResource(R.drawable.empty_favorite_list_removebg_preview)
                binding.ivEmptyList.visibility = View.VISIBLE
            } else {
                binding.ivEmptyList.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerRecipe.adapter = null
        _binding = null
        _bindingActivity = null
        super.onDestroyView()
    }

    override fun onDetach() {
        clickListener = null
        super.onDetach()
    }
}