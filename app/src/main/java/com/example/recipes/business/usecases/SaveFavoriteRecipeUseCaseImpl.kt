package com.example.recipes.business.usecases

import android.util.Log
import androidx.collection.arraySetOf
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.interfaces.SaveFavoriteRecipeUseCase
import com.example.recipes.business.utils.ChangeRecipeUserIdListUtil.changeRecipeUserIdList
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.entities.HitEntity
import com.google.firebase.database.DatabaseReference
import com.google.gson.GsonBuilder
import retrofit2.Response
import javax.inject.Inject

class SaveFavoriteRecipeUseCaseImpl @Inject constructor(
    private val dataBase: RecipeDataBase?,
    private val apiService: RecipesApiService
) : SaveFavoriteRecipeUseCase{

    override suspend fun saveFavoriteRecipe(
        recipe: Recipe,
        userId: String,
        favoriteFirebaseRecipesRef: DatabaseReference
    ) {
        val userIdList: MutableSet<String> = arraySetOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.add(userId)

        val recipeToSave: Response<HitEntity>
        var newRecipe: Recipe
        try {
            recipeToSave = apiService.getRecipeInfo(recipe.uri.substringAfter("recipe_"))
            newRecipe = if (recipeToSave.isSuccessful) {
                NetWorkEntitiesMappers.mapToRecipe(recipeToSave.body()?.recipeEntity)
            } else recipe
        } catch (e: Throwable) {
            Log.d("TAG", "saveFavoriteRecipe: ${e.message}")
            newRecipe = recipe
        }

        val newRecipeWithUserId = changeRecipeUserIdList(newRecipe, userIdList)
        dataBase?.recipesDao()?.insertFavoriteRecipes(
            DataBaseEntitiesMappers.mapToRecipeEntityLocal(newRecipeWithUserId)
        )

        // set changed recipe list to firebase database
        val gson = GsonBuilder()/*.disableHtmlEscaping()*/.create()
        val newRecipeString = gson.toJson(newRecipeWithUserId)
        favoriteFirebaseRecipesRef.child(newRecipeWithUserId.label).setValue(newRecipeString)

    }
}