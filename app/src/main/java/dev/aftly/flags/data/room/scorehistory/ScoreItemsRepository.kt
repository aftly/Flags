package dev.aftly.flags.data.room.scorehistory

import kotlinx.coroutines.flow.Flow

interface ScoreItemsRepository {
    fun getAllItemsStream(): Flow<List<ScoreItem>>

    suspend fun insertItem(item: ScoreItem)
    suspend fun deleteItem(item: ScoreItem)
}

class OfflineScoreItemsRepository(private val itemDao: ScoreItemDao) : ScoreItemsRepository {
    override fun getAllItemsStream(): Flow<List<ScoreItem>> = itemDao.getAllScores()

    override suspend fun insertItem(item: ScoreItem) = itemDao.insert(item)
    override suspend fun deleteItem(item: ScoreItem) = itemDao.delete(item)
}