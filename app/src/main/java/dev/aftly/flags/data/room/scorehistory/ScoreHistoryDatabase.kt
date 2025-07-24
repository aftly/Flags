package dev.aftly.flags.data.room.scorehistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.aftly.flags.data.room.Converters

@Database(entities = [ScoreItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScoreHistoryDatabase : RoomDatabase() {
    abstract fun itemDao(): ScoreItemDao
    companion object {
        private val MIGRATION_1_2 = object : Migration(startVersion = 1, endVersion = 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    |CREATE TABLE `score_items_temp` (
                        |`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        |`timestamp` INTEGER NOT NULL, 
                        |`score` INTEGER NOT NULL, 
                        |`out_of` INTEGER NOT NULL, 
                        |`time_mode` TEXT NOT NULL, 
                        |`timer_start` INTEGER, 
                        |`timer_end` INTEGER NOT NULL, 
                        |`game_super_categories` TEXT NOT NULL, 
                        |`game_sub_categories` TEXT NOT NULL, 
                        |`flags_all` TEXT NOT NULL, 
                        |`flags_guessed` TEXT NOT NULL, 
                        |`flags_skipped_guessed` TEXT NOT NULL DEFAULT '[]', 
                        |`flags_skipped` TEXT NOT NULL, 
                        |`flags_shown` TEXT NOT NULL
                    |)
                """.trimMargin())

                db.execSQL("""
                    |INSERT INTO `score_items_temp` (
                        |`id`, 
                        |`timestamp`, 
                        |`score`, 
                        |`out_of`, 
                        |`time_mode`, 
                        |`timer_start`, 
                        |`timer_end`, 
                        |`game_super_categories`, 
                        |`game_sub_categories`, 
                        |`flags_all`, 
                        |`flags_guessed`, 
                        |`flags_skipped`, 
                        |`flags_shown`
                    |)
                    |SELECT 
                        |`id`, 
                        |`timestamp`, 
                        |`score`, 
                        |`outOf`, 
                        |`timeMode`, 
                        |`timerStart`, 
                        |`timerEnd`, 
                        |`gameSuperCategories`, 
                        |`gameSubCategories`, 
                        |`flagsAll`, 
                        |`flagsGuessed`, 
                        |`flagsSkipped`, 
                        |`flagsShown` 
                    |FROM `score_items`
                """.trimMargin())

                db.execSQL("DROP TABLE score_items")
                db.execSQL("ALTER TABLE `score_items_temp` RENAME TO `score_items`")
            }
        }

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
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}