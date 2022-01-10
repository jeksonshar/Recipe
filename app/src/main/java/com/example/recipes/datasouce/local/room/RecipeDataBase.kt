package com.example.recipes.datasouce.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipes.datasouce.local.room.entities.*
import com.example.recipes.datasouce.local.room.typeconverters.ConvertersRoom

@Database(
    entities = [
        RecipeEntity::class/*,
        IngredientEntity::class,
        ImagesEntity::class,
        LargeEntity::class,
        RegularEntity::class,
        SmallEntity::class,
        ThumbnailEntity::class*/
    ], version = 2
)
@TypeConverters(ConvertersRoom::class)
abstract class RecipeDataBase : RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

    companion object {
        private const val DATABASE_NAME = "Recipes.db"

        @Volatile
        private var INSTANCE: RecipeDataBase? = null

        fun getDataBase(context: Context): RecipeDataBase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDataBase(context).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDataBase(context: Context): RecipeDataBase {

            return Room.databaseBuilder(
                context.applicationContext,
                RecipeDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}