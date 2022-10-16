package com.example.recipes.business.usecases.interfaces

import com.example.recipes.business.domain.models.Recipe
import com.google.firebase.database.DatabaseReference

interface DeleteFavoriteRecipeUseCase {
    suspend fun deleteFavoriteRecipe(
        recipe: Recipe,
        userId: String,
        favoriteFirebaseRecipesRef: DatabaseReference
    )
}