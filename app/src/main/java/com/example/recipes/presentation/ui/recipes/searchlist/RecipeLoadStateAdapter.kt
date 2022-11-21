package com.example.recipes.presentation.ui.recipes.searchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.recipes.databinding.RecipeLoadStateBinding

class RecipeLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<RecipeLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: RecipeLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
        val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RecipeLoadStateViewHolder {
        parent.context
        return RecipeLoadStateViewHolder.from(parent, retry)
    }
}

class RecipeLoadStateViewHolder(
    private val binding: RecipeLoadStateBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.buttonRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        binding.loadState = loadState
    }

    companion object {
        fun from(parent: ViewGroup, retry: () -> Unit): RecipeLoadStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RecipeLoadStateBinding.inflate(
                inflater,
                parent,
                false
            )
            return RecipeLoadStateViewHolder(binding, retry)
        }
    }
}