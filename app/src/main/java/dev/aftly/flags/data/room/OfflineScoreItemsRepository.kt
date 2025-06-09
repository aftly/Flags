package dev.aftly.flags.data.room

import kotlinx.coroutines.flow.Flow

class OfflineScoreItemsRepository(private val itemDao: ScoreItemDao) : ScoreItemsRepository {
    override fun getAllItemsStream(): Flow<List<ScoreItem>> = itemDao.getAllScores()

    override suspend fun insertItem(item: ScoreItem) = itemDao.insert(item)
    override suspend fun deleteItem(item: ScoreItem) = itemDao.delete(item)
}