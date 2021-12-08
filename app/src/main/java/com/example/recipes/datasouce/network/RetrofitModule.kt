package com.example.recipes.datasouce.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitModule {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.edamam.com")
        .client(client)
//        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val RECIPES_API_SERVICE: RecipesApiService = retrofit.create()

}