package com.example.recipes.business.usecases.interfaces

import kotlinx.coroutines.flow.Flow

interface CheckConnectionUseCase {
    fun isConnected(): Flow<Boolean>
}