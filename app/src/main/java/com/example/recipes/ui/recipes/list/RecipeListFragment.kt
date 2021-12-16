package com.example.recipes.ui.recipes.list

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
import com.example.recipes.data.Recipe
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.datasouce.network.RetrofitModule
import com.example.recipes.business.usecases.GetRecipeUseCase
import com.example.recipes.business.usecases.GetRecipeListUseCase
import com.example.recipes.datasouce.RecipeDataStore
import com.example.recipes.ui.dialogs.NoConnectionDialogFragment
import com.example.recipes.ui.recipes.MyViewModelFactory
import com.example.recipes.ui.recipes.details.RecipeDetailsFragment
import com.example.recipes.utils.CheckConnectionUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private val apiService = RetrofitModule.RECIPES_API_SERVICE

    private val viewModel: RecipeListViewModel by viewModels {
        MyViewModelFactory(GetRecipeListUseCase(apiService), GetRecipeUseCase(apiService), RecipeDataStore(requireContext()), this)
    }

    private var clickListener: RecipeFragmentClickListener? = object : RecipeFragmentClickListener {
        override fun openRecipeDetailsFragment(recipe: Recipe) {

            Log.d("TAG", "openRecipeDetailsFragment: ")
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentRecipesContainer, RecipeDetailsFragment.newInstance(recipe.uri.substringAfter('_')))
                .commit()
        }
    }

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        clickListener?.let { RecipePagingAdapter(it) }
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
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        binding.apply {
            recyclerRecipe.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerRecipe.adapter = pagingAdapter

            etSearch.doAfterTextChanged {
                viewModel.liveSearchByQuery(it)
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
                viewModel.changeSearchIsOpenedValue(etSearch.visibility)
            }

            buttonRetry.setOnClickListener {
                pagingAdapter?.retry()
            }

            etSearch.setOnClickListener {
                val text = etSearch.text
                viewModel.searchByTouch(text)
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
                    if (loadState.source.toString().contains("429")) "Да не торопись ты так\nЖди минуту!" else loadState.source.toString()
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
                viewModel.recipes.collectLatest { pagingData ->
                    pagingAdapter?.submitData(pagingData)
                }
            }
        }

        viewModel.searchIsOpened.observe(viewLifecycleOwner, {
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