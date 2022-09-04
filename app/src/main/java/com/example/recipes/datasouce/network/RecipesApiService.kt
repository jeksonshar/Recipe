package com.example.recipes.datasouce.network

import com.example.recipes.datasouce.network.entities.HitEntity
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import retrofit2.Response
import retrofit2.http.*

interface RecipesApiService {

    @GET("/api/recipes/v2")
    suspend fun getNextRecipesByQuery(
        @Query("q") query: String,
        @Query("_cont", encoded = true) contId: String? = null,
//        @QueryMap params: HashMap<String, String>,
        @Query("diet") filtersDiet: List<String>,
        @Query("health") filterHealth: List<String>,
        @Query("cuisineType") filterCuisineType: List<String>,
        @Query("mealType") filterMealType: List<String>,
    ): Response<RecipeSearchEntity>

    @GET("/api/recipes/v2/{id}")
    suspend fun getRecipeInfo(
        @Path("id") id: String
    ): Response<HitEntity>
}
