package com.example.recipes.datasouce

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class RecipeDataStore(context: Context) {

    private val settingsDataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val LAST_QUERY = stringPreferencesKey("last query")
    }

    fun getLastQuery(): Flow<String> = settingsDataStore.data.map {
        it[LAST_QUERY] ?: "chicken"
    }

    suspend fun setLastQuery(query: String) {
        settingsDataStore.edit {
            it[LAST_QUERY] = query
        }
    }
}