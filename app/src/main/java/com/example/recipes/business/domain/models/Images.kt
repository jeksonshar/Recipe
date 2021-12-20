package com.example.recipes.business.domain.models

data class Images(
    val large: Large,
    val regular: Regular,
    val small: Small,
    val thumbnail: Thumbnail
)

data class Large(
    val height: Int,
    val url: String,
    val width: Int
)
data class Regular(
    val height: Int,
    val url: String,
    val width: Int
)

data class Small(
    val height: Int,
    val url: String,
    val width: Int
)

data class Thumbnail(
    val height: Int,
    val url: String,
    val width: Int
)