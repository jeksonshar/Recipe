package com.example.recipes.datasouce.network

import com.example.recipes.data.*
import com.example.recipes.datasouce.network.models.ImagesModel
import com.example.recipes.datasouce.network.models.IngredientModel
import com.example.recipes.datasouce.network.models.RecipeModel
import com.example.recipes.datasouce.network.models.RecipeSearchModel

object NetWorkEntitiesMappers {

    fun mapToRecipes(model: RecipeSearchModel): List<Recipe> {
        val recipeEntity: ArrayList<RecipeModel> = ArrayList()
        model.hits?.forEach {
            recipeEntity.add(it.recipeModel ?: RecipeModel())
        }
        return recipeEntity.map {
            mapToRecipe(it)
        }
    }

    fun getHrefNextRecipes(model: RecipeSearchModel): String {
        return model.links?.next?.href ?: ""
    }

    fun mapToRecipe(model: RecipeModel?): Recipe {
        return Recipe(
            calories = model?.calories ?: 0.0,
            cautions = model?.cautions ?: emptyList(),
            cuisineType = model?.cuisineType ?: emptyList(),
            dietLabels = model?.dietLabels ?: emptyList(),
            dishType = model?.dishType ?: emptyList(),
            healthLabels = model?.healthLabels ?: emptyList(),
            image = model?.image ?: "",
            images = mapToImages(model?.images ?: ImagesModel()),
            ingredientLines = model?.ingredientLines ?: emptyList(),
            ingredients = model?.ingredients?.map { ingredientEntity ->
                mapToIngredients(ingredientEntity)
            } ?: emptyList(),
            label = model?.label ?: "",
            mealType = model?.mealType ?: emptyList(),
            shareAs = model?.shareAs ?: "",
            source = model?.source ?: "",
            totalTime = model?.totalTime ?: 0.0,
            totalWeight = model?.totalWeight ?: 0.0,
            uri = model?.uri ?: "",
            url = model?.url ?: "",
            yield = model?.yield ?: 0.0
        )
    }

    private fun mapToImages(imagesModel: ImagesModel): Images {
        return Images(
            large = Large(
                imagesModel.large?.height ?: 0,
                imagesModel.large?.url ?: "",
                imagesModel.large?.width ?: 0
            ),
            regular = Regular(
                imagesModel.regular?.height ?: 0,
                imagesModel.regular?.url ?: "",
                imagesModel.regular?.width ?: 0
            ),
            small = Small(
                imagesModel.small?.height ?: 0,
                imagesModel.small?.url ?: "",
                imagesModel.small?.width ?: 0
            ),
            thumbnail = Thumbnail(
                imagesModel.thumbnail?.height ?: 0,
                imagesModel.thumbnail?.url ?: "",
                imagesModel.thumbnail?.width ?: 0
            )
        )
    }

    private fun mapToIngredients(ingredientModel: IngredientModel): Ingredient {
        return Ingredient(
            food = ingredientModel.food ?: "",
            foodCategory = ingredientModel.foodCategory ?: "",
            foodId = ingredientModel.foodId ?: "",
            image = ingredientModel.image ?: "",
            measure = ingredientModel.measure ?: "",
            quantity = ingredientModel.quantity ?: 0.0,
            text = ingredientModel.text ?: "",
            weight = ingredientModel.weight ?: 0.0
        )
    }
}