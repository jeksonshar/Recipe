package com.example.recipes.presentation.ui.recipes.searchlist.enums

enum class Diet(private val value: String) : FiltersGroups{
    BALANCED("balanced"),
    HIGH_FIBER("high-fiber"),
    HIGH_PROTEIN("high-protein"),
    LOW_CARB("low-carb"),
    LOW_FAT("low-fat"),
    LOW_SODIUM("low-sodium");

    override fun getValue(): String {
        return value
    }
}