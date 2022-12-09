package com.example.recipes.presentation.ui.recipes.searchlist.enums

enum class Health(private val value: String) : FiltersGroups {
    ALCOHOL_COCKTAIL("alcohol-cocktail"),
    ALCOHOL_FREE("alcohol-free"),
    CELERY_FREE("celery-free"),
    CRUSTACEAN_FREE("crustacean-free"),
    DAIRY_FREE("dairy-free"),
    EGG_FREE("egg-free"),
    FISH_FREE("fish-free"),
    GLUTEN_FREE("gluten-free"),
    IMMUNE_SUPPORTIVE("immuno-supportive"),
    KOSHER("kosher"),
    LOW_SUGAR("low-sugar"),
    MOLLUSK_FREE("mollusk-free"),
    VEGETARIAN("vegetarian");

    override fun getValue(): String {
        return value
    }
}