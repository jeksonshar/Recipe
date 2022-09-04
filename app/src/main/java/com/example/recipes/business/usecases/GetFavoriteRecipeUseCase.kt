package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import javax.inject.Inject

class GetFavoriteRecipeUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?
) {

    suspend fun getFavoriteRecipe(uri: String, userId: String): Recipe? {
        if (dataBase == null) {
            return null
        }

        val recipeEntity = dataBase.recipesDao().getFavoriteRecipeByUri(uri)
        return if (recipeEntity != null && recipeEntity.userIdList.contains(userId)) {
            DataBaseEntitiesMappers.mapToRecipe(recipeEntity)
        } else {
            null
        }

    }
}