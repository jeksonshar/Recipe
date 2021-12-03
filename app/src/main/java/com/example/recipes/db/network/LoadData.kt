package com.example.recipes.db.network

import com.example.recipes.data.Recipe
import com.example.recipes.db.network.entities.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun loadRecipesByQuery(query: String): List<Recipe> = withContext(Dispatchers.IO){

    val recipeEntity: ArrayList<RecipeEntity> = ArrayList()
    RetrofitModule.recipesApi.searchRecipesByQuery(query).hits?.map {
        recipeEntity.add(it.recipeEntity ?: RecipeEntity())
    }

    return@withContext recipeEntity.map {
        Recipe(
            calories = it.calories ?: 0.toDouble(),
            cautions = it.cautions ?: emptyList(),
            cuisineType = it.cuisineType ?: emptyList(),
            dietLabels = it.dietLabels ?: emptyList(),
            dishType = it.dishType ?: emptyList(),
            healthLabels = it.healthLabels ?: emptyList(),
            image = it.image ?: "",
            images = ConverterModels.convertToImages(it.images!!),
            ingredientLines = it.ingredientLines ?: emptyList(),
            ingredients = it.ingredients?.map { ingridientEntity ->
                ConverterModels.convertToIngredients(ingridientEntity)
            } ?: emptyList(),
            label = it.label ?: "",
            mealType = it.mealType ?: emptyList(),
            shareAs = it.shareAs ?: "",
            source = it.source ?: "",
            totalTime = it.totalTime ?: 0,
            totalWeight = it.totalWeight ?: 0.toDouble(),
            uri = it.uri ?: "",
            url = it.url ?: "",
            yield = it.yield ?: 0
        )
    }
}