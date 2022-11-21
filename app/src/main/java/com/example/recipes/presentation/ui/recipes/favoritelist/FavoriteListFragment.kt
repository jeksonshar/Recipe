package com.example.recipes.presentation.ui.recipes.favoritelist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentFavoriteRecipeListBinding
import com.example.recipes.presentation.base.BaseFragment
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : BaseFragment<FragmentFavoriteRecipeListBinding>(R.layout.fragment_favorite_recipe_list) {

    private val viewModelFavorite: FavoriteListViewModel by viewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModelFavorite

        viewModelFavorite.getUserFavoriteRecipes()

        binding.recyclerRecipe.adapter = adapter

        viewModelFavorite.favoriteRecipes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        binding.recyclerRecipe.adapter = null
        super.onDestroyView()
    }

}