package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.theme.surfaceLight
import kotlinx.coroutines.delay

@Composable
fun FullscreenButton(
    visible: Boolean,
    onInvisible: () -> Unit,
    onFullScreen: () -> Unit,
) {
    LaunchedEffect(visible) {
        /* Disable fullscreen button automatically when not clicked */
        if (visible) {
            delay(timeMillis = Timings.SYSTEM_BARS_HANG.toLong() * 4)
            onInvisible()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = Timings.FULLSCREEN_BUTTON)),
        exit = fadeOut(animationSpec = tween(durationMillis = Timings.FULLSCREEN_BUTTON)),
    ) {
        IconButton(
            onClick = onFullScreen,
            modifier = Modifier.padding(Dimens.small8),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = null,
                tint = surfaceLight,
            )
        }
    }
}