package com.example.recipes.datasouce.room.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "images")
data class ImagesEntity(
    @Embedded val large: LargeEntity,
    @Embedded val regular: RegularEntity,
    @Embedded val small: SmallEntity,
    @Embedded val thumbnail: ThumbnailEntity
)

//@Entity(tableName = "large")
data class LargeEntity(
    @ColumnInfo(name = "large_height") val height: Int = 0,
    @ColumnInfo(name = "large_url") val url: String = "",
    @ColumnInfo(name = "large_width") val width: Int = 0
)

//@Entity(tableName = "regular")
data class RegularEntity(
    @ColumnInfo(name = "regular_height") val height: Int = 0,
    @ColumnInfo(name = "regular_url") val url: String = "",
    @ColumnInfo(name = "regular_width") val width: Int = 0
)

//@Entity(tableName = "small")
data class SmallEntity(
    @ColumnInfo(name = "small_height") val height: Int = 0,
    @ColumnInfo(name = "small_url") val url: String = "",
    @ColumnInfo(name = "small_width") val width: Int = 0
)

//@Entity(tableName = "thumbnail")
data class ThumbnailEntity(
    @ColumnInfo(name = "thumbnail_height") val height: Int = 0,
    @ColumnInfo(name = "thumbnail_url") val url: String = "",
    @ColumnInfo(name = "thumbnail_width") val width: Int = 0
)
