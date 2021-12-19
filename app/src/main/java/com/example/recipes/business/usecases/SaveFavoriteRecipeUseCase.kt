package com.example.recipes.business.usecases

import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.room.RecipeDataBase

class SaveFavoriteRecipeUseCase(private val dataBase: RecipeDataBase) {

    suspend fun saveRecipeToRoom(recipe: Recipe) {
        val recipeEntities = dataBase.recipesDao().getAllRecipes()
        if (recipeEntities.isEmpty()) {
            dataBase.recipesDao().insertRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
        } else {
            for (entity in recipeEntities) {
                if(entity.uri != recipe.uri) {
                    dataBase.recipesDao().insertRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
                } else {
                    dataBase.recipesDao().deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
                }
            }
        }
    }
}