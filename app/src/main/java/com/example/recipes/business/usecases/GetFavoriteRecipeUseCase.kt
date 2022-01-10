package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.datasouce.network.RecipesApiService

class GetFavoriteRecipeUseCase(private val dataBase: RecipeDataBase) {

    suspend fun getFavoriteRecipe(uri: String): Recipe? {
        val recipeEntity = dataBase.recipesDao().getRecipeByUri(uri)
        return if (recipeEntity != null) {
            DataBaseEntitiesMappers.mapToRecipe(recipeEntity)
        } else {
            null
        }

    }
}