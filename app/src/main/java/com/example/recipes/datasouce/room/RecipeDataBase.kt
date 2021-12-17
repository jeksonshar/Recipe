package com.example.recipes.datasouce.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipes.datasouce.room.models.*

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

        fun create(context: Context): RecipeDataBase {
            return Room.databaseBuilder(
                context,
                RecipeDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}