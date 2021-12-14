package com.example.recipes

import com.example.recipes.data.*
import com.example.recipes.datasouce.network.entities.ImagesEntity
import com.example.recipes.datasouce.network.entities.IngredientEntity
import com.example.recipes.datasouce.network.entities.RecipeEntity
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity

object ConverterModels {

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
            convertToRecipe(it)
        }
    }

    fun getHrefNextRecipes(entity: RecipeSearchEntity): String {
        return entity.links?.next?.href ?: ""
    }

    fun convertToRecipe(entity: RecipeEntity?): Recipe {
        return Recipe(
            calories = entity?.calories ?: 0.toDouble(),
            cautions = entity?.cautions ?: emptyList(),
            cuisineType = entity?.cuisineType ?: emptyList(),
            dietLabels = entity?.dietLabels ?: emptyList(),
            dishType = entity?.dishType ?: emptyList(),
            healthLabels = entity?.healthLabels ?: emptyList(),
            image = entity?.image ?: "",
            images = convertToImages(entity?.images ?: ImagesEntity()),
            ingredientLines = entity?.ingredientLines ?: emptyList(),
            ingredients = entity?.ingredients?.map { ingridientEntity ->
                convertToIngredients(ingridientEntity)
            } ?: emptyList(),
            label = entity?.label ?: "",
            mealType = entity?.mealType ?: emptyList(),
            shareAs = entity?.shareAs ?: "",
            source = entity?.source ?: "",
            totalTime = entity?.totalTime ?: 0.toDouble(),
            totalWeight = entity?.totalWeight ?: 0.toDouble(),
            uri = entity?.uri ?: "",
            url = entity?.url ?: "",
            yield = entity?.yield ?: 0.toDouble()
        )
    }
}