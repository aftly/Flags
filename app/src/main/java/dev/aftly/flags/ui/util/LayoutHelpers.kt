package dev.aftly.flags.ui.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun textButtonEndPadding(layDir: LayoutDirection): Dp =
    ButtonDefaults.TextButtonContentPadding.calculateEndPadding(layoutDirection = layDir)

@Composable
fun getCardColors(containerColor: Color): CardColors =
    CardDefaults.cardColors(containerColor = containerColor)

@Composable
fun getButtonColors(containerColor: Color): ButtonColors =
    ButtonDefaults.buttonColors(containerColor = containerColor)

@Composable
fun buttonCardColors(containerColor: Color): CardColors = getButtonColors(containerColor).let {
    buttonColors ->
    CardDefaults.cardColors(
        containerColor = buttonColors.containerColor,
        contentColor = buttonColors.contentColor,
    )
}

fun Modifier.clickableNoHaptics(
    shape: Shape,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier = this
    .clip(shape = shape)
    .indication(interactionSource, indication = ripple())
    .pointerInput(key1 = Unit) {
        detectTapGestures(
            onPress = { offset ->
                val press = PressInteraction.Press(pressPosition = offset)
                interactionSource.emit(interaction = press)

                val released = tryAwaitRelease()
                val end =
                    if (released) PressInteraction.Release(press = press)
                    else PressInteraction.Cancel(press = press)
                interactionSource.emit(interaction = end)
            },
            onTap = { onClick() },
            onLongPress = { onLongClick() },
        )
    }