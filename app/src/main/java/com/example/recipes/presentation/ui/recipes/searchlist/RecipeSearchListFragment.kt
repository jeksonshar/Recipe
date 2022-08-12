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
import com.example.recipes.presentation.ui.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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

            recyclerRecipe.adapter = pagingAdapter

            buttonRetry.setOnClickListener {
                pagingAdapter?.retry()
            }

            etSearch.setOnClickListener {
//                viewModelSearch.searchByTouch(etSearch.text)
                lifecycleScope.launch {
                    loadRecipes(etSearch.text.toString())
                }
            }

//            etSearch.setOnEditorActionListener { _, actionId: Int, event: KeyEvent ->
//                if ((event.keyCode == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
//                    loadRecipes()
//                }
//                return@setOnEditorActionListener false
//            }

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

        lifecycleScope.launch {
            pagingAdapter?.loadStateFlow?.collectLatest { loadState ->

                val stateRefresh = loadState.source.refresh
                val stateAppend = loadState.source.append
                when {
/*
почему если поставить сначала все stateRefresh а за ними все stateAppend то
isProgressBarPagingVisible не активируется при скролинге?
*/
                    stateAppend is LoadState.Loading -> {
                        viewModelSearch.loadState.value = loadState
                    }
                    stateAppend is LoadState.Error -> {
                        when {
                            stateAppend.error is PagingSourceException.Response429Exception -> {
                                viewModelSearch.loadState.value = loadState
                            }
                            loadState.prepend.endOfPaginationReached -> {
                                viewModelSearch.isProgressBarPagingVisible.value = false
                            }
                        }
                    }
                    stateRefresh is LoadState.Loading -> {
                        viewModelSearch.isEmptyListImageViewVisible.value = false
                        viewModelSearch.loadState.value = loadState
                    }
                    stateRefresh is LoadState.NotLoading -> {
//      почему тут не работает setValue?
                        viewModelSearch.isProgressBarPagingVisible.postValue(false)
                        viewModelSearch.isProgressBarWhileListEmptyVisible.postValue(false)
                    }
                    stateRefresh is LoadState.Error -> {
                        when (stateRefresh.error) {
                            is PagingSourceException.EmptyListException -> {
                                viewModelSearch.isEmptyListImageViewVisible.value = true
                                pagingAdapter?.submitData(PagingData.empty())
                            }
                             is HttpException -> {
                                 viewModelSearch.loadState.value = loadState
                             }
                        }
                    }
                }
            }
        }

        viewModelSearch.queryHandler.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (BackPressedSingleton.isBackPressClick != true) {
                    lifecycleScope.launch {
                        loadRecipes(it)
                    }
                }
            } else {
                viewModelSearch.isEmptyListImageViewVisible.value = true
                viewModelSearch.searchIsOpened.value = true
            }
        }

        viewModelSearch.loadState.observe(viewLifecycleOwner) {
            viewModelSearch.isProgressBarWhileListEmptyVisible.value = it.refresh is LoadState.Loading
            viewModelSearch.isErrorLoadingVisible.value =
                it.refresh !is LoadState.Loading && it.append is LoadState.Error
            viewModelSearch.isButtonRetryVisible.value =
                it.refresh !is LoadState.Loading && it.append is LoadState.Error
            viewModelSearch.isProgressBarPagingVisible.value = it.append is LoadState.Loading
            viewModelSearch.loadingError.value = if (it.source.toString().contains("429")) {
                context?.getString(R.string.too_fast)
            } else {
                it.source.toString()
            }
        }
    }

    private suspend fun loadRecipes(query: String?) {
        if (!CheckConnectionUtils.isNetConnected(requireContext())) {
            findNavController().navigate(R.id.action_recipeSearchListFragment_to_noConnectionDialogFragment)
        } else {
            viewModelSearch.loadRecipes(query).collectLatest {
                pagingAdapter?.submitData(it)
            }
        }
    }

    private fun signOutUser(): Boolean {
        auth?.signOut()
        startActivity(Intent(requireContext(), RegistrationActivity::class.java))
        viewModelSearch.resetLastQuery()
        requireActivity().finish()
        return false
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