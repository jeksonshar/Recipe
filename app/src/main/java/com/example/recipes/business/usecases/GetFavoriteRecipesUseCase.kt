package com.example.recipes.business.usecases

import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.room.ConvertersRoom
import com.example.recipes.datasouce.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.room.RecipeDataBase

class GetFavoriteRecipesUseCase(private val dataBase: RecipeDataBase) {

    suspend fun getRecipesFromRoom(): List<Recipe> {
        val entities = dataBase.recipesDao().getAllRecipes()
        val recipes: MutableList<Recipe> = ArrayList()
        for (entity in entities) {
            recipes.add(DataBaseEntitiesMappers.mapToRecipe(entity))
        }
        return recipes
    }

}