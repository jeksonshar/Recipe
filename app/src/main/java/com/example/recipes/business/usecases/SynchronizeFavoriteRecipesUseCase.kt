package com.example.recipes.business.usecases

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.utils.ChangeRecipeUserIdListUtil
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
                    val newRecipe = ChangeRecipeUserIdListUtil.changeRecipeUserIdList(recipe, listUserId)
                    dataBase?.recipesDao()?.insertFavoriteRecipes(
                        DataBaseEntitiesMappers.mapToRecipeEntityLocal(newRecipe)
                    )
                    recipes.add(newRecipe)
                }
            }
        }
        return recipes
    }
}