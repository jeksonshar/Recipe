package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.*
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.utils.CheckConnectionUtils
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeSearchListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding: FragmentRecipeListBinding
        get() = _binding!!

    private val viewModelSearch: RecipeSearchListViewModel by viewModels()

    private var auth: FirebaseAuth? = null

    private var clickListener: RecipeClickListener? = object : RecipeClickListener {
        override fun openRecipeDetailsFragment(recipe: Recipe) {
            viewModelSearch.setRecipeToSingleton(recipe)
            findNavController().navigate(R.id.action_recipeSearchListFragment_to_recipeDetailsFragment)
        }
    }

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        clickListener?.let {
            RecipePagingAdapter(it)
        }
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
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModelSearch

        auth = Firebase.auth

        binding.apply {

            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            titleOfList.setupWithNavController(navController, appBarConfiguration)

            val header = navView.getHeaderView(0)
            header.findViewById<TextView>(R.id.headerTitle).text = auth.let { it?.currentUser?.displayName }
            header.findViewById<TextView>(R.id.headerText).text = auth.let { it?.currentUser?.email }

            val loadStateAdapter = RecipeLoadStateAdapter { pagingAdapter?.retry() }
            recyclerRecipe.adapter = pagingAdapter?.withLoadStateFooter(loadStateAdapter)

            etSearch.setOnClickListener {
                lifecycleScope.launch {
                    loadRecipes(etSearch.text.toString())
                }
            }

            bottomNavigation.selectedItemId = R.id.recipeSearchListFragment

//            bottomNavigation.setupWithNavController(findNavController())          // при нажатии назад selectedItem остается на предидущем значении
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.favoriteListFragment -> {
                        findNavController().navigate(R.id.action_recipeSearchListFragment_to_favoriteListFragment)
                        false
                    }
                    else -> false
                }
            }

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.favoriteListFragment -> {
                        findNavController().navigate(R.id.action_recipeSearchListFragment_to_favoriteListFragment)
                        drawerLayout.close()
                        false
                    }
                    R.id.recipeSearchListFragment -> {
                        drawerLayout.close()
                        false
                    }
                    R.id.signOut -> {
                        signOutUser()
                    }
                    R.id.delete -> {
                        auth?.currentUser?.delete()
                        signOutUser()
                    }
                    else -> false
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter?.addLoadStateListener { loadState ->

            when (val stateRefresh = loadState.source.refresh) {
                is LoadState.Loading -> {
                    viewModelSearch.isEmptyListImageViewVisible.value = false
                    viewModelSearch.loadState.value = loadState
                    Log.d("TAG", "onViewCreatedException: Is not Exception")
                }
                is LoadState.NotLoading -> {
                    viewModelSearch.isProgressBarWhileListEmptyVisible.value = false
                }
                is LoadState.Error -> {
                    when (stateRefresh.error) {
                        is PagingSourceException.EmptyListException -> {
                            Log.d("TAG", "onViewCreatedException: EmptyList")
                            viewModelSearch.isEmptyListImageViewVisible.value = true
                            viewLifecycleOwner.lifecycleScope.launch {
                                pagingAdapter?.submitData(PagingData.empty())
                            }
                        }
                        is PagingSourceException.EndOfListException -> {
                            Log.d("TAG", "onViewCreatedException: End of List")
                        }
                        is PagingSourceException.Response429Exception -> {
                            Log.d("TAG", "onViewCreatedException: 429")
                            viewModelSearch.loadState.value = loadState
                            viewModelSearch.isProgressBarWhileListEmptyVisible.value = false
                        }
                    }
                }
            }
        }

        viewModelSearch.loadState.observe(viewLifecycleOwner) {
            viewModelSearch.isProgressBarWhileListEmptyVisible.value = it?.refresh is LoadState.Loading
            val state = it?.source?.refresh
            if (state is LoadState.Error && state.error is PagingSourceException.Response429Exception) {
                showSnackBar(requireContext().getString(R.string.too_fast))
            }
        }

        viewModelSearch.queryHandler.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (BackPressedSingleton.isBackPressClick != true) {
                    loadRecipes(it)
                } else if (pagingAdapter?.itemCount == 0) {
                    loadRecipes(it)
                } else {
                    viewModelSearch.isProgressBarWhileListEmptyVisible.value = false
                }
// если в деталях смена конфига, после возврата - список не прогружается (else if решает)
            } else {
                viewModelSearch.isEmptyListImageViewVisible.value = true
                viewModelSearch.searchIsOpened.value = true
            }
        }

//        viewModelSearch.loadState.observe(viewLifecycleOwner) {
//            viewModelSearch.isProgressBarWhileListEmptyVisible.value = it.refresh is LoadState.Loading
//            viewModelSearch.isErrorLoadingVisible.value =
//                it.refresh !is LoadState.Loading && it.append is LoadState.Error
//            viewModelSearch.isButtonRetryVisible.value =
//                it.refresh !is LoadState.Loading && it.append is LoadState.Error
//            viewModelSearch.isProgressBarPagingVisible.value = it.append is LoadState.Loading
//            viewModelSearch.loadingError.value = if (it.source.toString().contains("429")) {
//                context?.getString(R.string.too_fast)
//            } else {
//                it.source.toString()
//            }
//        }
    }

    private fun loadRecipes(query: String?) {
        if (!CheckConnectionUtils.isNetConnected(requireContext())) {
            findNavController().navigate(R.id.action_recipeSearchListFragment_to_noConnectionDialogFragment)
        } else {
            lifecycleScope.launchWhenStarted {
                viewModelSearch.loadRecipes(query).collectLatest {
                    pagingAdapter?.submitData(it)
                }
            }
        }
    }

    private fun signOutUser(): Boolean {
        auth?.signOut()
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        viewModelSearch.resetLastQuery()
        requireActivity().finish()
        return false
    }

    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(requireContext(), binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") {
            mySnackBar.dismiss()
        }
        mySnackBar.show()
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