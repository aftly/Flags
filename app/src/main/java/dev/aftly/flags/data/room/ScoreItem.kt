package dev.aftly.flags.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.TimeMode
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "score_items")
data class ScoreItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val score: Int,
    val outOf: Int,
    val timeMode: TimeMode,
    val timerStart: Int?,
    val timerEnd: Int,

    /* Handled by Room @TypeConverters */
    val gameSuperCategories: List<FlagSuperCategory>,
    val gameSubCategories: List<FlagCategory>,
    val flagsAll: List<FlagResources>,
    val flagsGuessed: List<FlagResources>,
    val flagsSkipped: List<FlagResources>,
    val flagsShown: List<FlagResources>,
)