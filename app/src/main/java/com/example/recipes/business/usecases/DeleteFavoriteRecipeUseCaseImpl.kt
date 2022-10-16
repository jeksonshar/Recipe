package com.example.recipes.business.usecases

import androidx.collection.arraySetOf
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.interfaces.DeleteFavoriteRecipeUseCase
import com.example.recipes.business.utils.ChangeRecipeUserIdListUtil.changeRecipeUserIdList
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.google.firebase.database.DatabaseReference
import com.google.gson.GsonBuilder
import javax.inject.Inject

class DeleteFavoriteRecipeUseCaseImpl @Inject constructor(
    private val dataBase: RecipeDataBase?,
) : DeleteFavoriteRecipeUseCase {

    override suspend fun deleteFavoriteRecipe(
        recipe: Recipe,
        userId: String,
        favoriteFirebaseRecipesRef: DatabaseReference
    ) {
        val userIdList: MutableSet<String> = arraySetOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.remove(userId)
        val changedRecipe = changeRecipeUserIdList(recipe, userIdList)
        // clear firebase database
        favoriteFirebaseRecipesRef.child(changedRecipe.label).removeValue()
        if (changedRecipe.userIdList.isNotEmpty()) {
            // set changed recipe list to firebase database
            val gson = GsonBuilder()/*.disableHtmlEscaping()*/.create()
            val newRecipeString = gson.toJson(changedRecipe)
            favoriteFirebaseRecipesRef.child(changedRecipe.label).setValue(newRecipeString)
        }
        // delete recipe from local database
        dataBase?.recipesDao()?.deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntityLocal(recipe))

    }
}