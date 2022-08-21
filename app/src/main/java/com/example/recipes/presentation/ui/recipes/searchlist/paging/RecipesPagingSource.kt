package com.example.recipes.presentation.ui.recipes.searchlist.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import com.example.recipes.presentation.ui.recipes.searchlist.PagingSourceException
import retrofit2.HttpException
import retrofit2.Response

class RecipesPagingSource(
    private val recipesApiService: RecipesApiService,
    private val fetchQuery: () -> String
) : PagingSource<String, Recipe>() {

    override fun getRefreshKey(state: PagingState<String, Recipe>): String? {
//        val anchorPosition = state.anchorPosition ?: return null
//        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Recipe> {

        if (fetchQuery.invoke().isEmpty()) {
            return LoadResult.Error(PagingSourceException.EmptyListException())
        }

        fun getHref(recipeSearchEntity: RecipeSearchEntity): String = NetWorkEntitiesMappers.getHrefNextRecipes(
            recipeSearchEntity
        )
            .substringAfter("_cont=")
            .substringBefore("&")

        val href = params.key
        val response: Response<RecipeSearchEntity>
        try {
            response = recipesApiService.getNextRecipesByQuery(fetchQuery.invoke(), href)

        } catch (e: Throwable) {
            Log.d("TAG", "ERROR: response didn't completed")
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            if (response.body()?.hits?.isEmpty() == true) {
                LoadResult.Error(PagingSourceException.EmptyListException())
            } else {
                LoadResult.Page(
                    data = NetWorkEntitiesMappers.mapToRecipes(response.body() ?: RecipeSearchEntity()),
                    prevKey = href,
                    nextKey = getHref(response.body() ?: RecipeSearchEntity())
                )
            }
        } else {
            when {
                response.raw().code == 429 -> {
                    LoadResult.Error(PagingSourceException.Response429Exception())
                }
                getHref(response.body() ?: RecipeSearchEntity()).isEmpty() -> {
                    LoadResult.Error(PagingSourceException.EndOfListException())
                }
                else -> {
                    Log.d("TAG", "responseNOT: ${response.raw().code}")
                    Log.d("TAG", "responseHREF: ${getHref(response.body() ?: RecipeSearchEntity())}")
                    LoadResult.Error(HttpException(response))
                }
            }
        }
    }
}