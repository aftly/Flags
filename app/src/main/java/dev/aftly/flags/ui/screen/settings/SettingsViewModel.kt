package dev.aftly.flags.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.aftly.flags.FlagsApplication
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
        }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlagsApplication)
                SettingsViewModel(application.userPreferencesRepository)
            }
        }
    }
}