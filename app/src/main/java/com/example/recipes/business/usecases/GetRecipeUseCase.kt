package com.example.recipes.business.usecases

import android.util.Log
import com.example.recipes.ConverterModels
import com.example.recipes.business.ResponseStatus
import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.network.RecipesApiService

class GetRecipeUseCase(private val apiService: RecipesApiService) {

    suspend fun getRecipe(id: String): Recipe {
        Log.d("TAG", "Recipe ID in getQuery: $id")
        val res = apiService.getRecipeInfo(id)
        Log.d("TAG", "getRecipe111: $res")

//        return try {
//            ResponseStatus.success(res)
//            ConverterModels.convertToRecipe(res.body()?.recipeEntity)
//        } catch (e: Throwable) {
//            ResponseStatus.error(data = null, message = res.message())
//            return
//        }
        return ConverterModels.convertToRecipe(res.body()?.recipeEntity)
    }
}