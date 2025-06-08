package dev.aftly.flags.ui.util

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/* Instantiate OrientationController for global access */
val LocalDarkTheme: ProvidableCompositionLocal<Boolean> =
    compositionLocalOf {
        error(message = "Dark theme not provided")
    }