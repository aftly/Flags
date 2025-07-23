package dev.aftly.flags

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.navigation.AppNavHost
import dev.aftly.flags.ui.AppViewModelProvider
import dev.aftly.flags.ui.theme.AppTheme
import dev.aftly.flags.ui.util.LocalOrientationController
import dev.aftly.flags.ui.util.OrientationController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val userPrefs = (application as FlagsApplication).container.userPreferencesRepository
        AppViewModelProvider.initThemePreference = runBlocking { userPrefs.theme.first() }
        AppViewModelProvider.initIsDynamicColor = runBlocking { userPrefs.isDynamicColor.first() }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val orientationController = OrientationController(activity = this)

            CompositionLocalProvider(
                value = LocalOrientationController provides orientationController
            ) {
                AppTheme(
                    darkTheme = viewModel.isDarkTheme(isSystemInDarkTheme()),
                    dynamicColor = uiState.isDynamicColor,
                    blackTheme = viewModel.isBlackTheme(),
                    initSystemBarsIsLight = viewModel.initSystemBarsIsLight(isSystemInDarkTheme()),
                ) {
                    AppNavHost()
                }
            }
        }
    }
}


// Preview screen in Android Studio
/*
@Preview (
    showBackground = true,
    showSystemUi = true)
@Composable
fun FlagsAppPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        AppNavHost()
    }
}
 */