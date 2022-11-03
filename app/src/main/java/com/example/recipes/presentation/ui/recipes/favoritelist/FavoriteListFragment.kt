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
import com.example.recipes.databinding.FragmentFavoriteRecipeListBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.utils.LoginUtil
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onAttach (FavoriteList) ", parameter = "+")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onCreate (FavoriteList) ", parameter = "+")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onCreateView (FavoriteList) ", parameter = "+")
        _binding = FragmentFavoriteRecipeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelFavorite

        viewModelFavorite.getUserFavoriteRecipes()

        binding.recyclerRecipe.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onViewCreated (FavoriteList) ", parameter = "+")
        viewModelFavorite.favoriteRecipes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onViewStateRestored (FavoriteList) ", parameter = "+")
    }

    override fun onStart() {
        super.onStart()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onStart (FavoriteList) ", parameter = "+")
    }

    override fun onResume() {
        super.onResume()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onResume (FavoriteList) ", parameter = "+")
    }

    override fun onPause() {
        super.onPause()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onPause (FavoriteList) ", parameter = "+")
    }

    override fun onStop() {
        super.onStop()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onStop (FavoriteList) ", parameter = "+")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onSaveInstanceState (FavoriteList) ", parameter = "+")
    }

    override fun onDestroyView() {
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDestroyView (FavoriteList) ", parameter = "+")
        binding.recyclerRecipe.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDestroy (FavoriteList) ", parameter = "+")
    }

    override fun onDetach() {
        super.onDetach()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDetach (FavoriteList) ", parameter = "+")
    }

}