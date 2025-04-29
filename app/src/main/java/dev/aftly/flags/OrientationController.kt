package dev.aftly.flags

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/* Custom controller for managing orientation (such as for enter fullscreen view functionality) */
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