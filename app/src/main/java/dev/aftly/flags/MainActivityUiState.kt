package dev.aftly.flags

import dev.aftly.flags.model.AppTheme

data class MainActivityUiState(
    val isDynamicColor: Boolean = false,
    val theme: String = AppTheme.SYSTEM.name,
)
