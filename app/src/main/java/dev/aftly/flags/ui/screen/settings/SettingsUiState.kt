package dev.aftly.flags.ui.screen.settings

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val materialYou: Boolean = false,
)
