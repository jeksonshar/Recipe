package com.example.recipes.datasouce.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient")
data class IngredientEntity(
    val food: String = "",
    val foodCategory: String = "",
    @PrimaryKey
    val foodId: String = "",
    val image: String = "",
    val measure: String = "",
    val quantity: Double = 0.0,
    val text: String = "",
    val weight: Double = 0.0
)
