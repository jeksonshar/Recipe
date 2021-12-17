package com.example.recipes.datasouce.room

import androidx.room.TypeConverter
import com.example.recipes.data.*
import com.example.recipes.datasouce.room.models.*
import com.google.gson.Gson

object ConvertersRoom {

    @TypeConverter
    fun convertIngredientsToString(ingredients: List<IngredientEntity>): String {
        return Gson().toJson(ingredients)
    }

    @TypeConverter
    fun convertJsonToIngredients(json: String): List<IngredientEntity> {
        return Gson().fromJson(json, Array<IngredientEntity>::class.java).toList()
    }

    @TypeConverter
    fun convertListToString(list: List<String>): String {
        return list.joinToString(",")
    }
    @TypeConverter
    fun convertStringToList(string: String?): List<String> {
        return string?.split(",") ?: emptyList()
    }
}