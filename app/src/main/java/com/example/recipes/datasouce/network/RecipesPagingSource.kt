package com.example.recipes.datasouce.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import retrofit2.HttpException
import retrofit2.Response

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

        fun getHref(recipeSearchEntity: RecipeSearchEntity): String = NetWorkEntitiesMappers.getHrefNextRecipes(recipeSearchEntity)
            .substringAfter("_cont=")
            .substringBefore("&")

        val href = params.key
        val response: Response<RecipeSearchEntity>
        try {
            response = recipesApiService.getNextRecipesByQuery(query, href)

        } catch (e: Throwable) {
            Log.d("TAG", "ERROR: response didn't completed")
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            LoadResult.Page(
                data = NetWorkEntitiesMappers.mapToRecipes(response.body() ?: RecipeSearchEntity()),
                prevKey = href,
                nextKey = getHref(response.body() ?: RecipeSearchEntity())
            )
        } else {
            Log.d("TAG", "responseNOT: $response")
            LoadResult.Error(HttpException(response))
        }
    }
}