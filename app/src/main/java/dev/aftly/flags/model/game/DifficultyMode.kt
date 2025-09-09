package dev.aftly.flags.model.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class DifficultyMode(
    val guessLimit: Int?,
    val icon: ImageVector?,
) {
    EASY (guessLimit = null, icon = Icons.Default.AllInclusive), /* Unlimited guesses */
    MEDIUM (guessLimit = 3, icon = null), /* 3 guesses */
    HARD (guessLimit = 1, icon = null), /* 1 guess */
    SUDDEN_DEATH (guessLimit = 0, icon = Icons.Default.ElectricBolt) /* Incorrect guess ends the game */
}