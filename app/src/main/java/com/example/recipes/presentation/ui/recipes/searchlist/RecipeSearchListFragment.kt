package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.recipes.RecipesActivity
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

//TODO по-моему лучще оставить в xml поле, но Влад ставил ТЗ - snackBar, еслине критично - оставляем xml,
// снекбар сейчас закомментирован

//    private lateinit var snackBarNoConnection: Snackbar

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

    private lateinit var fileName: String

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

        fileName = context?.filesDir.toString() + "/cachedRecipes.txt"
        viewModelSearch.setFileName(fileName)

        (activity as RecipesActivity).bindingActivity.ivOpenSearchET.setOnClickListener {
            viewModelSearch.changeSearchIsOpenedValue()
        }

        binding.apply {

            val loadStateAdapter = RecipeLoadStateAdapter { pagingAdapter?.retry() }
            recyclerRecipe.adapter = pagingAdapter?.withLoadStateFooter(loadStateAdapter)

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

            bottomNavigation.selectedItemId = R.id.recipeSearchListFragment

//            bottomNavigation.setupWithNavController(findNavController())          // при нажатии назад selectedItem остается на предидущем значении
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.favoriteListFragment -> {
                        findNavController().navigate(R.id.action_recipeSearchListFragment_to_favoriteListFragment)
                        false
                    }
                    R.id.userProfileFragment -> {
                        findNavController().navigate(R.id.action_recipeSearchListFragment_to_userProfileFragment)
                        false
                    }
                    else -> false
                }
            }
        }

        setFiltersListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter?.addLoadStateListener { loadState ->

            when (val stateRefresh = loadState.source.refresh) {
                is LoadState.Loading -> {
                    viewModelSearch.setEmptyListImageViewVisibility(false)
                    viewModelSearch.setLoadState(loadState)
                    Log.d("TAG", "onViewCreatedException: Is not Exception")
                }
                is LoadState.NotLoading -> {
                    viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                }
                is LoadState.Error -> {
                    when (stateRefresh.error) {
                        is PagingSourceException.EmptyListException -> {
                            Log.d("TAG", "onViewCreatedException: EmptyList")
                            viewModelSearch.setEmptyListImageViewVisibility(true)
                            viewLifecycleOwner.lifecycleScope.launch {
                                pagingAdapter?.submitData(PagingData.empty())
                            }
                        }
                        is PagingSourceException.EndOfListException -> {
                            Log.d("TAG", "onViewCreatedException: End of List")
                        }
                        is PagingSourceException.Response429Exception -> {
                            Log.d("TAG", "onViewCreatedException: 429")
                            viewModelSearch.setLoadState(loadState)
                            viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
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

        viewModelSearch.queryHandler.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (BackPressedSingleton.isBackPressClick.value != true) {
                    Log.d("TAG", "onViewCreated785: 1 - ${pagingAdapter?.itemCount}")
                    loadRecipes(it)
                } else if (pagingAdapter?.itemCount == 0) {
                    Log.d("TAG", "onViewCreated785: 2 - ${pagingAdapter?.itemCount}")
                    loadRecipes(it)
                } else {
                    viewModelSearch.setProgressBarWhileListEmptyVisibility(false)
                }
// если в деталях смена конфига, после возврата - список не прогружается (else if решает)
            } else {
                viewModelSearch.setEmptyListImageViewVisibility(true)
                viewModelSearch.setSearchIsOpened(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    pagingAdapter?.submitData(PagingData.empty())
                }
            }
        }

        viewModelSearch.searchIsOpened.observe(viewLifecycleOwner) {
            Log.d("TAG", "setSearchIcon: observe = ")
            setSearchIcon(it)
        }

        //удалить после того, как станет понятно с тулбаром, вопрос в туду
//        viewModelSearch.isNetConnected.observe(viewLifecycleOwner) {
//            Log.d("TAG", "onViewCreated: $it")
//            if (!it) {
//                showSnackBarNetConnection()
//            } else {
//                if (this::snackBarNoConnection.isInitialized) {
//                    dismissSnackBarNoConnection()
//                }
//                Log.d("TAG", "onViewCreated785: 3 - ${pagingAdapter?.itemCount}")
//            }
//        }
    }

//TODO!!! 22.10 по логике, если запрс пуст, то должна открыться строка поиска и значек смениться на стрелку,
// в другом месте это не работает
    override fun onResume() {
        super.onResume()
//TODO!!! 22.10 если открыть поле поиска - ярлык стрелка, но если после этого войти в детали и выйти, то поиск открыт,
// а ярлык - лупа, т.е не соответствует, хотя searchIsOpened - верное значение, что-то с жизненными циклами, разобраться!!!!
        Log.d("TAG", "setSearchIcon: search is open = ${viewModelSearch.searchIsOpened.value}")
        setSearchIcon(viewModelSearch.searchIsOpened.value == true)
    }

    /**
     * выполняем запрос по нажатию кнопок поиска или confirm
     * */
    private fun confirmLoadingRecipesByEnter(query: String) {
        if (query != viewModelSearch.queryHandler.value) {
            viewModelSearch.setQueryToDatastore(query)
        } else {
            Log.d("TAG", "onViewCreated785: 0 - ${pagingAdapter?.itemCount}")
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
                pagingAdapter?.submitData(PagingData.from(recipeList))
            }
        } else {
            lifecycleScope.launchWhenStarted {
                viewModelSearch.loadRecipes(query).collectLatest {
                    pagingAdapter?.submitData(lifecycle, it)
                }
            }
            if (!query.isNullOrBlank()) {
                viewModelSearch.sendEventToAnalytics(query)
            }
        }
    }

// нужно тут, поскольку ресурс поля активити зависит от сосотояния поля vm фрагмента,
// передавать vm этого фрагмента в xml активити не вариант
    private fun setSearchIcon(value: Boolean) {
        if (value) {
            Log.d("TAG", "setSearchIcon: arrow_up")
            (activity as RecipesActivity).bindingActivity.ivOpenSearchET.setImageResource(R.drawable.arrow_up)
        } else {
            Log.d("TAG", "setSearchIcon: search")
            (activity as RecipesActivity).bindingActivity.ivOpenSearchET.setImageResource(R.drawable.search)
            viewModelSearch.setFilterIsOpened(false)
        }
    }

    //TODO отображения отсутствия соединения сделал через textView в xml, со снэкбаром не разобрался,
    // не получилось разместить его между Toolbar Activity и recycler фрагмента. времени мого потратил.
    // Влад - оставить так или разобраться и реализовать через SnackBar?
//    private fun showSnackBarNetConnection() {
//
//        snackBarNoConnection = Snackbar.make(
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

    override fun onDestroyView() {
        binding.recyclerRecipe.adapter = null
        _binding = null
//        dismissSnackBarNoConnection()
        super.onDestroyView()
    }

    override fun onDetach() {
        clickListener = null
        super.onDetach()
    }
}