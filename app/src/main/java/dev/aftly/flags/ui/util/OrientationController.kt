package dev.aftly.flags.ui.util

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/* For controlling screen orientation (eg. for when entering fullscreen view) */
class OrientationController(val activity: ComponentActivity) {
    fun setLandscapeOrientation() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    fun unsetLandscapeOrientation() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

/* Instantiate OrientationController for global access */
val LocalOrientationController: ProvidableCompositionLocal<OrientationController> =
    compositionLocalOf {
        error(message = "OrientationController not provided")
    }

/* Get is portrait orientation */
fun isOrientationPortrait(configuration: Configuration): Boolean {
    return when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> true
        else -> false
    }
}

/* Get is landscape orientation */
fun isOrientationLandscape(configuration: Configuration): Boolean {
    return when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }
}