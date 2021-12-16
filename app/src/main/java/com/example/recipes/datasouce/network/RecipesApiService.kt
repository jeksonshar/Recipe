package com.example.recipes.datasouce.network

import com.example.recipes.datasouce.network.models.HitModel
import com.example.recipes.datasouce.network.models.RecipeSearchModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipesApiService {

    @GET("/api/recipes/v2")
    suspend fun getRecipesByQuery(
        @Query("q") query: String
    ): RecipeSearchModel

    @GET("/api/recipes/v2")
    suspend fun getNextRecipesByQuery(
        @Query("q") query: String,
        @Query("_cont", encoded = true) contId: String? = null
    ): Response<RecipeSearchModel>

    @GET("/api/recipes/v2/{id}")
    suspend fun getRecipeInfo(
        @Path("id") id: String
    ): Response<HitModel>
}
