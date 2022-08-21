package com.example.recipes.presentation.ui.recipes.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.business.domain.models.Ingredient
import com.example.recipes.databinding.FragmentDetailRecipeItemBinding

class DetailAdapter : ListAdapter<Ingredient, DetailViewHolder>(IngredientsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}

class DetailViewHolder private constructor(
    private val binding: FragmentDetailRecipeItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(ingredient: Ingredient) {
        // TODO через binding adapter
        binding.tvDetailItem.text = ingredient.text
    }

    companion object {
        fun from(parent: ViewGroup): DetailViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FragmentDetailRecipeItemBinding.inflate(inflater, parent, false)
            return DetailViewHolder(binding)
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