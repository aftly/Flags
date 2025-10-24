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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
) {
    val buttonColors = ButtonDefaults.buttonColors(containerColor = menu.color())
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val iconSize = Dimens.standardIconSize24 * fontScale
    val iconPadding = 2.dp * fontScale
    val titleStringRes = if (isFullSize) menu.title else menu.titleShort

    Box(
        modifier = modifier
            .onGloballyPositioned { layout ->
                onButtonPosition(layout.positionOnScreen())
                onButtonWidth(layout.size.width)
            }
    ) {
        Button(
            onClick = onMenuExpand,
            modifier = Modifier.height(Dimens.defaultFilterButtonHeight30 * fontScale),
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
                        .padding(start = iconPadding),
                    tint = buttonColors.contentColor,
                )

                Text(
                    text = stringResource(titleStringRes),
                    modifier = Modifier.weight(1f, fill = false),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
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