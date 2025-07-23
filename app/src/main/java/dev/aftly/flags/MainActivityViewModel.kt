package dev.aftly.flags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.datastore.UserPreferencesRepository
import dev.aftly.flags.ui.theme.AppThemePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


class MainActivityViewModel(
    userPreferencesRepository: UserPreferencesRepository,
    initThemePreference: AppThemePreference?,
    initIsDynamicColor: Boolean?,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> =
        combine(
            flow = userPreferencesRepository.theme,
            flow2 = userPreferencesRepository.isDynamicColor,
        ) { theme, isDynamicColor ->
            MainActivityUiState(theme, isDynamicColor)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = MainActivityUiState(
                theme = initThemePreference ?: AppThemePreference.SYSTEM,
                isDynamicColor = initIsDynamicColor ?: false,
            ),
        )


    fun isDarkTheme(isSystemInDarkTheme: Boolean) = when (uiState.value.theme) {
        AppThemePreference.DARK -> true
        AppThemePreference.BLACK -> true
        AppThemePreference.LIGHT -> false
        else -> isSystemInDarkTheme
    }

    fun isBlackTheme() = when (uiState.value.theme) {
        AppThemePreference.BLACK -> true
        AppThemePreference.SYSTEM_BLACK -> true
        else -> false
    }

    fun initSystemBarsIsLight(isSystemInDarkTheme: Boolean) =
        when (uiState.value.theme to isSystemInDarkTheme) {
            AppThemePreference.LIGHT to true -> true
            AppThemePreference.DARK to false -> false
            AppThemePreference.BLACK to false -> false
            else -> null
        }
}