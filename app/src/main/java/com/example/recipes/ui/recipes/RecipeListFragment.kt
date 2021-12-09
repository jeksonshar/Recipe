package com.example.recipes.ui.recipes

import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding

    //    private var adapter: RecipeListAdapter = RecipeListAdapter(emptyList())
    private val apiService = RetrofitModule.RECIPES_API_SERVICE

//    private val viewBinding by viewBinding(FragmentRecipeListBinding::bind)

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

//        viewBinding.recyclerRecipe.adapter = adapter
        binding?.recyclerRecipe?.layoutManager = GridLayoutManager(view.context, 2)
        binding?.recyclerRecipe?.adapter = pagingAdapter/*.withLoadStateFooter(
            footer = RecipesLoadStateAdapter { pagingAdapter.retry() }
        )*/

        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadState ->
//                binding?.progressBarPaging?.isVisible = loadState.refresh is LoadState.Loading
                binding?.progressBarWhileListEmpty?.isVisible = loadState.refresh is LoadState.Loading
                binding?.buttonRetry?.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
                binding?.tvErrorLoading?.text = loadState.source.toString()
                binding?.tvErrorLoading?.isVisible = loadState.refresh !is LoadState.Loading && loadState.append is LoadState.Error
            }
        }

//        lifecycleScope.launch {
//            viewModel.fetchRecipesForPagingAdapter().collectLatest {
//                viewModel.setDataLoading(false)
//                pagingAdapter.submitData(it)
//            }
//        }
//
//        viewModel.dataLoading.observe(viewLifecycleOwner, {
//            binding?.progressBarPaging?.isVisible = it
//        })

        lifecycleScope.launch {
            viewModel.recipes().collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }

        binding?.buttonRetry?.setOnClickListener {
            pagingAdapter.retry()
        }

        pagingAdapter.loadStateFlow.asLiveData().observe(viewLifecycleOwner, {
            binding?.progressBarPaging?.isVisible = it.append is LoadState.Loading
        })

//        recycler = binding?.recyclerRecipe
//        val layout = GridLayoutManager(view.context, 2)
//        recycler?.layoutManager = layout
//        recycler?.adapter = adapter

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
//
//        binding?.etSearch?.setOnClickListener {
//            val text = binding?.etSearch?.text
//            viewModel.searchByTouch(text)
//        }
//
        binding?.ivOpenSearchET?.setOnClickListener {
            viewModel.changeSearchIsOpenedValue(binding?.etSearch?.visibility ?: 0)
        }
//
//        viewModel.recipesRequest.observe(viewLifecycleOwner, {
//            val list = viewModel.entityToData(it)
//            Log.d("TAG", "list recipe: $list")
//            adapter.submitList(list)
//
//        })
//
//        viewModel.showUpdateProgress.observe(viewLifecycleOwner, {
//            if (!it) binding?.progressBarWhileListEmpty?.visibility = View.GONE
//        })
//
        viewModel.searchIsOpened.observe(viewLifecycleOwner, {
            binding?.etSearch?.visibility = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}