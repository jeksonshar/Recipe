package com.example.recipes.di.modules

import com.example.recipes.business.usecases.CheckConnectionUseCaseImpl
import com.example.recipes.business.usecases.interfaces.CheckConnectionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class Connectivity {

    @Binds
    abstract fun provideConnectivity(checkConnectionUseCaseImpl: CheckConnectionUseCaseImpl): CheckConnectionUseCase
}