package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R

enum class GameMode(
    @param:StringRes val title: Int
) {
    NAMES (title = R.string.game_mode_names_title),
    DATES (title = R.string.game_mode_dates_title)
}