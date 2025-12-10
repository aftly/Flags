package dev.aftly.flags.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.color


@Composable
fun RelatedFlagsButton(
    modifier: Modifier = Modifier,
    menu: FlagsMenu,
    isFullSize: Boolean,
    menuExpanded: Boolean,
    onMenuExpand: () -> Unit,
    onButtonPosition: (Offset) -> Unit = {},
    onButtonWidth: (Int) -> Unit = {},
    onButtonExtraHeight: (Dp) -> Unit = {},
) {
    val density = LocalDensity.current
    val buttonColors = ButtonDefaults.buttonColors(containerColor = menu.color())
    val iconSize = Dimens.iconSize24
    val iconPadding = 2.dp
    val titleStringRes = if (isFullSize) menu.title else menu.titleShort
    var lineCount by remember { mutableIntStateOf(value = 1) }
    val buttonHeightOneLine = with(receiver = density) { Dimens.filterButtonHeight30.toDp() }
    val buttonHeight = when (lineCount) {
        1 -> buttonHeightOneLine
        else -> Dp.Unspecified
    }
    val buttonHeightUnspecified by remember { mutableStateOf(value = buttonHeight) }

    Box(
        modifier = modifier
            .onGloballyPositioned { layout ->
                onButtonPosition(layout.positionOnScreen())
                onButtonWidth(layout.size.width)
                if (lineCount > 1) {
                    val height = with(receiver = density) { layout.size.height.toDp() }
                    val extraHeight = height - buttonHeightOneLine
                    onButtonExtraHeight(extraHeight)
                }
            }
    ) {
        Button(
            onClick = onMenuExpand,
            modifier = Modifier.height(buttonHeight),
            shape = MaterialTheme.shapes.large,
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = Dimens.medium16),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = menu.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(horizontal = iconPadding),
                    tint = buttonColors.contentColor,
                )

                Text(
                    text = stringResource(id = titleStringRes),
                    modifier = Modifier.weight(1f, fill = false),
                    textAlign = TextAlign.Center,
                    onTextLayout = { lineCount = it.lineCount },
                    style = MaterialTheme.typography.titleMedium.let { style ->
                        style.copy(lineHeight = style.fontSize * 1.2f)
                    },
                )

                if (!menuExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.menu_icon_expand),
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = iconPadding),
                        tint = buttonColors.contentColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.menu_icon_collapse),
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = iconPadding),
                        tint = buttonColors.contentColor,
                    )
                }
            }
        }
    }
}