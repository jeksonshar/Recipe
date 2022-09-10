package com.example.recipes.datasouce.local.room

import androidx.room.*
import androidx.room.OnConflictStrategy.*
import com.example.recipes.datasouce.local.room.entities.RecipeEntityLocal

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    suspend fun getAllFavoriteRecipes(): List<RecipeEntityLocal>

    @Query("SELECT * From recipes WHERE uri == :uri")
    suspend fun getFavoriteRecipeByUri(uri: String): RecipeEntityLocal?

    @Insert(onConflict = REPLACE)
    suspend fun insertFavoriteRecipes(vararg recipe: RecipeEntityLocal)

    @Delete
    suspend fun deleteRecipeFromFavorite(recipe: RecipeEntityLocal)

    @Query("DELETE FROM recipes")
    suspend fun delete()

    @Update
    suspend fun updateFavoriteRecipe(recipe: RecipeEntityLocal)
}