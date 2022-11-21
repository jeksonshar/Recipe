package com.example.recipes.presentation.ui.recipes.searchlist

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.domain.singletons.SearchEnteredSingleton
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.presentation.base.BaseFragment
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.recipes.RecipesActivityToolbarIconViewModel
import com.example.recipes.presentation.utils.LoginUtil
import com.example.recipes.presentation.utils.NewIntentUtil
import com.example.recipes.presentation.utils.SystemUtil.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeSearchListFragment : BaseFragment<FragmentRecipeListBinding>(R.layout.fragment_recipe_list) {

    private val viewModelSearch: RecipeSearchListViewModel by viewModels()
    private val viewModelToolbarIcon: RecipesActivityToolbarIconViewModel by activityViewModels()

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        RecipePagingAdapter(
            clickListener = object : RecipeClickListener {
                override fun openRecipeDetailsFragment(recipe: Recipe) {
                    viewModelSearch.setRecipeToSingleton(recipe)
                    val bundle = Bundle()
                    bundle.putString("recipeLink", recipe.shareAs)
                    findNavController().navigate(R.id.action_recipeSearchListFragment_to_recipeDetailsFragment, bundle)
                }
            },
            share = { recipe ->
                startActivity(NewIntentUtil.createNewShareIntent(recipe))
            }
        )
    }

    private lateinit var fileName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModelSearch
        binding.vmShare = viewModelToolbarIcon

        fileName = requireContext().filesDir.toString() + "/cachedRecipes.txt"
        viewModelSearch.setFileName(fileName)

        binding.apply {

            val loadStateAdapter = RecipeLoadStateAdapter { pagingAdapter.retry() }
            recyclerRecipe.adapter = pagingAdapter.withLoadStateFooter(loadStateAdapter)

            /** обработка нажатия ок - поиск и закрытие клавиатуры */
            etSearch.setOnKeyListener { _, keyCode, keyEvent ->
                if (
                    keyEvent.action == KeyEvent.ACTION_DOWN &&
                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                    keyCode == EditorInfo.IME_ACTION_DONE
                ) {
                    confirmLoadingRecipesByEnter(etSearch.text.toString())
                    return@setOnKeyListener true
                }
                false
            }

            /** обработка нажатия Apply - поиск и закрытие фильтра */
            btnFilterConfirm.setOnClickListener {
                confirmLoadingRecipesByEnter(etSearch.text.toString())
                viewModelSearch.changeFilterVisibility()
            }

            swipeToRefreshRecipeSearch.setOnRefreshListener {
// если нет интернета или запрос пустой, поиск не выполняется и прогресс swipeRefresh закрываем
                if (viewModelSearch.isNetConnected.value == true && !viewModelSearch.queryHandler.value.isNullOrBlank()) {
                    loadRecipes(viewModelSearch.queryHandler.value)
                    viewModelSearch.setRefreshingProgressVisibility(true)
                } else {
                    viewModelSearch.setRefreshingProgressVisibility(false)
                }
            }
        }

        setFiltersListener()

        pagingAdapter.addLoadStateListener { loadState ->

            when (val stateRefresh = loadState.source.refresh) {
                is LoadState.Loading -> {
                    viewModelSearch.setEmptyListImageViewVisibility(false)
                    viewModelSearch.setLoadState(loadState)
                    LoginUtil.logD("TAG", "onViewCreatedException: Paging adapter is Loading")
                }
                is LoadState.NotLoading -> {
                    viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                    viewModelSearch.setRefreshingProgressVisibility(false)
                    LoginUtil.logD("TAG", "onViewCreatedException: Paging adapter not Loading")
                }
                is LoadState.Error -> {
                    when (stateRefresh.error) {
                        is PagingSourceException.EmptyListException -> {
                            LoginUtil.logD("TAG", "onViewCreatedException: EmptyList")
                            viewModelSearch.setEmptyListImageViewVisibility(true)
                            viewLifecycleOwner.lifecycleScope.launch {
                                pagingAdapter.submitData(PagingData.empty())
                            }
                        }
                        is PagingSourceException.EndOfListException -> {
                            LoginUtil.logD("TAG", "onViewCreatedException: End of List")
                        }
                        is PagingSourceException.Response429Exception -> {
                            LoginUtil.logD("TAG", "onViewCreatedException: 429")
                            viewModelSearch.setLoadState(loadState)
                            viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                            viewModelSearch.setRefreshingProgressVisibility(false)
                        }
                    }
                }
            }
        }

        viewModelSearch.loadState.observe(viewLifecycleOwner) {
            viewModelSearch.setProgressBarWhileListEmptyVisibility(
                it?.refresh is LoadState.Loading &&
                        viewModelSearch.isRefreshingProgressBarVisible.value != true
            )
            val state = it?.source?.refresh
            if (state is LoadState.Error && state.error is PagingSourceException.Response429Exception) {
                showSnackBar(requireContext().getString(R.string.too_fast))
            }
        }

        viewModelSearch.isRefreshingProgressBarVisible.observe(viewLifecycleOwner) {
            binding.swipeToRefreshRecipeSearch.isRefreshing = it
            viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
        }

        viewModelSearch.queryHandler.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                when (pagingAdapter.itemCount) {
                    0 -> loadRecipes(it)
                }
// если в деталях смена конфига, после возврата - список не прогружается (else if решает)
            } else {
                viewModelSearch.setEmptyListImageViewVisibility(true)
                viewModelToolbarIcon.setSearchIsOpened(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    pagingAdapter.submitData(PagingData.empty())
                }
            }
        }

        viewModelSearch.isNetConnected.observe(viewLifecycleOwner) {
            if (it && SearchEnteredSingleton.isSearchEntered != true) {
                loadRecipes(viewModelSearch.queryHandler.value)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setSearchIcon(viewModelToolbarIcon.searchIsOpened.value == true)
    }

    /**
     * выполняем запрос по нажатию кнопок поиска или confirm
     * */
    private fun confirmLoadingRecipesByEnter(query: String) {
        viewModelSearch.clearSearchEntered()
        if (query != viewModelSearch.queryHandler.value) {
            viewModelSearch.setQueryToDatastore(query)
        }
        if (viewModelSearch.isNetConnected.value == true) {
            loadRecipes(query)
        } else {
            Toast.makeText(requireContext(), requireContext().getString(R.string.search_failed), Toast.LENGTH_LONG).show()
        }
        requireContext().hideKeyboard(view)
    }

    private fun setFiltersListener() {
        binding.apply {
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
        }
    }

    private fun loadRecipes(query: String?) {
        if (viewModelSearch.isNetConnected.value != true) {
            val recipeList = viewModelSearch.getFromFileCacheOfLoadRecipes(fileName)
            lifecycleScope.launch {
                pagingAdapter.submitData(PagingData.from(recipeList))
            }
        } else {
            if (!query.isNullOrBlank()) {
                lifecycleScope.launch {
// TODO 3 при использовании repeatOnLifecycle(Lifecycle.State.CREATED) {XXX} если onCreate не выполняется, то работа  XXX все равно
//  выполняется, хотя не должна. Разобраться как правильно это реализовать
                    viewModelSearch.loadRecipes(query).collectLatest {
                        viewModelSearch.setSearchEntered()
                        pagingAdapter.submitData(lifecycle, it)
                    }
                }
                viewModelSearch.sendEventToAnalytics(query)
            }
        }
    }

    private fun setSearchIcon(value: Boolean) {
        viewModelToolbarIcon.setSearchIsOpened(value)
        if (!value) {
            viewModelSearch.setFilterIsOpened(false)
        }
    }

    /** SnackBar с сообщ для клиента и кнопкой, не для информации об отсутствии соединения*/
    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(requireContext(), binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") {
            mySnackBar.dismiss()
        }
        mySnackBar.show()
    }

    override fun onDestroyView() {
        binding.recyclerRecipe.adapter = null
        super.onDestroyView()
    }

//TODO (напоминалка) создавать контракт (в случае если не используется navigation component - findNavController())
// в виде companion object для созданию объекта фрагмента (типа newInstance(.. парам))
}