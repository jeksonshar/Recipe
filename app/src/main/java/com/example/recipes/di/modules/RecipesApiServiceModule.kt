package com.example.recipes.di.modules

import com.example.recipes.datasouce.network.RecipesApiService
import com.example.recipes.datasouce.network.RetrofitModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipesApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(): RecipesApiService {
        return RetrofitModule.RECIPES_API_SERVICE
    }
}