package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase

class GetFavoriteRecipesUseCase(private val dataBase: RecipeDataBase?) {

    suspend fun getRecipesFromRoom(): List<Recipe> {
        if (dataBase == null) {
            return emptyList()
        }
        val entities = dataBase.recipesDao().getAllRecipes()
        val recipes: MutableList<Recipe> = ArrayList()
        for (entity in entities) {
            recipes.add(DataBaseEntitiesMappers.mapToRecipe(entity))
        }
        return recipes
    }

}