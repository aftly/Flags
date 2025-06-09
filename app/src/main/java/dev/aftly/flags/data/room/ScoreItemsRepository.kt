package dev.aftly.flags.data.room

import kotlinx.coroutines.flow.Flow

interface ScoreItemsRepository {
    fun getAllItemsStream(): Flow<List<ScoreItem>>

    suspend fun insertItem(item: ScoreItem)
    suspend fun deleteItem(item: ScoreItem)
}