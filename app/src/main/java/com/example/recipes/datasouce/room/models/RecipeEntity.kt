package com.example.recipes.datasouce.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipes.data.Ingredient

@Entity(tableName = "recipes")
data class RecipeEntity(
    val calories: Double = 0.0,
    val cautions: String = "",
    val cuisineType: String = "",
    val dietLabels: String = "",
    val dishType: String = "",
    val healthLabels: String = "",
    val image: String = "",
    val images: ImagesEntity,
    val ingredientLines: String = "",
    val ingredients: List<IngredientEntity> = emptyList(),
    val label: String = "",
    val mealType: String = "",
    val shareAs: String = "",
    val source: String = "",
    val totalTime: Double = 0.0,
    val totalWeight: Double = 0.0,
    @PrimaryKey
    val uri: String = "",
    val url: String = "",
    val yield: Double = 0.0,
    val isFavorite: Boolean = false
)
