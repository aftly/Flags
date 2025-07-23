package dev.aftly.flags.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.aftly.flags.ui.theme.AppThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val THEME = stringPreferencesKey(name = "theme")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey(name = "is_dynamic_color")
        const val TAG = "UserPreferencesRepo"
        const val ERROR_MESSAGE = "Error reading preferences."
    }

    suspend fun saveTheme(theme: AppThemePreference) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme.name
        }
    }

    suspend fun saveDynamicColor(isDynamicColor: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLOR] = isDynamicColor
        }
    }

    val theme: Flow<AppThemePreference> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, ERROR_MESSAGE, it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            AppThemePreference.get(preferences[THEME])
        }

    val isDynamicColor: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, ERROR_MESSAGE, it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_DYNAMIC_COLOR] ?: false
        }
}