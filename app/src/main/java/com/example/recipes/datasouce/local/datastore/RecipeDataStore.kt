package com.example.recipes.datasouce.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeDataStore @Inject constructor(
    @ApplicationContext context: Context
) {

    private val settingsDataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val LAST_QUERY = stringPreferencesKey("last query")

        private val IS_NOT_FIRST_LAUNCH = booleanPreferencesKey("is not first launch")
    }

    fun getLastQuery(): Flow<String?> = settingsDataStore.data.map {
        it[LAST_QUERY]
    }

    suspend fun setLastQuery(query: String) {
        settingsDataStore.edit {
            it[LAST_QUERY] = query
        }
    }

    fun checkNotFistLaunch(): Flow<Boolean> {
        return settingsDataStore.data.map {
            it[IS_NOT_FIRST_LAUNCH] ?: false
        }
    }

    suspend fun setNotFirstLaunch() {
        settingsDataStore.edit {
            it[IS_NOT_FIRST_LAUNCH] = true
        }
    }

}