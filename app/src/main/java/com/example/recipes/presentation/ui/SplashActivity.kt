package com.example.recipes.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recipes.business.utils.CheckConnectionUtils
import com.example.recipes.datasouce.local.datastore.RecipeDataStore
import com.example.recipes.presentation.ui.auth.AuthActivity
import com.example.recipes.presentation.ui.viewpager.ViewPagerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity: ComponentActivity() {

    @Inject lateinit var recipeDataStore: RecipeDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        CheckConnectionUtils.getNetConnection(applicationContext)

        val isNotFirstLaunch = runBlocking { recipeDataStore.checkNotFistLaunch().first() }
        if (isNotFirstLaunch) {
//            startActivity(Intent(this, RecipesActivity::class.java))
            startActivity(Intent(this, AuthActivity::class.java))
        } else {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        finish()
    }
}