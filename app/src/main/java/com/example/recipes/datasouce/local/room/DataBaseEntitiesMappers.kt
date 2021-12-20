package com.example.recipes.datasouce.local.room

import com.example.recipes.business.domain.models.*
import com.example.recipes.datasouce.local.room.entities.*

object DataBaseEntitiesMappers {

    fun mapToRecipeEntity(recipe: Recipe): RecipeEntity {
        return RecipeEntity(
            calories = recipe.calories,
            cautions = recipe.cautions,
            cuisineType = recipe.cuisineType,
            dietLabels = recipe.dietLabels,
            dishType = recipe.dishType,
            healthLabels = recipe.healthLabels,
            image = recipe.image,
            images = mapToImagesEntity(recipe.images),
            ingredientLines = recipe.ingredientLines,
            ingredients = mapToIngredientsEntity(recipe.ingredients),
            label = recipe.label,
            mealType = recipe.mealType,
            shareAs = recipe.shareAs,
            source = recipe.source,
            totalWeight = recipe.totalWeight,
            totalTime = recipe.totalTime,
            uri = recipe.uri,
            url = recipe.url,
            yield = recipe.yield,
            isFavorite = recipe.isFavorite
        )
    }

    fun mapToRecipe(entity: RecipeEntity): Recipe {
        return Recipe(
            calories = entity.calories,
            cautions = entity.cautions,
            cuisineType = entity.cuisineType,
            dietLabels = entity.dietLabels,
            dishType = entity.dishType,
            healthLabels = entity.healthLabels,
            image = entity.image,
            images = mapToImages(entity.images),
            ingredientLines = entity.ingredientLines,
            ingredients = mapToIngredients(entity.ingredients),
            label = entity.label,
            mealType = entity.mealType,
            shareAs = entity.shareAs,
            source = entity.source,
            totalTime = entity.totalTime,
            totalWeight = entity.totalWeight,
            uri = entity.uri,
            url = entity.url,
            yield = entity.yield,
            isFavorite = entity.isFavorite
        )
    }


    private fun mapToIngredientsEntity(ingredients: List<Ingredient>): List<IngredientEntity> {
        val listEntity: MutableList<IngredientEntity> = ArrayList()
        for (ingredient in ingredients) {
            listEntity.add(
                IngredientEntity(
                    foodId = ingredient.foodId,
                    food = ingredient.food,
                    foodCategory = ingredient.foodCategory,
                    image = ingredient.image,
                    measure = ingredient.measure,
                    quantity = ingredient.quantity,
                    text = ingredient.text,
                    weight = ingredient.weight
                )
            )
        }
        return listEntity
    }

    private fun mapToIngredients(ingredients: List<IngredientEntity>): List<Ingredient> {
        val listEntity: MutableList<Ingredient> = ArrayList()
        for (ingredient in ingredients) {
            listEntity.add(
                Ingredient(
                    foodId = ingredient.foodId,
                    food = ingredient.food,
                    foodCategory = ingredient.foodCategory,
                    image = ingredient.image,
                    measure = ingredient.measure,
                    quantity = ingredient.quantity,
                    text = ingredient.text,
                    weight = ingredient.weight
                )
            )
        }
        return listEntity
    }


    private fun mapToImagesEntity(images: Images): ImagesEntity {
        return ImagesEntity(
            mapToLargeEntity(images.large),
            mapToRegularEntity(images.regular),
            mapToSmallEntity(images.small),
            mapToThumbnailEntity(images.thumbnail)
        )
    }
    private fun mapToImages(images: ImagesEntity): Images {
        return Images(
            mapTotLarge(images.large),
            mapToRegular(images.regular),
            mapToSmall(images.small),
            mapToThumbnail(images.thumbnail)
        )
    }


    private fun mapToLargeEntity(large: Large): LargeEntity {
        return LargeEntity(
            large.height,
            large.url,
            large.width
        )
    }
    private fun mapTotLarge(entity: LargeEntity): Large {
        return Large(
            entity.height,
            entity.url,
            entity.width
        )
    }


    private fun mapToRegularEntity(regular: Regular): RegularEntity {
        return RegularEntity(
            regular.height,
            regular.url,
            regular.width
        )
    }
    private fun mapToRegular(regular: RegularEntity): Regular {
        return Regular(
            regular.height,
            regular.url,
            regular.width
        )
    }


    private fun mapToSmallEntity(small: Small): SmallEntity {
        return SmallEntity(
            small.height,
            small.url,
            small.width
        )
    }
    private fun mapToSmall(small: SmallEntity): Small {
        return Small(
            small.height,
            small.url,
            small.width
        )
    }


    private fun mapToThumbnailEntity(thumbnail: Thumbnail): ThumbnailEntity {
        return ThumbnailEntity(
            thumbnail.height,
            thumbnail.url,
            thumbnail.width
        )
    }
    private fun mapToThumbnail(thumbnail: ThumbnailEntity): Thumbnail {
        return Thumbnail(
            thumbnail.height,
            thumbnail.url,
            thumbnail.width
        )
    }
}