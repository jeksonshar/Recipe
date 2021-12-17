package com.example.recipes.ui.recipes.searchlist

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

class RecipeSearchListAdapter(
    private val recipes: List<Recipe>
) : ListAdapter<Recipe, RecipeSearchListViewHolder>(RecipesListComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeSearchListViewHolder {
        return RecipeSearchListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_search_list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecipeSearchListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
    override fun getItemId(position: Int): Long {
        val uriRecipe = recipes[position].uri
        return uriRecipe.substringAfter('_', uriRecipe).hashCode().toLong()
    }
/** почему при переопределении этого метода список выводится пустой? что не так переопределяю? */
    override fun getItemCount(): Int {
        return recipes.size
    }
}

class RecipeSearchListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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

class RecipesListComparator : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.uri == newItem.uri
    }
}