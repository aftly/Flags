package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timings


@Composable
fun RelatedFlagsButton(
    modifier: Modifier = Modifier,
    buttonExpanded: Boolean,
    onButtonExpand: () -> Unit,
    onButtonHeightChange: (Dp) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    cardColors: CardColors = CardDefaults.cardColors(containerColor = containerColor),
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor),
    fontScale: Float,
    currentFlag: FlagResources,
    relatedFlags: List<FlagResources>,
    onFlagSelect: (FlagResources) -> Unit,
) {
    /* Manage button height */
    val oneLineButtonHeight = Dimens.defaultFilterButtonHeight30 * fontScale
    val twoLineButtonHeight = Dimens.defaultFilterButtonHeight50 * fontScale
    var buttonHeight by remember {
        mutableStateOf(value = Dimens.defaultFilterButtonHeight30 * fontScale)
    }
    LaunchedEffect(buttonHeight) {
        when (buttonHeight) {
            oneLineButtonHeight -> onButtonHeightChange(oneLineButtonHeight)
            twoLineButtonHeight -> onButtonHeightChange(twoLineButtonHeight)
        }
    }
    val density = LocalDensity.current


    /* Filter button content */
    Column(modifier = modifier) {
        Button(
            onClick = { onButtonExpand() },
            modifier = Modifier.padding(bottom = Dimens.small8) /* Separate Button from Menu */
                .height(buttonHeight),
            shape = MaterialTheme.shapes.large,
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = Dimens.medium16),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                /* Manage dynamic spacer size for button title center alignment */
                val buttonWidth = maxWidth
                var textWidth by remember { mutableStateOf(value = 0.dp) }

                val iconSize = Dimens.standardIconSize24 * fontScale
                val iconPadding = 2.dp * fontScale
                val iconSizePadding = iconSize + iconPadding
                val iconsTotalSize = (iconSize + iconPadding + 1.dp) * 2


                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(
                        modifier = Modifier.width(width =
                            if (textWidth + iconsTotalSize < buttonWidth) {
                                iconSizePadding
                            } else 0.dp
                        )
                    )

                    Text(
                        text = stringResource(R.string.menu_related_flags),
                        modifier = Modifier.weight(weight = 1f, fill = false),
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
                            with(density) { textWidth = textLayoutResult.size.width.toDp() }

                            if (textLayoutResult.lineCount > 1) {
                                buttonHeight = twoLineButtonHeight
                            } else if (buttonHeight != oneLineButtonHeight) {
                                buttonHeight = oneLineButtonHeight
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )

                    if (!buttonExpanded) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.menu_icon_expand),
                            modifier = Modifier.size(iconSize)
                                .padding(start = iconPadding),
                            tint = buttonColors.contentColor,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.menu_icon_collapse),
                            modifier = Modifier.size(iconSize)
                                .padding(start = iconPadding),
                            tint = buttonColors.contentColor,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = buttonExpanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = Timings.MENU_COLLAPSE),
                shrinkTowards = Alignment.Top,
            ),
        ) {
            Card(
                shape = Shapes.large,
                colors = cardColors,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        top = Dimens.small8,
                        bottom = Dimens.small10,
                    ),
                ) {
                    items(
                        count = relatedFlags.size,
                        key = { index -> relatedFlags[index].id }
                    ) { index ->
                        ListItem(
                            modifier = Modifier.fillMaxWidth(),
                            flag = relatedFlags[index],
                            currentFlag = currentFlag,
                            buttonColors = buttonColors,
                            fontScale = fontScale,
                            onFlagSelect = onFlagSelect,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    flag: FlagResources,
    currentFlag: FlagResources,
    buttonColors: ButtonColors,
    fontScale: Float,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val verticalPadding = Dimens.small8
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale

    TextButton(
        onClick = { onFlagSelect(flag) },
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = buttonColors,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(flag.flagOf),
                    modifier = Modifier.padding(end = Dimens.small8)
                )
            }
            if (flag != currentFlag) {
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    modifier = Modifier.height(dynamicHeight - verticalPadding * 2),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.standardIconSize24 * fontScale)
                )
            }
        }
    }
}