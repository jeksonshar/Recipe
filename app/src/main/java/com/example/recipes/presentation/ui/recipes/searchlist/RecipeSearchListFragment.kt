package com.example.recipes.presentation.ui.recipes.searchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.utils.CheckConnectionUtils
import com.example.recipes.databinding.FragmentRecipeListBinding
import com.example.recipes.presentation.ui.recipes.BackPressedSingleton
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.ui.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeSearchListFragment : Fragment(R.layout.fragment_recipe_list) {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private val viewModelSearch: RecipeSearchListViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

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
        binding.apply {

            auth = Firebase.auth
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
            titleOfList.setupWithNavController(navController, appBarConfiguration)

            val header = navView.getHeaderView(0)
            header.findViewById<TextView>(R.id.headerTitle).text = auth.currentUser?.displayName
            header.findViewById<TextView>(R.id.headerText).text = auth.currentUser?.email

            recyclerRecipe.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerRecipe.adapter = pagingAdapter

            etSearch.doAfterTextChanged {
                viewModelSearch.liveSearchByQuery(it)
            }

            /**
            Альтеранативная doAfterTextChanged {..} реализация живого поиска, более тяжелая и более функцинальная

            binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            viewModel.liveSearchByQuery(s)
            }
            })
             */


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
                when(it.itemId) {
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
                        auth.signOut()
                        startActivity(Intent(requireContext(), RegistrationActivity::class.java))
                        viewModelSearch.resetLastQuery()
                        requireActivity().finish()
                        false
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
            findNavController().navigate(R.id.action_recipeSearchListFragment_to_noConnectionDialogFragment)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModelSearch.queryHandler.collect {
                if (!it.isNullOrEmpty()) {
                    if (BackPressedSingleton.isBackPressClick == null) {
                        lifecycleScope.launch {
                            viewModelSearch.recipes(it)?.collectLatest { pagingData ->
                                binding.ivEmptyList.visibility = View.GONE
                                pagingAdapter?.submitData(pagingData)
                            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }
}