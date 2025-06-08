package dev.aftly.flags.ui.screen.settings

import dev.aftly.flags.model.AppTheme

data class SettingsUiState(
    val isDynamicColor: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
)
