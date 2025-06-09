package dev.aftly.flags.data.room

import android.content.Context

interface AppContainer {
    val scoreItemsRepository: ScoreItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val scoreItemsRepository: ScoreItemsRepository by lazy {
        OfflineScoreItemsRepository(ScoreHistoryDatabase.getDatabase(context).itemDao())
    }
}