package com.example.recipes.business.usecases

import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.room.ConvertersRoom
import com.example.recipes.datasouce.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.room.RecipeDataBase

class SaveFavoriteRecipeUseCase(private val dataBase: RecipeDataBase) {

    suspend fun saveRecipeToRoom(recipe: Recipe) {
        dataBase.recipesDao().insertRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
    }

}