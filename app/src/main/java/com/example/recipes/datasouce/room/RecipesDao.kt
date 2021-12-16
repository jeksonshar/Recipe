package com.example.recipes.datasouce.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.*
import androidx.room.Query
import com.example.recipes.data.Ingredient
import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.room.models.RecipeEntity

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("SELECT * From recipes WHERE uri == :uri")
    suspend fun getRecipeByUri(uri: String): RecipeEntity

    @Query("SELECT * FROM ingredient")
    suspend fun getIngredientsRecipe(): List<Ingredient>

    @Insert(onConflict = REPLACE)
    suspend fun insertRecipes(vararg recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipeFromFavorite(recipe: RecipeEntity)
}