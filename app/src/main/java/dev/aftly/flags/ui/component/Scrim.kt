package dev.aftly.flags.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Scrim(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .pointerInput(key1 = onClick) {
                detectTapGestures { onClick() }
            }
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClick()
                    true
                } else {
                    false
                }
            }
    )
}