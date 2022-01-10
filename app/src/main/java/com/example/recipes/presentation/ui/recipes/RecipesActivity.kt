package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.recipes.R
import com.example.recipes.presentation.ui.recipes.searchlist.RecipeSearchListFragment

class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)
    }
}