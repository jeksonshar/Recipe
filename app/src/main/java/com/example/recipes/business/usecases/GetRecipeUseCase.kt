package com.example.recipes.business.usecases

import com.example.recipes.business.ResponseStatus
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.entities.HitEntity
import com.example.recipes.datasouce.network.entities.RecipeEntity
import retrofit2.Response
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(
    private val apiService: RecipesApiService
) {

    suspend fun getRecipe(id: String): ResponseStatus<RecipeEntity> {
        val response: Response<HitEntity>
        return try {
            response = apiService.getRecipeInfo(id)
            if (response.code() == 200) {
                ResponseStatus.success(response.body()?.recipeEntity)
            } else {
                ResponseStatus.error(data = null, message = response.code().toString())
            }
        } catch (e: Throwable) {
            ResponseStatus.error(data = null, message = e.message ?: "")
        }
    }
}