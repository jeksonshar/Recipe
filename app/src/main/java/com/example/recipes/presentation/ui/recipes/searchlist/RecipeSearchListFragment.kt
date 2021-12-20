package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.R
import com.example.recipes.business.usecases.GetRecipesBySearchUseCase
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeSearchListBinding
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.datasouce.network.RetrofitModule
import com.example.recipes.presentation.ui.dialogs.NoConnectionDialogFragment
import com.example.recipes.presentation.ui.recipes.RecipeFragmentClickListener
import com.example.recipes.presentation.ui.recipes.details.RecipeDetailsFragment
import com.example.recipes.presentation.ui.recipes.favoritelist.FavoriteListFragment
import com.example.recipes.business.utils.CheckConnectionUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeSearchListFragment : Fragment() {

    private var _binding: FragmentRecipeSearchListBinding? = null
    private val binding get() = _binding!!

    private val apiService = RetrofitModule.RECIPES_API_SERVICE

    private val viewModelSearch: RecipeSearchListViewModel by viewModels {
        RecipeSearchViewModelFactory(
            GetRecipesBySearchUseCase(apiService),
            RecipeDataStore(requireContext()),
            this
        )
    }

    private var clickListener: RecipeFragmentClickListener? = object : RecipeFragmentClickListener {
        override fun openRecipeDetailsFragment(recipe: Recipe) {

            Log.d("TAG", "openRecipeDetailsFragment: ")
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentRecipesContainer, RecipeDetailsFragment.newInstance(recipe))
                .commit()
        }
    }

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        clickListener?.let {
            RecipePagingAdapter(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecipeFragmentClickListener) {
            clickListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeSearchListBinding.inflate(inflater, container, false)
        binding.apply {
            recyclerRecipe.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerRecipe.adapter = pagingAdapter

            etSearch.doAfterTextChanged {
                viewModelSearch.liveSearchByQuery(it)
            }

            /**
             *      Альтеранативная doAfterTextChanged {..} реализация живого поиска, более тяжелая и более функцинальная
             */
/*//        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                viewModel.liveSearchByQuery(s)
//            }
//        })*/

            ivOpenSearchET.setOnClickListener {
                viewModelSearch.changeSearchIsOpenedValue(etSearch.visibility)
            }

            buttonRetry.setOnClickListener {
                pagingAdapter?.retry()
            }

            etSearch.setOnClickListener {
                val text = etSearch.text
                viewModelSearch.searchByTouch(text)
            }

            bottomNavigation.selectedItemId = R.id.nav_search

            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_favorite -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentRecipesContainer, FavoriteListFragment())
                            .commit()
                        true
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
                binding.buttonRetry.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
                binding.tvErrorLoading.text =
                    if (loadState.source.toString().contains("429")) {
                        "Да не торопись ты так\nЖди минуту!"
                    } else {
                        loadState.source.toString()
                    }
                binding.tvErrorLoading.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
            }
        }

        if (!CheckConnectionUtils.isNetConnected(requireContext())) {

//            NoConnectionDialogFragment().show(requireActivity().supportFragmentManager, null) // не подходит по логике работы диалога
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentRecipesContainer, NoConnectionDialogFragment())
                    .addToBackStack(null)
            }
        } else {
            lifecycleScope.launch {
                viewModelSearch.recipes.collectLatest { pagingData ->
                    pagingAdapter?.submitData(pagingData)
                }
            }
        }

        viewModelSearch.searchIsOpened.observe(viewLifecycleOwner, {
            binding.etSearch.visibility = it
        })

        pagingAdapter?.loadStateFlow?.asLiveData()?.observe(viewLifecycleOwner, {
            binding.progressBarPaging.isVisible = it.append is LoadState.Loading
        })
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