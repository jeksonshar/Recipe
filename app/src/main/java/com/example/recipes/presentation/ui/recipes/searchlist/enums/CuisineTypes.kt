package com.example.recipes.presentation.ui.recipes.searchlist.enums

enum class CuisineTypes(private val value: String) : FiltersGroups{
    AMERICAN("american"),
    ASIAN("asian"),
    BRITISH("british"),
    CHINESE("chinese"),
    FRENCH("french"),
    GREEK("greek"),
    INDIAN("indian"),
    ITALIAN("italian"),
    KOREAN("korean"),
    MEXICAN("mexican");

    override fun getValue(): String {
        return value
    }
}