package com.example.recipes.db.network

import com.example.recipes.db.network.entities.RecipeSearchEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipesApi {

    @GET("/api/recipes/v2")
    suspend fun searchRecipesByQuery(
        @Query("q") query: String
    ): RecipeSearchEntity

    @GET("/api/recipes/v2/{id}")
    suspend fun getRecipeInfo(
        @Path("id") id: String
    )
}
