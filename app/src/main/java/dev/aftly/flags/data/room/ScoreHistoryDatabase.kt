package dev.aftly.flags.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScoreItem::class], version = 1, exportSchema = false)
abstract class ScoreHistoryDatabase : RoomDatabase() {
    abstract fun itemDao(): ScoreItemDao
    companion object {
        @Volatile
        private var Instance: ScoreHistoryDatabase? = null

        fun getDatabase(context: Context): ScoreHistoryDatabase {
            return Instance ?: synchronized(lock = this) {
                Room.databaseBuilder(
                    context, ScoreHistoryDatabase::class.java, "score_history_database"
                ).fallbackToDestructiveMigration(dropAllTables = true) // TODO: Replace
                    .build()
                    .also { Instance = it }
            }
        }
    }
}