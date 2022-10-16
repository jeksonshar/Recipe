package com.example.recipes.presentation.ui.recipes.searchlist.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.SaveInFileCacheLoadRecipesUseCaseImpl
import com.example.recipes.datasouce.network.NetWorkEntitiesMappers
import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import com.example.recipes.presentation.ui.recipes.searchlist.PagingSourceException
import retrofit2.HttpException
import retrofit2.Response

class RecipesPagingSource(
    private val recipesApiService: RecipesApiService,
    private val saveInFileCacheLoadRecipesUseCase: SaveInFileCacheLoadRecipesUseCaseImpl,
    private val fileName: String,
    private val filtersDiet: List<String>?,
    private val filtersHealth: List<String>?,
    private val filtersCuisineType: List<String>?,
    private val filtersMealType: List<String>?,
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

        Log.d("TAG", "load: ${params.key}")
        val href = params.key
        val response: Response<RecipeSearchEntity>
        try {
            response = recipesApiService.getNextRecipesByQuery(
                fetchQuery.invoke(),
                href,
                filtersDiet ?: emptyList(),
                filtersHealth ?: emptyList(),
                filtersCuisineType ?: emptyList(),
                filtersMealType ?: emptyList(),
            )

        } catch (e: Throwable) {
            Log.d("TAG", "ERROR: response didn't completed + ${e.message}")
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            if (response.body()?.hits?.isEmpty() == true) {
                LoadResult.Error(PagingSourceException.EmptyListException())
            } else {
                val loadingData = NetWorkEntitiesMappers.mapToRecipes(response.body() ?: RecipeSearchEntity())
                if (href == null) {
                    // сохранение первых 20 загруженных рецептов в файл
                    cachingFirstLoadRecipes(loadingData)
                }
                LoadResult.Page(
                    data = loadingData,
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

    private fun cachingFirstLoadRecipes(loadingData: List<Recipe>) {
        saveInFileCacheLoadRecipesUseCase.saveInFileCacheOfLoadRecipes(fileName, loadingData)
    }
}