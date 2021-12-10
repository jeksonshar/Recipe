package com.example.recipes.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.datasouce.network.RetrofitModule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding

    private val apiService = RetrofitModule.RECIPES_API_SERVICE

    private val viewModel: RecipeListViewModel by viewModels {
        MyViewModelFactory(RecipesUseCase(apiService), this)
    }

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        RecipePagingAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerRecipe?.layoutManager = GridLayoutManager(view.context, 2)
        binding?.recyclerRecipe?.adapter = pagingAdapter

        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadState ->
                binding?.progressBarWhileListEmpty?.isVisible = loadState.refresh is LoadState.Loading
                binding?.buttonRetry?.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
                binding?.tvErrorLoading?.text = loadState.source.toString()
                binding?.tvErrorLoading?.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
            }
        }

        lifecycleScope.launch {
            viewModel.recipes.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
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

        binding?.etSearch?.doAfterTextChanged {
            viewModel.liveSearchByQuery(it)
        }

        binding?.ivOpenSearchET?.setOnClickListener {
            viewModel.changeSearchIsOpenedValue(binding?.etSearch?.visibility ?: 0)
        }

        binding?.buttonRetry?.setOnClickListener {
            pagingAdapter.retry()
        }

        viewModel.searchIsOpened.observe(viewLifecycleOwner, {
            binding?.etSearch?.visibility = it
        })

        pagingAdapter.loadStateFlow.asLiveData().observe(viewLifecycleOwner, {
            binding?.progressBarPaging?.isVisible = it.append is LoadState.Loading
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}