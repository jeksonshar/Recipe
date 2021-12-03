package com.example.recipes.view.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipes.R
import com.example.recipes.data.Recipe

class RecipeListAdapter(
    private val recipes: List<Recipe>
) : ListAdapter<Recipe, RecipeListViewHolder>(RecipesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListViewHolder {
        return RecipeListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecipeListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
    override fun getItemId(position: Int): Long {
        val uriRecipe = recipes[position].uri
        return uriRecipe.substringAfter('_', uriRecipe).hashCode().toLong()
    }

}

class RecipeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val recipeImageView = view.findViewById<AppCompatImageView>(R.id.ivItemRecipeList)
    private val recipeName = view.findViewById<AppCompatTextView>(R.id.tvNameRecipe)

    fun onBind(recipe: Recipe) {

        Glide.with(itemView.context)
            .load(recipe.image)
            .apply(RequestOptions())
            .into(recipeImageView)

        recipeName.text = recipe.label
    }
}

class RecipesComparator : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.uri == newItem.uri
    }
}