package dev.aftly.flags.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.datastore.UserPreferencesRepository
import dev.aftly.flags.ui.theme.AppThemePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val uiState =
        combine(
            flow = userPreferencesRepository.theme,
            flow2 = userPreferencesRepository.isDynamicColor,
        ) { theme, isDynamicColor ->
            SettingsUiState(AppThemePreference.get(theme.name), isDynamicColor)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SettingsUiState()
        )

    fun saveTheme(theme: AppThemePreference) {
        viewModelScope.launch {
            userPreferencesRepository.saveTheme(theme)
        }
    }

    fun saveDynamicColor(isDynamicColor: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveDynamicColor(isDynamicColor)
        }
    }

    fun systemBarsIsLight(isSystemInDarkTheme: Boolean) =
        when (uiState.value.theme) {
            AppThemePreference.LIGHT -> true
            AppThemePreference.DARK -> false
            AppThemePreference.BLACK -> false
            AppThemePreference.SYSTEM -> !isSystemInDarkTheme
            AppThemePreference.SYSTEM_BLACK -> !isSystemInDarkTheme
        }
}