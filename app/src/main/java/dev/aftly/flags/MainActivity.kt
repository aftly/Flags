package dev.aftly.flags

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import dev.aftly.flags.navigation.AppNavHost
import dev.aftly.flags.ui.theme.FlagsTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val orientationController = OrientationController(activity = this)

            CompositionLocalProvider(
                value = LocalOrientationController provides orientationController
            ) {
                FlagsTheme {
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