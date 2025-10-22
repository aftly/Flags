package dev.aftly.flags.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GamePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_FIRST_GAME = booleanPreferencesKey(name = "is_first_game")
        const val TAG = "GameRepo"
        const val ERROR_MESSAGE = "Error reading game datastore."
    }

    suspend fun onFirstGame() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_GAME] = false
        }
    }

    val isFirstGame: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, ERROR_MESSAGE, it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_FIRST_GAME] ?: true
        }
}