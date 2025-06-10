package dev.aftly.flags.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.MainActivityViewModel
import dev.aftly.flags.ui.screen.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainActivityViewModel(flagsApplication().container.userPreferencesRepository)
        }

        initializer {
            SettingsViewModel(flagsApplication().container.userPreferencesRepository)
        }
    }
}

fun CreationExtras.flagsApplication(): FlagsApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlagsApplication)