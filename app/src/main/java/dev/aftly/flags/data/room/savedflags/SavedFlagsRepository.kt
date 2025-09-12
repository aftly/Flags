package dev.aftly.flags.data.room.savedflags

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toSet

interface SavedFlagsRepository {
    fun getAllFlagsStream(): Flow<List<SavedFlag>>

    suspend fun insertFlag(flag: SavedFlag)
    suspend fun deleteFlag(flag: SavedFlag)
}

class OfflineSavedFlagsRepository(private val flagDao: SavedFlagDao) : SavedFlagsRepository {
    override fun getAllFlagsStream(): Flow<List<SavedFlag>> = flagDao.getAllSavedFlags()

    override suspend fun insertFlag(flag: SavedFlag) = flagDao.insert(flag)
    override suspend fun deleteFlag(flag: SavedFlag) = flagDao.delete(flag)
}
