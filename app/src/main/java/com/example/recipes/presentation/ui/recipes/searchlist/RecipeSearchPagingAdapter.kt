package com.example.recipes.presentation.ui.recipes.searchlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import by.kirich1409.viewbindingdelegate.viewBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.databinding.FragmentRecipeListItemBinding
import com.example.recipes.presentation.ui.recipes.RecipeFragmentClickListener
import com.example.recipes.presentation.utils.ImagesUtil

class RecipePagingAdapter(
    private val clickListener: RecipeFragmentClickListener
) : PagingDataAdapter<Recipe, RecipePagingViewHolder>(RecipePagingComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipePagingViewHolder {
        return RecipePagingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecipePagingViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder:")
        getItem(position)?.let { recipe ->
            Log.d("TAG", "onBindViewHolder: $recipe")
            holder.onBind(recipe)
            holder.itemView.setOnClickListener {
                clickListener.openRecipeDetailsFragment(recipe)
            }
        }
    }
}

class RecipePagingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val viewBinding by viewBinding(FragmentRecipeListItemBinding::bind)

    fun onBind(recipe: Recipe) {

        Log.d("TAG", "onBind Recipe: $recipe")
        with(viewBinding) {

            ImagesUtil.setImage(recipe.image, ivItemRecipeList)

            tvNameRecipe.text = recipe.label
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