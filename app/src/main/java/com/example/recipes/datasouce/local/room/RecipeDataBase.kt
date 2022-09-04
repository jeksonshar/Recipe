package com.example.recipes.datasouce.local.room

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.recipes.datasouce.local.room.entities.*
import com.example.recipes.datasouce.local.room.typeconverters.ConvertersRoom

@Database(
    entities = [
        RecipeEntity::class
    ],
//    autoMigrations = [
//        AutoMigration(
//            from = 2,
//            to = 3,
//            spec = RecipeDataBase.MyAutoMigration::class
//        )
//    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(ConvertersRoom::class)
abstract class RecipeDataBase : RoomDatabase() {

//    @RenameTable(fromTableName = "Recipes.db", toTableName = "Recipes3.db")
//    @DeleteColumn(tableName = "recipes", columnName = "userId")
//    class MyAutoMigration : AutoMigrationSpec

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
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}