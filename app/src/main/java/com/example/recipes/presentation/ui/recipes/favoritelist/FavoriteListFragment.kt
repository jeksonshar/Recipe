package com.example.recipes.presentation.ui.recipes.favoritelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentFavoriteRecipeListBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteRecipeListBinding? = null
    private val binding: FragmentFavoriteRecipeListBinding
        get() = _binding!!

    private val viewModelFavorite: FavoriteListViewModel by viewModels()
    // TODO 26,10 убрать из тулбара в фаворит лист кнопку назад

    private val adapter by lazy {
        FavoriteListAdapter(
            clickListener = object : RecipeClickListener {
                override fun openRecipeDetailsFragment(recipe: Recipe) {
                    viewModelFavorite.setRecipeToSingleton(recipe)
                    findNavController().navigate(R.id.action_favoriteListFragment_to_recipeDetailsFragment)
                }
            },
            /**
             * Если параметр share в адаптере отсутствует, то кнопка шаринга рецепта в итеме скрыта,
             * если параметр раскоментировать, кнопка шаринга будет шарить выбранный рецепт (итем) через соцсети
             */
//            share = { recipe ->
//                startActivity(NewShareIntent.createNewShareIntent(recipe))
//            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteRecipeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelFavorite

        viewModelFavorite.getUserFavoriteRecipes()

        binding.apply {

            recyclerRecipe.adapter = adapter

//            bottomNavigation.selectedItemId = R.id.favoriteListFragment
//            bottomNavigation.setupWithNavController(findNavController()) // при нажатии назад selectedItem остается на предидущем значении
//            bottomNavigation.setOnItemSelectedListener {
//                when (it.itemId) {
//                    R.id.recipeSearchListFragment -> {
//                        findNavController().navigate(R.id.action_favoriteListFragment_to_recipeSearchListFragment)
//                        false
//                    }
//                    R.id.userProfileFragment -> {
//                        findNavController().navigate(R.id.action_favoriteListFragment_to_userProfileFragment)
//                        false
//                    }
//                    else -> false
//                }
//            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFavorite.favoriteRecipes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        binding.recyclerRecipe.adapter = null
        _binding = null
        super.onDestroyView()
    }

}