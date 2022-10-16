package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.interfaces.GetStateManageFavoriteRecipeUseCase
import com.example.recipes.business.utils.StatesManageFavoriteRecipes
import com.example.recipes.datasouce.local.room.RecipeDataBase
import javax.inject.Inject

class GetStateManageFavoriteRecipeUseCaseImpl @Inject constructor(
    private val dataBase: RecipeDataBase?,
) : GetStateManageFavoriteRecipeUseCase{

    override suspend fun getStateManageFavoriteRecipes(recipe: Recipe, userId: String): StatesManageFavoriteRecipes? {
        if (dataBase != null) {
            val recipeEntity = dataBase.recipesDao().getFavoriteRecipeByUri(recipe.uri)
            return if (recipeEntity == null || !(recipeEntity.userIdList.contains(userId))) {
                StatesManageFavoriteRecipes.SAVE
            } else {
                StatesManageFavoriteRecipes.DELETE
            }
        }
        return null
    }

}