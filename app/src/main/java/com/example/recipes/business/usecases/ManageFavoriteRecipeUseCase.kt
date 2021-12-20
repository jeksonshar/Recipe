package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase

class ManageFavoriteRecipeUseCase(private val dataBase: RecipeDataBase) {

    suspend fun manageRecipe(recipe: Recipe) {
        val recipeEntity = dataBase.recipesDao().getRecipeByUri(recipe.uri)
        if (recipeEntity == null) {
            save(recipe)
        } else {
            delete(recipe)
        }
    }

    private suspend fun save(recipe: Recipe) {
        dataBase.recipesDao().insertRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
    }

    private suspend fun delete(recipe: Recipe) {
        dataBase.recipesDao().deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
    }
}