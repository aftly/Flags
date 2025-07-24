package dev.aftly.flags.data.room.savedflags

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedFlagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flag: SavedFlag)

    @Delete
    suspend fun delete(flag: SavedFlag)

    @Query(value = "SELECT * FROM saved_flags ORDER BY id ASC")
    fun getAllSavedFlags(): Flow<List<SavedFlag>>
}