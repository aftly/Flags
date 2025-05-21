package dev.aftly.flags.ui.util

import android.content.pm.ActivityInfo
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