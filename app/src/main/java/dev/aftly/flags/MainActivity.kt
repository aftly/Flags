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
import dev.aftly.flags.ui.theme.AppTheme
import dev.aftly.flags.ui.util.LocalOrientationController
import dev.aftly.flags.ui.util.OrientationController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainActivityViewModel = viewModel(
                factory = MainActivityViewModel.Factory
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val orientationController = OrientationController(activity = this)

            CompositionLocalProvider(
                value = LocalOrientationController provides orientationController
            ) {
                AppTheme(
                    darkTheme = viewModel.isDarkTheme(isSystemInDarkTheme()),
                    dynamicColor = uiState.isDynamicColor,
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