package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
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
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
        firebaseAnalytics = Firebase.analytics

        binding.apply {

            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            titleOfList.setupWithNavController(navController, appBarConfiguration)

            val header = navView.getHeaderView(0)
            header.findViewById<TextView>(R.id.headerTitle).text = auth.let { it?.currentUser?.displayName }
            header.findViewById<TextView>(R.id.headerText).text = auth.let { it?.currentUser?.email }

            val loadStateAdapter = RecipeLoadStateAdapter { pagingAdapter?.retry() }
            recyclerRecipe.adapter = pagingAdapter?.withLoadStateFooter(loadStateAdapter)

            etSearch.setOnKeyListener { _, i, keyEvent ->
                if (
                    keyEvent != null &&
                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                    i == EditorInfo.IME_ACTION_DONE
                ) {
                    viewModelSearch.setQueryToDatastore(etSearch.text.toString())
                    viewModelSearch.changeSearchIsOpenedValue()
                }
                return@setOnKeyListener false
            }

            btnFilterConfirm.setOnClickListener {
                if (etSearch.text.toString() != viewModelSearch.queryHandler.value) {
                    viewModelSearch.setQueryToDatastore(etSearch.text.toString())
                } else {
                    lifecycleScope.launch {
                        loadRecipes(viewModelSearch.queryHandler.value)
                    }
                }
                viewModelSearch.changeFilterVisibility()
            }

            // start - сделать красивее?
            filterDietGroup.setOnCheckedStateChangeListener { _, _ ->
                chipBalanced.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
                chipHighFiber.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
                chipHighProtein.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
                chipLowCarb.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
                chipLowFat.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
                chipLowSodium.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateDietFilter(compoundButton.id, b)
                }
            }
            filterHealthGroup.setOnCheckedStateChangeListener { _, _ ->
                chipAlcoholCocktail.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipAlcoholFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipCeleryFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipCrustceanFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipDairyFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipEggFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipFishFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipGlutenFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipImmunoSupportive.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipKosher.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipLowSugar.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipMolluskFree.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
                chipVegetarian.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateHealthFilter(compoundButton.id, b)
                }
            }
            filterCuisineTypesGroup.setOnCheckedStateChangeListener { _, _ ->
                chipAmerican.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipAsian.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipBritish.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipChinese.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipFrench.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipGreek.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipIndian.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipItalian.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipKorean.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
                chipMexican.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateCuisineTypeFilter(compoundButton.id, b)
                }
            }
            filterMealTypeGroup.setOnCheckedStateChangeListener { _, _ ->
                chipBreakfast.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
                chipBrunch.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
                chipLunch.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
                chipDinner.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
                chipSnack.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
                chipTeatime.setOnCheckedChangeListener { compoundButton, b ->
                    viewModelSearch.changeCheckedStateMealTypeFilter(compoundButton.id, b)
                }
            }
            // end - go to start

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
                    R.id.settings -> {
                        findNavController().navigate(R.id.action_recipeSearchListFragment_to_userProfileFragment)
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
                viewLifecycleOwner.lifecycleScope.launch {
                    pagingAdapter?.submitData(PagingData.empty())
                }
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
            if (query != null) {
                firebaseAnalytics.logEvent("search_clicked") {
                    param(FirebaseAnalytics.Param.ITEM_ID, query.hashCode().toString())
                    param(FirebaseAnalytics.Param.ITEM_NAME, query)
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "search_clicked")
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