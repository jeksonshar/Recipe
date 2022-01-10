package com.example.recipes.presentation.ui.recipes.searchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.databinding.FragmentRecipeListBinding

class RecipesLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<ProgressViewHolder>() {

    override fun onBindViewHolder(holder: ProgressViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ProgressViewHolder {
        return ProgressViewHolder(/*binding ,*/parent, retry)
    }
}

class ProgressViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_list, parent, false)) {

    private val binding = FragmentRecipeListBinding.bind(itemView)

    private val errorMsg = binding.tvErrorLoading
    private val retry = binding.buttonRetry
        .also {
            it.setOnClickListener { retry() }
        }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage
        }

        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
    }
}
