package com.example.recipes.business.usecases

import androidx.paging.PagingSource
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.RecipesPagingSource

class GetRecipesBySearchUseCase(private val apiService: RecipesApiService) {

    operator fun invoke(query: String): PagingSource<String, Recipe> {
        return RecipesPagingSource(apiService, query)
    }
}