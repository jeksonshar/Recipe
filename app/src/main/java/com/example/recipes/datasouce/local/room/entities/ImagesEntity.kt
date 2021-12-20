package com.example.recipes.datasouce.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ImagesEntity(
    @Embedded val large: LargeEntity,
    @Embedded val regular: RegularEntity,
    @Embedded val small: SmallEntity,
    @Embedded val thumbnail: ThumbnailEntity
)

data class LargeEntity(
    @ColumnInfo(name = "large_height") val height: Int = 0,
    @ColumnInfo(name = "large_url") val url: String = "",
    @ColumnInfo(name = "large_width") val width: Int = 0
)

data class RegularEntity(
    @ColumnInfo(name = "regular_height") val height: Int = 0,
    @ColumnInfo(name = "regular_url") val url: String = "",
    @ColumnInfo(name = "regular_width") val width: Int = 0
)

data class SmallEntity(
    @ColumnInfo(name = "small_height") val height: Int = 0,
    @ColumnInfo(name = "small_url") val url: String = "",
    @ColumnInfo(name = "small_width") val width: Int = 0
)

data class ThumbnailEntity(
    @ColumnInfo(name = "thumbnail_height") val height: Int = 0,
    @ColumnInfo(name = "thumbnail_url") val url: String = "",
    @ColumnInfo(name = "thumbnail_width") val width: Int = 0
)
