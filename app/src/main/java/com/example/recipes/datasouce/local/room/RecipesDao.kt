package com.example.recipes.datasouce.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import com.example.recipes.datasouce.local.room.entities.RecipeEntity

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    suspend fun getAllFavoriteRecipes(): List<RecipeEntity>

    @Query("SELECT * From recipes WHERE uri == :uri")
    suspend fun getFavoriteRecipeByUri(uri: String): RecipeEntity?

    @Insert(onConflict = IGNORE)
    suspend fun insertRecipes(vararg recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipeFromFavorite(recipe: RecipeEntity)
}