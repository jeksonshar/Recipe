package com.example.recipes.di.modules

import android.content.Context
import com.example.recipes.datasouce.local.room.RecipeDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipeDataBaseModule {

    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ): RecipeDataBase {
        return RecipeDataBase.getDataBase(context)
    }
}