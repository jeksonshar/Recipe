package com.example.recipes

import com.example.recipes.data.*
import com.example.recipes.datasouce.network.entities.ImagesEntity
import com.example.recipes.datasouce.network.entities.IngredientEntity
import com.example.recipes.datasouce.network.entities.RecipeEntity
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity

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
                quantity = ingredientEntity.quantity ?: 0.toDouble(),
                text = ingredientEntity.text ?: "",
                weight = ingredientEntity.weight ?: 0.toDouble()
            )
        }

        fun convertToRecipes(entity: RecipeSearchEntity): List<Recipe> {
            val recipeEntity: ArrayList<RecipeEntity> = ArrayList()
            entity.hits?.forEach {
                recipeEntity.add(it.recipeEntity ?: RecipeEntity())
            }
            return recipeEntity.map {
                Recipe(
                    calories = it.calories ?: 0.toDouble(),
                    cautions = it.cautions ?: emptyList(),
                    cuisineType = it.cuisineType ?: emptyList(),
                    dietLabels = it.dietLabels ?: emptyList(),
                    dishType = it.dishType ?: emptyList(),
                    healthLabels = it.healthLabels ?: emptyList(),
                    image = it.image ?: "",
                    images = convertToImages(it.images ?: ImagesEntity()),
                    ingredientLines = it.ingredientLines ?: emptyList(),
                    ingredients = it.ingredients?.map { ingridientEntity ->
                        convertToIngredients(ingridientEntity)
                    } ?: emptyList(),
                    label = it.label ?: "",
                    mealType = it.mealType ?: emptyList(),
                    shareAs = it.shareAs ?: "",
                    source = it.source ?: "",
                    totalTime = it.totalTime ?: 0.toDouble(),
                    totalWeight = it.totalWeight ?: 0.toDouble(),
                    uri = it.uri ?: "",
                    url = it.url ?: "",
                    yield = it.yield ?: 0.toDouble()
                )
            }
        }

        fun getHrefNextRecipes(entity: RecipeSearchEntity): String {
            return entity.links?.next?.href ?: ""
        }
    }
}