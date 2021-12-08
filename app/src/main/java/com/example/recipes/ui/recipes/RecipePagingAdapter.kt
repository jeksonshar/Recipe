package com.example.recipes.ui.recipes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import by.kirich1409.viewbindingdelegate.viewBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipes.R
import com.example.recipes.data.Recipe
import com.example.recipes.databinding.FragmentRecipeListItemBinding

class RecipePagingAdapter : PagingDataAdapter<Recipe, RecipePagingViewHolder>(RecipePagingComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipePagingViewHolder {
        return RecipePagingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_recipe_list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecipePagingViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder:")
        getItem(position)?.let {
            Log.d("TAG", "onBindViewHolder: $it")
            holder.onBind(it)
        }
    }
}

class RecipePagingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val viewBinding by viewBinding(FragmentRecipeListItemBinding::bind)

    fun onBind(recipe: Recipe) {

        Log.d("TAG", "onBind Recipe: $recipe")
        with(viewBinding) {
            Glide.with(itemView.context)
                .load(recipe.image)
                .apply(RequestOptions())
                .into(ivItemRecipeList)

            tvNameRecipe.text = recipe.label
        }
    }

    companion object {

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