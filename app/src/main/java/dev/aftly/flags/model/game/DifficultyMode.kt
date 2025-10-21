package dev.aftly.flags.model.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aftly.flags.R
import kotlinx.serialization.Serializable

@Serializable
enum class DifficultyMode(
    val title: Int,
    val guessLimit: Int?,
    val icon: ImageVector?,
) {
    EASY ( /* Unlimited guesses */
        title = R.string.game_mode_difficulty_easy_title,
        guessLimit = null,
        icon = Icons.Default.AllInclusive,
    ),
    MEDIUM ( /* 3 guesses */
        title = R.string.game_mode_difficulty_medium_title,
        guessLimit = 3,
        icon = null,
    ),
    HARD ( /* 1 guess */
        title = R.string.game_mode_difficulty_hard_title,
        guessLimit = 1,
        icon = null,
    ),
    SUDDEN_DEATH ( /* Incorrect guess ends the game */
        title = R.string.game_mode_difficulty_sudden_death_title,
        guessLimit = 0,
        icon = Icons.Default.ElectricBolt,
    )
}