package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.business.domain.singletons.BackPressedSingleton
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
class RecipeSearchListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding: FragmentRecipeListBinding
        get() = _binding!!

    private val viewModelSearch: RecipeSearchListViewModel by viewModels()
    private val viewModelToolbarIcon: RecipesActivityToolbarIconViewModel by activityViewModels()

//TODO 25,10 оставить в xml поле как запасной вариант, разобраться и запустить snackBar, как основной вариант
// снекбар сейчас закомментирован

//    private lateinit var snackBarNoConnection: Snackbar

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        RecipePagingAdapter(
            clickListener = object : RecipeClickListener {
                override fun openRecipeDetailsFragment(recipe: Recipe) {
                    viewModelSearch.setRecipeToSingleton(recipe)
                    findNavController().navigate(R.id.action_recipeSearchListFragment_to_recipeDetailsFragment)
                }
            },
            share = { recipe ->
                startActivity(NewIntentUtil.createNewShareIntent(recipe))
            }
        )
    }

    private lateinit var fileName: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onAttach (RecipeSearchList) ", parameter = "+")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onCreate (RecipeSearchList) ", parameter = "+")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onCreateView (RecipeSearchList) ", parameter = "+")
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
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
                loadRecipes(viewModelSearch.queryHandler.value)
                viewModelSearch.setRefreshingProgressVisibility(true)
            }
        }

        setFiltersListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onViewCreated (RecipeSearchList) ", parameter = "+")
        pagingAdapter.addLoadStateListener { loadState ->

            when (val stateRefresh = loadState.source.refresh) {
                is LoadState.Loading -> {
                    viewModelSearch.setEmptyListImageViewVisibility(false)
                    viewModelSearch.setLoadState(loadState)
                    //TODO 28.10 вынести все логированиев Util Object
                    Log.d("TAG", "onViewCreatedException: Is not Exception")
                }
                is LoadState.NotLoading -> {
                    viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                    viewModelSearch.setRefreshingProgressVisibility(false)
                }
                is LoadState.Error -> {
                    when (stateRefresh.error) {
                        is PagingSourceException.EmptyListException -> {
                            Log.d("TAG", "onViewCreatedException: EmptyList")
                            viewModelSearch.setEmptyListImageViewVisibility(true)
                            viewLifecycleOwner.lifecycleScope.launch {
                                pagingAdapter.submitData(PagingData.empty())
                            }
                        }
                        is PagingSourceException.EndOfListException -> {
                            Log.d("TAG", "onViewCreatedException: End of List")
                        }
                        is PagingSourceException.Response429Exception -> {
                            Log.d("TAG", "onViewCreatedException: 429")
                            viewModelSearch.setLoadState(loadState)
                            viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                            viewModelSearch.setRefreshingProgressVisibility(false)
                        }
                    }
                }
            }
        }

        viewModelSearch.loadState.observe(viewLifecycleOwner) {
            viewModelSearch.setProgressBarWhileListEmptyVisibility(it?.refresh is LoadState.Loading)
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
            if (!it.isNullOrEmpty()) {
                if (pagingAdapter.itemCount == 0) {
                    Log.d("TAG", "onViewCreated785:$it")
                    loadRecipes(it)
                } else {
                    viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
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
            if (it) {
//                if (this::snackBarNoConnection.isInitialized) {
//                    dismissSnackBarNoConnection()
//                }
                loadRecipes(viewModelSearch.queryHandler.value)
            }
//            else {
//                showSnackBarNetConnection()
//            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onViewStateRestored (RecipeSearchList) ", parameter = "+")
    }

    override fun onStart() {
        super.onStart()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onStart (RecipeSearchList) ", parameter = "+")
    }

    override fun onResume() {
        super.onResume()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onResume (RecipeSearchList) ", parameter = "+")
        setSearchIcon(viewModelToolbarIcon.searchIsOpened.value == true)
    }

    /**
     * выполняем запрос по нажатию кнопок поиска или confirm
     * */
    private fun confirmLoadingRecipesByEnter(query: String) {
        if (query != viewModelSearch.queryHandler.value) {
            viewModelSearch.setQueryToDatastore(query)
        } else {
            Log.d("TAG", "onViewCreated785: 0 - ${pagingAdapter.itemCount}")
            loadRecipes(viewModelSearch.queryHandler.value)
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
//            findNavController().navigate(R.id.action_recipeSearchListFragment_to_noConnectionDialogFragment)
            val recipeList = viewModelSearch.getFromFileCacheOfLoadRecipes(fileName)
            lifecycleScope.launch {
                pagingAdapter.submitData(PagingData.from(recipeList))
            }
        } else {
            //TODO 25,10 Избавиться от BackPressed, далее разгрести всю работу без него
            if (BackPressedSingleton.isBackPressClick.value != true && !viewModelSearch.queryHandler.value.isNullOrEmpty()) {
                lifecycleScope.launchWhenStarted {
                    viewModelSearch.loadRecipes(query).collectLatest {
                        pagingAdapter.submitData(lifecycle, it)
                    }
                }
            }
            if (!query.isNullOrBlank()) {
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

    //TODO 25,10 разобраться и реализовать через SnackBar?
//    private fun showSnackBarNetConnection() {
//
//        snackBarNoConnection = Snackbar.make(
    //TODO 25,10 binding.root - использовать вместо requireView()

//            requireView(),
//            requireContext().getText(R.string.turn_on_net_connection_and_make_search),
//            Snackbar.LENGTH_INDEFINITE
//        )
//        val view = snackBarNoConnection.view
//        val snackParam = view.layoutParams as FrameLayout.LayoutParams
//        snackParam.gravity = Gravity.TOP
//        view.layoutParams = snackParam
//        snackBarNoConnection.setBackgroundTint(requireContext().getColor(R.color.background_snack_attention))
//        snackBarNoConnection.show()
//    }

//    private fun dismissSnackBarNoConnection() {
//        if (this::snackBarNoConnection.isInitialized) {
//            snackBarNoConnection.dismiss()
//        }
//    }

    /** SnackBar с сообщ для клиента и кнопкой, не для информации об отсутствии соединения*/
    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(requireContext(), binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") {
            mySnackBar.dismiss()
        }
        mySnackBar.show()
    }

    override fun onPause() {
        super.onPause()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onPause (RecipeSearchList) ", parameter = "+")
    }

    override fun onStop() {
        super.onStop()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onStop (RecipeSearchList) ", parameter = "+")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onSaveInstanceState (RecipeSearchList) ", parameter = "+")
    }

    override fun onDestroyView() {
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDestroyView (RecipeSearchList) ", parameter = "+")
        binding.recyclerRecipe.adapter = null
        _binding = null
//        dismissSnackBarNoConnection()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDestroy (RecipeSearchList) ", parameter = "+")
    }

    override fun onDetach() {
        super.onDetach()
        LoginUtil.logD(tag = "LiveCycleMethod:", startMsg = "onDetach (RecipeSearchList) ", parameter = "+")
    }

//TODO 24,10  создавать контракт (в случае если не используется navigation component - findNavController())
// в виде companion object для созданию объекта фрагмента (типа newInstance(.. парам))
}