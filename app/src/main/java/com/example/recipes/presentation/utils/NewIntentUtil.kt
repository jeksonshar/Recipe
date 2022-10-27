package com.example.recipes.presentation.utils

import android.content.Intent
import com.example.recipes.business.domain.models.Recipe

object NewIntentUtil {

    fun createNewShareIntent(recipe: Recipe): Intent {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" //для URL можно использовать text/x-uri

            putExtra(Intent.EXTRA_TEXT, recipe.shareAs)

        }
        return Intent.createChooser(intent, "Выбери месседжер:")
    }

}