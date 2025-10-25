package dev.aftly.flags.ui.util

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun textButtonEndPadding(layDir: LayoutDirection): Dp =
    ButtonDefaults.TextButtonContentPadding.calculateEndPadding(layoutDirection = layDir)