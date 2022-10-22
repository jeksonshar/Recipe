package com.example.recipes.datasouce.local.room.typeconverters

import androidx.room.TypeConverter
import com.example.recipes.business.domain.models.*
import com.example.recipes.datasouce.local.room.entities.*
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
    // понадибится, когда переделаю в модели recipe List на Set
    @TypeConverter
    fun convertSetToString(set: Set<String>): String {
        return set.joinToString(",")
    }
    @TypeConverter
    fun converterStringToSet(string: String?): Set<String> {
        return (string?.split(",") ?: emptyList()).toSet()
    }
}