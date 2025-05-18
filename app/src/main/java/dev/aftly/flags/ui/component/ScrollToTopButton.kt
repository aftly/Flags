package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.ui.theme.Timing

@Composable
fun ScrollToTopButton(
    isVisible: Boolean,
    containerColor: Color,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = tween(durationMillis = Timing.SCROLL_BUTTON)),
        exit = scaleOut(animationSpec = tween(durationMillis = Timing.SCROLL_BUTTON)),
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = containerColor,
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowUp,
                contentDescription = null,
            )
        }
    }
}