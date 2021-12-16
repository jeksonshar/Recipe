package com.example.recipes.datasouce.room

import androidx.room.TypeConverter
import com.example.recipes.data.*
import com.example.recipes.datasouce.room.models.*

object ConvertersRoom {

    @TypeConverter
    fun convertToRecipeEntity(recipe: Recipe): RecipeEntity {
        return RecipeEntity(
            calories = recipe.calories,
            cautions = convertListToString(recipe.cautions),
            cuisineType = convertListToString(recipe.cuisineType),
            dietLabels = convertListToString(recipe.dietLabels),
            dishType = convertListToString(recipe.dishType),
            healthLabels = convertListToString(recipe.healthLabels),
            image = recipe.image,
            images = convertToImagesEntity(recipe.images),
            ingredientLines = convertListToString(recipe.ingredientLines),
            ingredients = convertToIngredientsEntity(recipe.ingredients),
            label = recipe.label,
            mealType = convertListToString(recipe.mealType),
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
    @TypeConverter
    fun convertToRecipe(entity: RecipeEntity): Recipe {
        return Recipe(
            calories = entity.calories,
            cautions = convertStringToList(entity.cautions),
            cuisineType = convertStringToList(entity.cuisineType),
            dietLabels = convertStringToList(entity.dietLabels),
            dishType = convertStringToList(entity.dishType),
            healthLabels = convertStringToList(entity.healthLabels),
            image = entity.image,
            images = convertToImages(entity.images),
            ingredientLines = convertStringToList(entity.ingredientLines),
            ingredients = convertToIngredients(entity.ingredients),
            label = entity.label,
            mealType = convertStringToList(entity.mealType),
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


    @TypeConverter
    fun convertToIngredientsEntity(ingredients: List<Ingredient>): List<IngredientEntity> {
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
    @TypeConverter
    fun convertToIngredients(ingredients: List<IngredientEntity>): List<Ingredient> {
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


    @TypeConverter
    private fun convertToImagesEntity(images: Images): ImagesEntity {
        return ImagesEntity(
            convertToLargeEntity(images.large),
            convertToRegularEntity(images.regular),
            convertToSmallEntity(images.small),
            convertToThumbnailEntity(images.thumbnail)
        )
    }
    @TypeConverter
    private fun convertToImages(images: ImagesEntity): Images {
        return Images(
            convertTotLarge(images.large),
            convertToRegular(images.regular),
            convertToSmall(images.small),
            convertToThumbnail(images.thumbnail)
        )
    }


    @TypeConverter
    private fun convertToLargeEntity(large: Large): LargeEntity {
        return LargeEntity(
            large.height,
            large.url,
            large.width
        )
    }
    @TypeConverter
    private fun convertTotLarge(entity: LargeEntity): Large {
        return Large(
            entity.height,
            entity.url,
            entity.width
        )
    }


    @TypeConverter
    private fun convertToRegularEntity(regular: Regular): RegularEntity {
        return RegularEntity(
            regular.height,
            regular.url,
            regular.width
        )
    }
    @TypeConverter
    private fun convertToRegular(regular: RegularEntity): Regular {
        return Regular(
            regular.height,
            regular.url,
            regular.width
        )
    }


    @TypeConverter
    private fun convertToSmallEntity(small: Small): SmallEntity {
        return SmallEntity(
            small.height,
            small.url,
            small.width
        )
    }
    @TypeConverter
    private fun convertToSmall(small: SmallEntity): Small {
        return Small(
            small.height,
            small.url,
            small.width
        )
    }


    @TypeConverter
    private fun convertToThumbnailEntity(thumbnail: Thumbnail): ThumbnailEntity {
        return ThumbnailEntity(
            thumbnail.height,
            thumbnail.url,
            thumbnail.width
        )
    }
    @TypeConverter
    private fun convertToThumbnail(thumbnail: ThumbnailEntity): Thumbnail {
        return Thumbnail(
            thumbnail.height,
            thumbnail.url,
            thumbnail.width
        )
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