package com.example.recipes.business.usecases

import android.util.Log
import com.example.recipes.business.domain.models.Recipe
import com.example.recipes.business.usecases.interfaces.SaveInFileCacheLoadRecipesUseCase
import com.google.gson.GsonBuilder
import java.io.File
import javax.inject.Inject

class SaveInFileCacheLoadRecipesUseCaseImpl @Inject constructor(): SaveInFileCacheLoadRecipesUseCase {
    override fun saveInFileCacheOfLoadRecipes(fileName: String, data: List<Recipe>) {
        if (!File(fileName).exists()) {
            File(fileName).createNewFile()
        }
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val cachedRecipeListString = gson.toJson(data)
        Log.d("TAG", "saveInFileCacheOfLoadRecipes: $cachedRecipeListString")

        File(fileName).writeText(cachedRecipeListString)
    }
}