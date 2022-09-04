package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import javax.inject.Inject

class ManageFavoriteRecipeUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?
) {

    suspend fun manageRecipe(recipe: Recipe, userId: String) {
        if (dataBase != null) {
            val recipeEntity = dataBase.recipesDao().getFavoriteRecipeByUri(recipe.uri)
            if (recipeEntity == null || !recipeEntity.userIdList.contains(userId)) {
                save(recipe, userId)
            } else {
                delete(recipe, userId)
            }
        }
    }

    private suspend fun save(recipe: Recipe, userId: String) {
        val userIdList: MutableList<String> = arrayListOf()
        recipe.userIdList.forEach {
            userIdList.add(it)
        }
        userIdList.add(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        dataBase!!.recipesDao().insertRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(newRecipe))
    }

    private suspend fun delete(recipe: Recipe, userId: String) {
        val userIdList: MutableList<String> = arrayListOf()
        recipe.userIdList.forEach {
            userIdList.add(it)
        }
        userIdList.remove(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        dataBase!!.recipesDao().deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntity(newRecipe))
    }

    private fun changeRecipeUserIdList(recipe: Recipe, userIdList: List<String>): Recipe {
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
            userIdList = userIdList
        )
    }
}