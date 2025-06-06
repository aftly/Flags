package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun DialogActionButton(
    onClick: () -> Unit,
    @StringRes buttonStringResId: Int,
) {
    val buttonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    val buttonTextColor = MaterialTheme.colorScheme.primary

    TextButton(
        onClick = onClick,
        colors = buttonColors
    ) {
        Text(
            text = stringResource(buttonStringResId),
            color = buttonTextColor,
        )
    }
}