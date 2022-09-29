package com.example.recipes.di.modules

import com.example.recipes.datasouce.network.ApiKeyInterceptor
import com.example.recipes.datasouce.network.RecipesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipesApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(): RecipesApiService {

        val client = OkHttpClient().newBuilder()
            .addInterceptor(ApiKeyInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.edamam.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create()
    }
}