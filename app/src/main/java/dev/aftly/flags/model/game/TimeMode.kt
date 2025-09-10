package dev.aftly.flags.model.game

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aftly.flags.R
import kotlinx.serialization.Serializable

@Serializable
enum class TimeMode(
    @param:StringRes val title: Int,
    val icon: ImageVector,
) {
    STANDARD (
        title = R.string.time_mode_standard,
        icon = Icons.Default.Timer,
    ),
    TIME_TRIAL (
        title = R.string.time_mode_time_trial,
        icon = Icons.Default.HourglassFull,
    )
}