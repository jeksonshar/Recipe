package com.example.recipes.business.usecases

import android.os.Bundle
import android.util.Log
import androidx.collection.arraySetOf
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javax.inject.Inject

class ManageFavoriteRecipeUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?
) {
    private val firebaseAnalytics = Firebase.analytics
    private val favoriteFirebaseRecipesRef = Firebase.database
        .getReference("favorite")
        .child("favorite_recipes")

    suspend fun manageRecipe(recipe: Recipe, userId: String) {
        if (dataBase != null) {
            val recipeEntity = dataBase.recipesDao().getFavoriteRecipeByUri(recipe.uri)
            if (recipeEntity == null || !(recipeEntity.userIdList.contains(userId))) {
                save(recipe, userId)
            } else {
                delete(recipe, userId)
            }
        }
    }

    private suspend fun save(recipe: Recipe, userId: String) {
        val userIdList: MutableSet<String> = arraySetOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.add(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        dataBase?.recipesDao()?.insertFavoriteRecipes(DataBaseEntitiesMappers.mapToRecipeEntityLocal(newRecipe))

        val gson = GsonBuilder()/*.disableHtmlEscaping()*/.create()
        val newRecipeString = gson.toJson(newRecipe)
        favoriteFirebaseRecipesRef.child(newRecipe.label).setValue(newRecipeString)

        firebaseAnalytics.logEvent("add_to_favorite") {
            param(FirebaseAnalytics.Param.ITEM_ID, recipe.uri)
            param(FirebaseAnalytics.Param.ITEM_NAME, recipe.label)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "addFavorite")
        }
    }

    private suspend fun delete(recipe: Recipe, userId: String) {
        val userIdList: MutableSet<String> = arraySetOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.remove(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        if (newRecipe.userIdList.isEmpty()) {
            dataBase?.recipesDao()?.deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntityLocal(recipe))
            favoriteFirebaseRecipesRef.child(newRecipe.label).removeValue()
        } else {
            val gson = GsonBuilder()/*.disableHtmlEscaping()*/.create()
            val newRecipeString = gson.toJson(newRecipe)
            favoriteFirebaseRecipesRef.child(newRecipe.label).removeValue()
            favoriteFirebaseRecipesRef.child(newRecipe.label).setValue(newRecipeString)
            dataBase?.recipesDao()?.deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntityLocal(recipe))
        }

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.uri)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, recipe.label)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "removeFavorite")
        firebaseAnalytics.logEvent("remove_from_favorite", bundle)
    }

    private fun changeRecipeUserIdList(recipe: Recipe, userIdList: Set<String>): Recipe {
        return Recipe(
            calories = recipe.calories,
            cautions = recipe.cautions,
            cuisineType = recipe.cuisineType,
            dietLabels = recipe.dietLabels,
            dishType = recipe.dishType,
            healthLabels = recipe.healthLabels,
            image = recipe.image,
            images = recipe.images,
            ingredientLines = recipe.ingredientLines,
            ingredients = recipe.ingredients,
            label = recipe.label,
            mealType = recipe.mealType,
            shareAs = recipe.shareAs,
            source = recipe.source,
            totalTime = recipe.totalTime,
            totalWeight = recipe.totalWeight,
            uri = recipe.uri,
            url = recipe.url,
            yield = recipe.yield,
            isFavorite = recipe.isFavorite,
            userIdList = userIdList.toList()
        )
    }
}