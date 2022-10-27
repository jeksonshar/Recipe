package com.example.recipes.presentation.ui.recipes.searchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListItemBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener

class RecipePagingAdapter(
    private val clickListener: RecipeClickListener,
    private val share: ((recipe: Recipe) -> Unit)?
) : PagingDataAdapter<Recipe, RecipePagingViewHolder>(RecipePagingComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipePagingViewHolder {
        return RecipePagingViewHolder.from(parent, clickListener, share)
    }

    override fun onBindViewHolder(holder: RecipePagingViewHolder, position: Int) {
        getItem(position)?.let { recipe ->
            holder.bind(recipe)
        }
    }
}

class RecipePagingViewHolder private constructor(
    private val binding: FragmentRecipeListItemBinding,
    private val listener: RecipeClickListener,
    private val share: ((recipe: Recipe) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {
    private var localItem: Recipe? = null

    init {
        binding.root.setOnClickListener {
            localItem?.let { recipe -> listener.openRecipeDetailsFragment(recipe) }
        }
        binding.btnItemShare.setOnClickListener {
            localItem?.let { recipe -> share?.invoke(recipe) }
        }
    }

    fun bind(recipe: Recipe?) {
        localItem = recipe
        binding.recipe = recipe
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: RecipeClickListener,
            share: ((recipe: Recipe) -> Unit)?
        ): RecipePagingViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FragmentRecipeListItemBinding.inflate(
                inflater,
                parent,
                false
            )
            return RecipePagingViewHolder(binding, listener, share)
        }
    }
}

private object RecipePagingComparator : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.uri == newItem.uri
    }
}