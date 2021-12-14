package com.example.recipes.ui.recipes.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recipes.R
import com.example.recipes.data.Ingredient
import com.example.recipes.data.Recipe
import com.example.recipes.databinding.FragmentDetailRecipeItemBinding
import com.example.recipes.databinding.FragmentRecipeListItemBinding
import com.example.recipes.ui.recipes.list.RecipePagingViewHolder
import com.example.recipes.ui.recipes.list.RecipesComparator

class DetailAdapter : ListAdapter<Ingredient, DetailViewHolder>(IngredientsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_detail_recipe_item, parent, false))
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}

class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val viewBinding by viewBinding(FragmentDetailRecipeItemBinding::bind)

    fun onBind(ingredient: Ingredient) {
        viewBinding.apply {
            tvDetailItem.text = ingredient.text
        }
    }
}

class IngredientsComparator : DiffUtil.ItemCallback<Ingredient>() {
    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem.foodId == newItem.foodId
    }
}