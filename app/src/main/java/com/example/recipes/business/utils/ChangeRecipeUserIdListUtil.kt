package com.example.recipes.business.utils

import com.example.recipes.business.domain.models.Recipe

object ChangeRecipeUserIdListUtil {

        fun changeRecipeUserIdList(recipe: Recipe, userIdList: Set<String>): Recipe {
        return Recipe(
            calories = recipe.calories,
            cautions = recipe.cautions,
            cuisineType = recipe.cuisineType,
            dietLabels = recipe.dietLabels,
            dishType = recipe.dishType,
            healthLabels = recipe.healthLabels,
            image = recipe.image,
            images = recipe.images,
            ingredientLines = recipe.ingredientLines,
            ingredients = recipe.ingredients,
            label = recipe.label,
            mealType = recipe.mealType,
            shareAs = recipe.shareAs,
            source = recipe.source,
            totalTime = recipe.totalTime,
            totalWeight = recipe.totalWeight,
            uri = recipe.uri,
            url = recipe.url,
            yield = recipe.yield,
            isFavorite = recipe.isFavorite,
            userIdList = userIdList.toList()
        )
    }
}