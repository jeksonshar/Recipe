package com.example.recipes.datasouce.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    val calories: Double = 0.0,
    val cautions: List<String>,
    val cuisineType: List<String>,
    val dietLabels: List<String>,
    val dishType: List<String>,
    val healthLabels: List<String>,
    val image: String = "",
    @Embedded val images: ImagesEntity,
    val ingredientLines: List<String>,
    val ingredients: List<IngredientEntity> = emptyList(),
    val label: String = "",
    val mealType: List<String>,
    val shareAs: String = "",
    val source: String = "",
    val totalTime: Double = 0.0,
    val totalWeight: Double = 0.0,
    @PrimaryKey
    val uri: String = "",
    val url: String = "",
    val yield: Double = 0.0,
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "userId")
    val userIdList: List<String> = emptyList()
)
