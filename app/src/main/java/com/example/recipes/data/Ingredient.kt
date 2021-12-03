package com.example.recipes.data

data class Ingredient(
    val food: String,
    val foodCategory: String,
    val foodId: String,
    val image: String,
    val measure: String,
    val quantity: Int,
    val text: String,
    val weight: Double
)
