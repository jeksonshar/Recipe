package com.example.recipes.db.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipes.ConverterModels
import com.example.recipes.data.Recipe
import com.example.recipes.db.network.entities.RecipeSearchEntity
import retrofit2.HttpException

class RecipesPagingSource(
    private val recipesApiService: RecipesApiService,
    private val query: String
) : PagingSource<String, Recipe>() {

    override fun getRefreshKey(state: PagingState<String, Recipe>): String? {
//        val anchorPosition = state.anchorPosition ?: return null
//        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Recipe> {

        fun getHref(recipeSearchEntity: RecipeSearchEntity): String = ConverterModels.getHrefNextRecipes(recipeSearchEntity)
            .substringAfter("_cont=")
            .substringBefore("&")

        suspend fun firstResponse() = recipesApiService.getRecipesByQuery(query)

        val nextHref = params.key ?: getHref(firstResponse())
        val response = recipesApiService.getNextRecipesByQuery(query, nextHref)

        return if (response.isSuccessful) {
            LoadResult.Page(
                data = ConverterModels.convertToRecipes(response.body() ?: RecipeSearchEntity()),
                prevKey = if (nextHref == getHref(firstResponse())) null else nextHref,
                nextKey = getHref(response.body() ?: RecipeSearchEntity())
            )
        } else {
            LoadResult.Error(HttpException(response))
        }
    }
}