package com.example.recipes.business.usecases

import android.util.Log
import com.example.recipes.business.ResponseStatus
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.entities.RecipeEntity
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(
    private val apiService: RecipesApiService
) {

//    suspend fun getRecipe(id: String): ResponseStatus<RecipeEntity> {
//        Log.d("TAG", "Recipe ID in getQuery: $id")
//        val response = apiService.getRecipeInfo(id)
//        Log.d("TAG", "getRecipe111: $response")
//        val messageResponse = response.message()
//        return try {
//            if (response.code() == 200) {
//                ResponseStatus.success(response.body()?.recipeEntity)
//            } else {
//                ResponseStatus.error(data = null, message = response.code().toString())
//            }
//        } catch (e: Throwable) {
//            ResponseStatus.error(data = null, message = messageResponse)
//        }
//    }
}