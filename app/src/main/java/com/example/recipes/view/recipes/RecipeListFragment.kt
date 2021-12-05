package com.example.recipes.view.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding
    private var recycler: RecyclerView? = null
    private var adapter: RecipeListAdapter = RecipeListAdapter(emptyList())
    private val viewModel: RecipeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initRequest()
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

        recycler = binding?.recyclerRecipe
        recycler?.layoutManager = GridLayoutManager(view.context, 2)
        recycler?.adapter = adapter

        viewModel.recipesRequest.observe(viewLifecycleOwner, {
            val list = viewModel.entityToData(it)
            Log.d("TAG", "list recipe: $list")
            adapter.submitList(list)

        })

        viewModel.showApdateProgress.observe(viewLifecycleOwner, {
            if (!it) binding?.progressBarWhileListEmpty?.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}