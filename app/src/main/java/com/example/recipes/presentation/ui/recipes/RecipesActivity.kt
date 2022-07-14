package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.R
import com.example.recipes.presentation.ui.recipes.searchlist.LoadingRecipesSingleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recipes)

        LoadingRecipesSingleton.clear()
    }

    override fun onBackPressed() {
        BackPressedSingleton.isBackPressClick = true
        super.onBackPressed()
    }
}