package dev.aftly.flags.data.room.scorehistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.TimeMode
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "score_items")
data class ScoreItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val score: Int,
    @ColumnInfo(name = "out_of")
    val outOf: Int,
    @ColumnInfo(name = "time_mode")
    val timeMode: TimeMode,
    @ColumnInfo(name = "timer_start")
    val timerStart: Int?,
    @ColumnInfo(name = "timer_end")
    val timerEnd: Int,

    /* Handled by Room @TypeConverters */
    @ColumnInfo(name = "game_super_categories")
    val gameSuperCategories: List<FlagCategoryBase>,
    @ColumnInfo(name = "game_sub_categories")
    val gameSubCategories: List<FlagCategory>,
    @ColumnInfo(name = "flags_all")
    val flagsAll: List<FlagResources>,
    @ColumnInfo(name = "flags_guessed")
    val flagsGuessed: List<FlagResources>,
    @ColumnInfo(name = "flags_skipped_guessed")
    val flagsSkippedGuessed: List<FlagResources> = emptyList(),
    @ColumnInfo(name = "flags_skipped")
    val flagsSkipped: List<FlagResources>,
    @ColumnInfo(name = "flags_shown")
    val flagsShown: List<FlagResources>,
)