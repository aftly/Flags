package dev.aftly.flags.ui.util

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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