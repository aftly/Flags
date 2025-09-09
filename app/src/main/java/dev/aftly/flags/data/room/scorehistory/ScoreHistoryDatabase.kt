package dev.aftly.flags.data.room.scorehistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.aftly.flags.data.room.Converters

@Database(entities = [ScoreItem::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScoreHistoryDatabase : RoomDatabase() {
    abstract fun itemDao(): ScoreItemDao
    companion object {
        @Volatile
        private var Instance: ScoreHistoryDatabase? = null

        fun getDatabase(context: Context): ScoreHistoryDatabase {
            return Instance ?: synchronized(lock = this) {
                Room
                    .databaseBuilder(
                        context = context,
                        klass = ScoreHistoryDatabase::class.java,
                        name = "score_history_database"
                    )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}