package com.example.recipes.ui.recipes

import androidx.paging.PagingSource
import com.example.recipes.data.Recipe
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.RecipesPagingSource

class RecipesUseCase(private val apiService: RecipesApiService) {

    operator fun invoke(query: String): PagingSource<String, Recipe> {
        return RecipesPagingSource(apiService, query)
    }
}