package com.example.recipes.presentation.ui.recipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.R
import com.example.recipes.business.domain.singletons.BackPressedSingleton
import com.example.recipes.business.domain.singletons.LoadedRecipesSingleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recipes)

        LoadedRecipesSingleton.clear()
    }

    override fun onBackPressed() {
        BackPressedSingleton.isBackPressClick = true
        super.onBackPressed()
    }
}