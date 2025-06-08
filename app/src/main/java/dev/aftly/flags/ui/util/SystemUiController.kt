package dev.aftly.flags.ui.util

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class SystemUiController(
    view: View,
    private val window: Window,
) {
    private val compatController = WindowCompat.getInsetsController(window, view)
    val isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun setSystemBars(visible: Boolean) {
        when (visible) {
            true ->
                if (isApi30) window.insetsController?.show(WindowInsets.Type.systemBars())
                else compatController.show(WindowInsetsCompat.Type.systemBars())

            false ->
                if (isApi30) window.insetsController?.hide(WindowInsets.Type.systemBars())
                else compatController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun setLightStatusBar(light: Boolean) {
        if (isApi30) {
            val appearance = if (light) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0

            window.insetsController?.setSystemBarsAppearance(
                appearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            compatController.isAppearanceLightStatusBars = light
        }
    }

    fun setSystemBarsSwipeBehaviour() {
        if (isApi30) {
            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}