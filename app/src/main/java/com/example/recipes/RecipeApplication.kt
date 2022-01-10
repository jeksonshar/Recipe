package com.example.recipes

import android.app.Application
import com.example.recipes.datasouce.local.room.RecipeDataBase

class RecipeApplication : Application() {

    var db: RecipeDataBase? = null

    override fun onCreate() {
        super.onCreate()

        db = RecipeDataBase.getDataBase(applicationContext)
    }
}