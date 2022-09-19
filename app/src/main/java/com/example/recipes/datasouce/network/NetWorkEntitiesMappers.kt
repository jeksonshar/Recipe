package com.example.recipes.datasouce.network

import com.example.recipes.business.domain.models.*
import com.example.recipes.datasouce.network.entities.ImagesEntity
import com.example.recipes.datasouce.network.entities.IngredientEntity
import com.example.recipes.datasouce.network.entities.RecipeEntity
import com.example.recipes.datasouce.network.entities.RecipeSearchEntity

object NetWorkEntitiesMappers {

    fun mapToRecipes(model: RecipeSearchEntity): List<Recipe> {
        val recipeEntity: ArrayList<RecipeEntity> = ArrayList()
        model.hits?.forEach {
            recipeEntity.add(it.recipeEntity ?: RecipeEntity())
        }
        return recipeEntity.map {
            mapToRecipe(it)
        }
    }

    fun getHrefNextRecipes(model: RecipeSearchEntity): String {
        return model.links?.next?.href ?: ""
    }

    fun mapToRecipe(entity: RecipeEntity?): Recipe {
        return Recipe(
            calories = entity?.calories ?: 0.0,
            cautions = entity?.cautions ?: emptyList(),
            cuisineType = entity?.cuisineType ?: emptyList(),
            dietLabels = entity?.dietLabels ?: emptyList(),
            dishType = entity?.dishType ?: emptyList(),
            healthLabels = entity?.healthLabels ?: emptyList(),
            image = entity?.image ?: "",
            images = mapToImages(entity?.images),
            ingredientLines = entity?.ingredientLines ?: emptyList(),
            ingredients = entity?.ingredients?.map { ingredientEntity ->
                mapToIngredients(ingredientEntity)
            } ?: emptyList(),
            label = entity?.label ?: "",
            mealType = entity?.mealType ?: emptyList(),
            shareAs = entity?.shareAs ?: "",
            source = entity?.source ?: "",
            totalTime = entity?.totalTime ?: 0.0,
            totalWeight = entity?.totalWeight ?: 0.0,
            uri = entity?.uri ?: "",
            url = entity?.url ?: "",
            yield = entity?.yield ?: 0.0
        )
    }

    private fun mapToImages(imagesEntity: ImagesEntity?): Images {
        return Images(
            large = Large(
                imagesEntity?.large?.height ?: 0,
                imagesEntity?.large?.url ?: "",
                imagesEntity?.large?.width ?: 0
            ),
            regular = Regular(
                imagesEntity?.regular?.height ?: 0,
                imagesEntity?.regular?.url ?: "",
                imagesEntity?.regular?.width ?: 0
            ),
            small = Small(
                imagesEntity?.small?.height ?: 0,
                imagesEntity?.small?.url ?: "",
                imagesEntity?.small?.width ?: 0
            ),
            thumbnail = Thumbnail(
                imagesEntity?.thumbnail?.height ?: 0,
                imagesEntity?.thumbnail?.url ?: "",
                imagesEntity?.thumbnail?.width ?: 0
            )
        )
    }

    private fun mapToIngredients(ingredientEntity: IngredientEntity): Ingredient {
        return Ingredient(
            food = ingredientEntity.food ?: "",
            foodCategory = ingredientEntity.foodCategory ?: "",
            foodId = ingredientEntity.foodId ?: "",
            image = ingredientEntity.image ?: "",
            measure = ingredientEntity.measure ?: "",
            quantity = ingredientEntity.quantity ?: 0.0,
            text = ingredientEntity.text ?: "",
            weight = ingredientEntity.weight ?: 0.0
        )
    }
}