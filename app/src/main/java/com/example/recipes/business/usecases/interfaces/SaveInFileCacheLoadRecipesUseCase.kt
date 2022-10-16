package com.example.recipes.business.usecases.interfaces

import com.example.recipes.business.domain.models.Recipe

interface SaveInFileCacheLoadRecipesUseCase {
    fun saveInFileCacheOfLoadRecipes(fileName: String, data: List<Recipe>)
}