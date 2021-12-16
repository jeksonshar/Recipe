package com.example.recipes.datasouce

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.recipes.data.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeDataStore(context: Context) {

    private val settingsDataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val LAST_QUERY = stringPreferencesKey("last query")

        private val RECIPE_KEY = stringSetPreferencesKey("recipe_uri")
        private val listRecipe = mutableSetOf<String>()
    }

    fun getLastQuery(): Flow<String> = settingsDataStore.data.map {
        it[LAST_QUERY] ?: "chicken"
    }

    suspend fun setLastQuery(query: String) {
        settingsDataStore.edit {
            it[LAST_QUERY] = query
        }
    }

    suspend fun setFavoriteRecipe(recipe: Recipe) {
        settingsDataStore.edit {
            it[RECIPE_KEY] = listRecipe.plus(recipe.uri)
        }
    }

    suspend fun delFavoriteRecipe(recipe: Recipe) {
        settingsDataStore.edit {
            it[RECIPE_KEY] = listRecipe.minus(recipe.uri)
        }
    }

    fun isContainFavoriteRecipe(recipe: Recipe): Flow<Boolean> = settingsDataStore.data.map {
        it[RECIPE_KEY]?.contains(recipe.uri) ?: false
    }
}