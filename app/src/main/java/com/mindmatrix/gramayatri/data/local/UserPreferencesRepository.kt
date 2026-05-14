package com.mindmatrix.gramayatri.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")

    val displayName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[DISPLAY_NAME_KEY] ?: "" }

    suspend fun saveDisplayName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[DISPLAY_NAME_KEY] = name
        }
    }
}