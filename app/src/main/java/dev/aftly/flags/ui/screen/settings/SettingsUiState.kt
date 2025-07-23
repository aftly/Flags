package dev.aftly.flags.ui.screen.settings

import dev.aftly.flags.ui.theme.AppThemePreference

data class SettingsUiState(
    val theme: AppThemePreference = AppThemePreference.SYSTEM,
    val isDynamicColor: Boolean = false,
)
