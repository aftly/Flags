package dev.aftly.flags.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.aftly.flags.data.datastore.GamePreferencesRepository
import dev.aftly.flags.data.datastore.UserPreferencesRepository
import dev.aftly.flags.data.room.savedflags.OfflineSavedFlagsRepository
import dev.aftly.flags.data.room.savedflags.SavedFlagsDatabase
import dev.aftly.flags.data.room.savedflags.SavedFlagsRepository
import dev.aftly.flags.data.room.scorehistory.OfflineScoreItemsRepository
import dev.aftly.flags.data.room.scorehistory.ScoreHistoryDatabase
import dev.aftly.flags.data.room.scorehistory.ScoreItemsRepository


private const val THEME_PREFERENCES_NAME = "theme_preferences.preferences_pb"

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
    val gamePreferencesRepository: GamePreferencesRepository
    val scoreItemsRepository: ScoreItemsRepository
    val savedFlagsRepository: SavedFlagsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
        context.dataStoreFile(THEME_PREFERENCES_NAME)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore)
    }
    override val gamePreferencesRepository: GamePreferencesRepository by lazy {
        GamePreferencesRepository(dataStore)
    }
    override val scoreItemsRepository: ScoreItemsRepository by lazy {
        OfflineScoreItemsRepository(ScoreHistoryDatabase.getDatabase(context).itemDao())
    }
    override val savedFlagsRepository: SavedFlagsRepository by lazy {
        OfflineSavedFlagsRepository(SavedFlagsDatabase.getDatabase(context).flagDao())
    }
}