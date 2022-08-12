package com.example.recipes.presentation.ui.recipes.searchlist

import com.example.recipes.datasouce.network.entities.RecipeSearchEntity
import retrofit2.HttpException
import retrofit2.Response

sealed class PagingSourceException : Exception() {
    class EmptyListException : PagingSourceException()
    class NetWorkOffException : PagingSourceException()
    class Response429Exception(response: Response<RecipeSearchEntity>) : HttpException(response)
}
