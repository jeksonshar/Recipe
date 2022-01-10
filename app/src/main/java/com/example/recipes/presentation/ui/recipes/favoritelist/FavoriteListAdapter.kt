package com.example.recipes.presentation.ui.recipes.favoritelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.presentation.ui.recipes.RecipeFragmentClickListener
import com.example.recipes.presentation.utils.ImagesUtil

class FavoriteListAdapter(
    private val clickListener: RecipeFragmentClickListener
) : ListAdapter<Recipe, FavoriteListViewHolder>(FavoriteRecipesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        return FavoriteListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        getItem(position).let { recipe ->
            holder.onBind(recipe)
            holder.itemView.setOnClickListener {
                clickListener.openRecipeDetailsFragment(recipe)
            }
        }
    }
}

class FavoriteListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val recipeImageView = view.findViewById<AppCompatImageView>(R.id.ivItemRecipeList)
    private val recipeName = view.findViewById<AppCompatTextView>(R.id.tvNameRecipe)

    fun onBind(recipe: Recipe) {

        ImagesUtil.setImage(recipe.image, recipeImageView)
        recipeName.text = recipe.label
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