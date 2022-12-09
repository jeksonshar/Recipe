package com.example.recipes.presentation.ui.recipes.searchlist.enums

enum class MealTypes(private val value: String) : FiltersGroups{
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SNACK("snack"),
    TEA_TIME("teatime");

    override fun getValue(): String {
        return value
    }
}