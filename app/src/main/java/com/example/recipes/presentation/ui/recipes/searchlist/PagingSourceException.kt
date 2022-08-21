package com.example.recipes.presentation.ui.recipes.searchlist

sealed class PagingSourceException : Exception() {
    class EmptyListException : PagingSourceException()
    class EndOfListException : PagingSourceException()
    class NetWorkOffException : PagingSourceException()
    class Response429Exception : PagingSourceException()
}
