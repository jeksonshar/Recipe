package com.example.recipes.db.network

import com.example.recipes.data.*
import com.example.recipes.db.network.entities.ImagesEntity
import com.example.recipes.db.network.entities.IngredientEntity

class ConverterModels {

    companion object {

        fun convertToImages(imagesEntity: ImagesEntity): Images {
            return Images(
                large = Large(
                    imagesEntity.large?.height ?: 0,
                    imagesEntity.large?.url ?: "",
                    imagesEntity.large?.width ?: 0
                ),
                regular = Regular(
                    imagesEntity.regular?.height ?: 0,
                    imagesEntity.regular?.url ?: "",
                    imagesEntity.regular?.width ?: 0
                ),
                small = Small(
                    imagesEntity.small?.height ?: 0,
                    imagesEntity.small?.url ?: "",
                    imagesEntity.small?.width ?: 0
                ),
                thumbnail = Thumbnail(
                    imagesEntity.thumbnail?.height ?: 0,
                    imagesEntity.thumbnail?.url ?: "",
                    imagesEntity.thumbnail?.width ?: 0
                )
            )
        }

        fun convertToIngredients(ingredientEntity: IngredientEntity): Ingredient {
            return Ingredient(
                food = ingredientEntity.food ?: "",
                foodCategory = ingredientEntity.foodCategory ?: "",
                foodId = ingredientEntity.foodId ?: "",
                image = ingredientEntity.image ?: "",
                measure = ingredientEntity.measure ?: "",
                quantity = ingredientEntity.quantity ?: 0,
                text = ingredientEntity.text ?: "",
                weight = ingredientEntity.weight ?: 0.toDouble()
            )
        }
    }
}