package com.example.recipes.business.usecases.interfaces

import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.utils.StatesManageFavoriteRecipes

interface GetStateManageFavoriteRecipeUseCase {
    suspend fun getStateManageFavoriteRecipes(recipe: Recipe, userId: String): StatesManageFavoriteRecipes?
}