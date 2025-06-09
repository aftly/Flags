package dev.aftly.flags.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_items")
data class ScoreItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val score: Int,
    val outOf: Int,
    val timeMode: String,
    val timerStart: Int?,
    val timerEnd: Int,

    /* JSON strings representing List<FlagSuperCategory> and List<FlagCategory> */
    val flagSuperCategoriesJson: String,
    val flagSubCategoriesJson: String,

    /* JSON strings representing List<FlagResources> */
    val flagsGuessedJson: String,
    val flagsSkippedJson: String,
    val flagsShownJson: String,
    val flagsRemainderJson: String,
)