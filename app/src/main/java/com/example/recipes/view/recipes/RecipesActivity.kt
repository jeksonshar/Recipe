package com.example.recipes.view.recipes

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.R

class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentRecipesContainer, RecipeListFragment(), CURRENT_FRAGMENT_TAG)
                .commit()
        } else {
            supportFragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG)
        }
    }

    companion object {
        const val CURRENT_FRAGMENT_TAG = "CurrentFragment"
    }
}