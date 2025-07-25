package dev.aftly.flags.data.room.scorehistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ScoreItem)

    @Delete
    suspend fun delete(item: ScoreItem)

    @Query(value = "SELECT * from score_items ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<ScoreItem>>
}