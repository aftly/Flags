package dev.aftly.flags

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.aftly.flags.data.UserPreferencesRepository

private const val THEME_PREFERENCES_NAME = "theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCES_NAME
)

class FlagsApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}