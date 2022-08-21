package com.example.recipes.presentation.ui.recipes.favoritelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentFavoriteRecipeListBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteRecipeListBinding? = null
    private val binding: FragmentFavoriteRecipeListBinding
        get() = _binding!!

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
        binding.apply {

            val auth = Firebase.auth

            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            binding.titleOfList.setupWithNavController(navController, appBarConfiguration)

            val header = navView.getHeaderView(0)
            header.findViewById<TextView>(R.id.headerTitle).text = auth.currentUser?.displayName
            header.findViewById<TextView>(R.id.headerText).text = auth.currentUser?.email

            recyclerRecipe.adapter = adapter

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

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.favoriteListFragment -> {
                        drawerLayout.close()
                        false
                    }
                    R.id.recipeSearchListFragment -> {
                        findNavController().navigate(R.id.action_favoriteListFragment_to_recipeSearchListFragment)
                        false
                    }
                    R.id.signOut -> {
                        Firebase.auth.signOut()
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                        requireActivity().finish()
                        false
                    }
                    else -> false
                }
            }
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
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }
}