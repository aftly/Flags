package dev.aftly.flags.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.MainActivityViewModel
import dev.aftly.flags.ui.screen.settings.SettingsViewModel
import dev.aftly.flags.ui.theme.AppThemePreference

object AppViewModelProvider {
    var initThemePreference: AppThemePreference? = null
    var initIsDynamicColor: Boolean? = null

    val Factory = viewModelFactory {
        initializer {
            MainActivityViewModel(
                userPreferencesRepository = flagsApplication().container.userPreferencesRepository,
                initThemePreference = initThemePreference,
                initIsDynamicColor = initIsDynamicColor,
            )
        }

        initializer {
            SettingsViewModel(flagsApplication().container.userPreferencesRepository)
        }
    }
}

fun CreationExtras.flagsApplication(): FlagsApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlagsApplication)