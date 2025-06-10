package dev.aftly.flags.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.UserPreferencesRepository
import dev.aftly.flags.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        combine(
            userPreferencesRepository.isDynamicColor,
            userPreferencesRepository.theme
        ) { isDynamicColor, theme ->
            SettingsUiState(isDynamicColor, AppTheme.get(theme))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SettingsUiState()
        )

    fun saveDynamicColor(isDynamicColor: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveDynamicColor(isDynamicColor)
        }
    }

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.saveTheme(theme)
        }
    }

    fun systemBarsIsLight(isSystemInDarkTheme: Boolean) =
        when (uiState.value.theme) {
            AppTheme.LIGHT -> true
            AppTheme.DARK -> false
            AppTheme.BLACK -> false
            AppTheme.SYSTEM -> !isSystemInDarkTheme
            AppTheme.SYSTEM_BLACK -> !isSystemInDarkTheme
        }
}