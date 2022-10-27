package com.example.recipes.presentation.ui.recipes.favoritelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListItemBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener

class FavoriteListAdapter(
    private val clickListener: RecipeClickListener,
    private val share: ((recipe: Recipe) -> Unit)? = null
) : ListAdapter<Recipe, FavoriteListViewHolder>(FavoriteRecipesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        return FavoriteListViewHolder.from(parent, clickListener, share)
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        getItem(position).let { recipe ->
            holder.bind(recipe)
        }
    }
}

class FavoriteListViewHolder(
    private val binding: FragmentRecipeListItemBinding,
    private val clickListener: RecipeClickListener,
    private val share: ((recipe: Recipe) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {
    private var localRecipe: Recipe? = null

    init {
        binding.root.setOnClickListener {
            localRecipe?.let { recipe -> clickListener.openRecipeDetailsFragment(recipe) }
        }

        if (share == null) {
            binding.btnItemShare.visibility = View.GONE
        } else {
            binding.btnItemShare.setOnClickListener {
                localRecipe?.let { recipe -> share.invoke(recipe) }
            }
        }
    }

    fun bind(recipe: Recipe) {
        binding.recipe = recipe
        localRecipe = recipe
    }

    companion object {
        fun from(
            parent: ViewGroup,
            clickListener: RecipeClickListener,
            share: ((recipe: Recipe) -> Unit)?
        ): FavoriteListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FragmentRecipeListItemBinding.inflate(
                inflater,
                parent,
                false
            )
            return FavoriteListViewHolder(binding, clickListener, share)
        }
    }
}

class FavoriteRecipesComparator : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.uri == newItem.uri
    }

}