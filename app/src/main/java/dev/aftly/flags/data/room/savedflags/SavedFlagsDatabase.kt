package dev.aftly.flags.data.room.savedflags

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.aftly.flags.data.room.Converters

@Database(entities = [SavedFlag::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SavedFlagsDatabase : RoomDatabase() {
    abstract fun flagDao(): SavedFlagDao
    companion object {
        @Volatile
        private var Instance: SavedFlagsDatabase? = null

        fun getDatabase(context: Context): SavedFlagsDatabase {
            return Instance ?: synchronized(lock = this) {
                Room
                    .databaseBuilder(
                        context = context,
                        klass = SavedFlagsDatabase::class.java,
                        name = "saved_flags_database"
                    )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}