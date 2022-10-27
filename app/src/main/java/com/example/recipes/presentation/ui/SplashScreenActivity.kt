package com.example.recipes.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.example.recipes.presentation.ui.recipes.RecipesActivity
import com.example.recipes.presentation.ui.viewpager.ViewPagerActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity: ComponentActivity() {

    @Inject lateinit var recipeDataStore: RecipeDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val isNotFirstLaunch = runBlocking { recipeDataStore.checkNotFistLaunch().first() }
        val currentUser = Firebase.auth.currentUser

        when {
            !isNotFirstLaunch -> {
                startActivity(newIntent(this, ViewPagerActivity()))
            }
            isNotFirstLaunch && currentUser == null -> {
                startActivity(newIntent(this, AuthActivity()))
            }
            isNotFirstLaunch && currentUser != null -> {
                startActivity(newIntent(this, RecipesActivity()))
            }
        }

        finish()
    }

    companion object {
        fun newIntent(context: Context, activity: AppCompatActivity): Intent {
            return Intent(context, activity::class.java)
        }
    }

}