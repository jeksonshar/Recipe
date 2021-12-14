package com.example.recipes.business

data class ResponseStatus<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): ResponseStatus<T> =
            ResponseStatus(Status.SUCCESS, data, null)

        fun <T> error(data: T?, message: String): ResponseStatus<T> =
            ResponseStatus(Status.ERROR, data = data, message)

        fun <T> loading(data: T?): ResponseStatus<T> =
            ResponseStatus(Status.LOADING, data = data, null)
    }
}

