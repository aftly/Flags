package dev.aftly.flags.data.room

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.aftly.flags.data.UserPreferencesRepository


private const val THEME_PREFERENCES_NAME = "theme_preferences.preferences_pb"

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
    val scoreItemsRepository: ScoreItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
        context.dataStoreFile(THEME_PREFERENCES_NAME)
    }
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore)
    }

    override val scoreItemsRepository: ScoreItemsRepository by lazy {
        OfflineScoreItemsRepository(ScoreHistoryDatabase.getDatabase(context).itemDao())
    }
}