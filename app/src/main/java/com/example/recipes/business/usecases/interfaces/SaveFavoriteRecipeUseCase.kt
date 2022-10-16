package com.example.recipes.business.usecases.interfaces

import com.example.recipes.business.domain.models.Recipe
import com.google.firebase.database.DatabaseReference

interface SaveFavoriteRecipeUseCase {
    suspend fun saveFavoriteRecipe(
        recipe: Recipe,
        userId: String,
        favoriteFirebaseRecipesRef: DatabaseReference
    )
}

