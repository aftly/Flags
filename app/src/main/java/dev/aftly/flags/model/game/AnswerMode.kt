package dev.aftly.flags.model.game

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aftly.flags.R
import kotlinx.serialization.Serializable

@Serializable
enum class AnswerMode(
    @param:StringRes val title: Int,
    val icon: ImageVector,
    @param:StringRes val description: Int,
    @param:StringRes val description2: Int?,
) {
    NAMES (
        title = R.string.game_mode_answer_names_title,
        icon = Icons.Default.TextFormat,
        description = R.string.game_mode_answer_names_description,
        description2 = null,
    ),
    DATES (
        title = R.string.game_mode_answer_dates_title,
        icon = Icons.Default.DateRange,
        description = R.string.game_mode_answer_dates_description,
        description2 = R.string.game_mode_answer_dates_description_2,
    )
}