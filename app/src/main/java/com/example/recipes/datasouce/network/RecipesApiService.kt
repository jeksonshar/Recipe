package com.example.recipes.datasouce.network

import com.example.recipes.datasouce.network.entities.HitEntity
import com.example.recipes.datasouce.network.entities.RecipeEntity
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipesApiService {

    @GET("/api/recipes/v2")
    suspend fun getRecipesByQuery(
        @Query("q") query: String
    ): RecipeSearchEntity

    @GET("/api/recipes/v2")
    suspend fun getNextRecipesByQuery(
        @Query("q") query: String,
        @Query("_cont", encoded = true) contId: String? = null
    ): Response<RecipeSearchEntity>

    @GET("/api/recipes/v2/{id}")
    suspend fun getRecipeInfo(
        @Path("id") id: String
    ): Response<HitEntity>
}
