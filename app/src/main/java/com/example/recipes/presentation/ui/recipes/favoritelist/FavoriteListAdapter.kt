package com.example.recipes.presentation.ui.recipes.favoritelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListItemBinding
import com.example.recipes.presentation.ui.recipes.RecipeClickListener
import com.example.recipes.presentation.utils.ImagesUtil

class FavoriteListAdapter(
    private val clickListener: RecipeClickListener
) : ListAdapter<Recipe, FavoriteListViewHolder>(FavoriteRecipesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        return FavoriteListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        getItem(position).let { recipe ->
            holder.bind(recipe)
            holder.itemView.setOnClickListener {
                clickListener.openRecipeDetailsFragment(recipe)
            }
        }
    }
}

class FavoriteListViewHolder(
    private val binding: FragmentRecipeListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(recipe: Recipe) {
        binding.apply {
            ImagesUtil.setImage(recipe.image, ivItemRecipeList)
            tvNameRecipe.text = recipe.label
        }
    }

    companion object {
        fun from(parent: ViewGroup): FavoriteListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FragmentRecipeListItemBinding.inflate(
                inflater,
                parent,
                false
            )
            return FavoriteListViewHolder(binding)
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