package com.example.recipes.db.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(TYPE, public)
            .addQueryParameter(API_ID_HEADER, apiID)
            .addQueryParameter(API_KEY_HEADER, apiKey)
            .build()
        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()

        return chain.proceed(request)
    }

    companion object {
        const val API_ID_HEADER = "app_id"
        const val API_KEY_HEADER = "app_key"
        const val TYPE = "type"
    }
}

internal const val apiID = "9bba3bee"
internal const val apiKey = "6a39577cc5b4e51fe02189c818432474"
const val public = "public"
