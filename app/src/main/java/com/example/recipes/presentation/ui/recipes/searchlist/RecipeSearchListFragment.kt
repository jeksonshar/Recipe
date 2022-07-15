package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.utils.CheckConnectionUtils
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.LoadedRecipesSingleton
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.registration.RegistrationActivity
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

            recyclerRecipe.adapter = pagingAdapter

            ivOpenSearchET.setOnClickListener {
                viewModelSearch.changeSearchIsOpenedValue(etSearch.visibility)
            }

            buttonRetry.setOnClickListener {
                pagingAdapter?.retry()
            }

            etSearch.setOnClickListener {
                viewModelSearch.searchByTouch(etSearch.text)
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

        lifecycleScope.launch {
            pagingAdapter?.loadStateFlow?.collectLatest { loadState ->
                binding.progressBarWhileListEmpty.isVisible = loadState.refresh is LoadState.Loading
                binding.buttonRetry.isVisible =
                    loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
                binding.tvErrorLoading.text =
                    if (loadState.source.toString().contains("429")) {
                        "Да не торопись ты так\nЖди минуту!"
                    } else {
                        loadState.source.toString()
                    }
                binding.tvErrorLoading.isVisible =
                    loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModelSearch.queryHandler.collect {
                // много ифов, нужно сделать красиво и понятно!!! LoadingRecipesSingleton - на костыль похоже
                if (!it.isNullOrEmpty()) {
                    if (BackPressedSingleton.isBackPressClick == null) { // нажата назад - не скачиваем данные, а берем имеющиеся значения
                        loadRecipes(it)
                    } else {
                        if (LoadedRecipesSingleton.isDataLoaded == null) { // если имеющихся значений нет - скачиваем
                            loadRecipes(it)
                        }
                    }
                } else {
                    viewModelSearch.changeSearchIsOpenedValue(binding.etSearch.visibility)
                    binding.apply {
                        buttonRetry.visibility = View.GONE
                        tvErrorLoading.visibility = View.GONE
                        progressBarPaging.visibility = View.GONE
                        progressBarWhileListEmpty.visibility = View.GONE
                        ivEmptyList.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModelSearch.searchIsOpened.observe(viewLifecycleOwner) {
            binding.etSearch.visibility = it
            if (it == RecipeSearchListViewModel.VIEW_VISIBLE) {
                binding.ivOpenSearchET.setImageResource(R.drawable.up_arrow)
            } else {
                binding.ivOpenSearchET.setImageResource(R.drawable.search)
            }
        }

        pagingAdapter?.loadStateFlow?.asLiveData()?.observe(viewLifecycleOwner) {
            binding.progressBarPaging.isVisible = it.append is LoadState.Loading
        }
    }

    private suspend fun loadRecipes(query: String) {
        if (!CheckConnectionUtils.isNetConnected(requireContext())) {
            findNavController().navigate(R.id.action_recipeSearchListFragment_to_noConnectionDialogFragment)
        } else {
            viewModelSearch.recipes(query)?.collectLatest { pagingData ->
                LoadedRecipesSingleton.isDataLoaded = true
                binding.ivEmptyList.visibility = View.GONE
                pagingAdapter?.submitData(pagingData)
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