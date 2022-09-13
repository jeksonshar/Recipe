package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import javax.inject.Inject

class SynchronizeFavoriteRecipesUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?,
) {

    suspend fun synchronizeFavoriteRecipesWithFirebase(
        favorites: List<Recipe>,
        userId: String
    ): List<Recipe> {

        // удаляю в Room любимые рецепты юзера
        val allFavoriteRecipes = dataBase?.recipesDao()?.getAllFavoriteRecipes()
        allFavoriteRecipes?.forEach {
            if (it.userIdList.contains(userId)) {
                dataBase?.recipesDao()?.deleteRecipeFromFavorite(it)
            }
        }

        // фильтрую рецепты по пользовыателю
        val recipes: MutableList<Recipe> = ArrayList()
        if (favorites.isNotEmpty()) {
            favorites.forEach { recipe ->
                if (recipe.userIdList.contains(userId)) {
                    val listUserId: MutableList<String> = recipe.userIdList as MutableList<String>
                    listUserId.add(userId)
                    val newRecipe = changeRecipeUserIdList(recipe, listUserId)
                    dataBase?.recipesDao()?.insertFavoriteRecipes(
                        DataBaseEntitiesMappers.mapToRecipeEntityLocal(newRecipe)
                    )
                    recipes.add(newRecipe)
                }
            }
        }
        return recipes
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