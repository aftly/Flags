package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R

enum class AppTheme(@StringRes val title: Int) {
    SYSTEM (title = R.string.settings_theme_system),
    LIGHT (title = R.string.settings_theme_light),
    DARK (title = R.string.settings_theme_dark),
    BLACK (title = R.string.settings_theme_black);

    companion object {
        fun get(name: String?): AppTheme =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}