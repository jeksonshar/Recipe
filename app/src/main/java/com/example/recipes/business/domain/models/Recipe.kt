package com.example.recipes.business.domain.models

data class Recipe(
    val calories: Double,
    val cautions: List<String>,
    val cuisineType: List<String>,
    val dietLabels: List<String>,
//    val digest: List<DigestEntity>,
    val dishType: List<String>,
    val healthLabels: List<String>,
    val image: String,
    val images: Images,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val label: String,
    val mealType: List<String>,
    val shareAs: String,
    val source: String,
//    val totalDaily: TotalDailyEntity,
//    val totalNutrients: TotalNutrientsEntity,
    val totalTime: Double,
    val totalWeight: Double,
    val uri: String,
    val url: String,
    val yield: Double,
    val isFavorite: Boolean = false,
    val userIdList: List<String> = emptyList()
)
