package com.example.recipes.business.usecases

import android.os.Bundle
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.local.room.DataBaseEntitiesMappers
import com.example.recipes.datasouce.local.room.RecipeDataBase
import com.example.recipes.datasouce.local.room.entities.RecipeEntityLocal
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class ManageFavoriteRecipeUseCase @Inject constructor(
    private val dataBase: RecipeDataBase?
) {
    private val firebaseAnalytics = Firebase.analytics
    private val firebaseDatabase = Firebase.database

    suspend fun manageRecipe(recipe: Recipe, userId: String) {
        if (dataBase != null) {
            val recipeEntity = dataBase.recipesDao().getFavoriteRecipeByUri(recipe.uri)
            if (recipeEntity == null || !recipeEntity.userIdList.contains(userId)) {
                save(recipe, userId)
            } else {
                delete(recipe, userId)
            }
        }
    }

    private suspend fun save(recipe: Recipe, userId: String) {
        val userIdList: MutableList<String> = arrayListOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.add(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        dataBase!!.recipesDao().insertFavoriteRecipes(DataBaseEntitiesMappers.mapToRecipeEntity(newRecipe))
        firebaseAnalytics.logEvent("add_to_favorite") {
            param(FirebaseAnalytics.Param.ITEM_ID, recipe.uri)
            param(FirebaseAnalytics.Param.ITEM_NAME, recipe.label)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "addFavorite")
        }

        val favoriteRecipesListEntity = dataBase.recipesDao().getAllFavoriteRecipes()
        changeFirebaseDatabase(favoriteRecipesListEntity)
    }

    private suspend fun delete(recipe: Recipe, userId: String) {
        val userIdList: MutableList<String> = arrayListOf()
        dataBase?.recipesDao()?.getFavoriteRecipeByUri(recipe.uri)?.userIdList?.forEach {
            userIdList.add(it)
        }
        userIdList.remove(userId)
        val newRecipe = changeRecipeUserIdList(recipe, userIdList)
        if (newRecipe.userIdList.isEmpty()) {
            dataBase!!.recipesDao().deleteRecipeFromFavorite(DataBaseEntitiesMappers.mapToRecipeEntity(recipe))
        } else {
            dataBase!!.recipesDao().updateFavoriteRecipe(DataBaseEntitiesMappers.mapToRecipeEntity(newRecipe))
        }
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.uri)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, recipe.label)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "removeFavorite")
        firebaseAnalytics.logEvent("remove_from_favorite", bundle)

        val favoriteRecipesListEntity = dataBase.recipesDao().getAllFavoriteRecipes()
        changeFirebaseDatabase(favoriteRecipesListEntity)
    }

    private fun changeFirebaseDatabase(favoriteRecipesListEntity: List<RecipeEntityLocal>) {
        val ref = firebaseDatabase.getReference("favorite")
        val favoriteFirebaseRecipesRef = ref.child("favorite_recipes")
        val favorites = HashMap<String, List<String>>()
        for (i in favoriteRecipesListEntity) {
            if (i.uri.isNotEmpty()) {
                val recipeId = i.uri.substringAfterLast("_")
                favorites[recipeId] = i.userIdList
            }
        }
        favoriteFirebaseRecipesRef.setValue(favorites)
    }

    private fun changeRecipeUserIdList(recipe: Recipe, userIdList: List<String>): Recipe {
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
            userIdList = userIdList
        )
    }
}