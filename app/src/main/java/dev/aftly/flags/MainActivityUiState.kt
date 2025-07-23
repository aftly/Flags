package dev.aftly.flags

import dev.aftly.flags.ui.theme.AppThemePreference

data class MainActivityUiState(
    val theme: AppThemePreference = AppThemePreference.SYSTEM,
    val isDynamicColor: Boolean = false,
)
