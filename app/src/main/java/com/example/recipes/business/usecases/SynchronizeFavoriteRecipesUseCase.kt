package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.example.recipes.datasouce.network.RecipesApiService
import javax.inject.Inject

class SynchronizeFavoriteRecipesUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?,
    private val apiService: RecipesApiService
) {

    suspend fun synchronizeFavoriteRecipesWithFirebase(
        favorites: HashMap<String, List<String>>,
        userId: String
    ): List<Recipe> {

        // удалеяю любимые рецепты юзера
        val allFavoriteRecipes = dataBase?.recipesDao()?.getAllFavoriteRecipes()
        allFavoriteRecipes?.forEach {
            if (it.userIdList.contains(userId)) {
                dataBase?.recipesDao()?.deleteRecipeFromFavorite(it)
            }
        }

        // скачиваю и сохраняю любимые рецепты юзера, отмеченные в Firebase
        val recipes: MutableList<Recipe> = ArrayList()
        if (favorites.isNotEmpty()) {
            favorites.forEach { (s, list) ->
                if (list.contains(userId)) {
                    val recipeEntity = apiService.getRecipeInfo(s).body()?.recipeEntity
                    val recipe = NetWorkEntitiesMappers.mapToRecipe(recipeEntity)
                    val newRecipe = changeRecipeUserIdList(recipe, list)
                    val recipeEntityLocal = DataBaseEntitiesMappers.mapToRecipeEntity(newRecipe)
                    recipes.add(newRecipe)
                    dataBase?.recipesDao()?.insertFavoriteRecipes(recipeEntityLocal)
                }
            }
        }
        return recipes
    }

    private suspend fun deleteFromRoomRecipesByUser(userId: String) {
        val allFavoriteRecipes = dataBase?.recipesDao()?.getAllFavoriteRecipes()
        allFavoriteRecipes?.forEach {
            if (it.userIdList.contains(userId)) {
                dataBase?.recipesDao()?.deleteRecipeFromFavorite(it)
            }
        }
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