package com.example.recipes.view.recipes

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.R

class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentRecipesContainer, RecipeListFragment()).commit()
    }
}