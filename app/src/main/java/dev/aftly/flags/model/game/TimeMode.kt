package dev.aftly.flags.model.game

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Serializable

@Serializable
enum class TimeMode(@param:StringRes val title: Int) {
    STANDARD (title = R.string.time_mode_standard),
    TIME_TRIAL (title = R.string.time_mode_time_trial)
}