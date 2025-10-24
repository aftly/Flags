package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceLight
import dev.aftly.flags.ui.util.color
import dev.aftly.flags.ui.util.colorSelect
import dev.aftly.flags.ui.util.flagDatesString

/* ---------- SCRIM RELATED ---------- */
@Composable
fun MenuScrimAnimatedVisibility(
    visible: Boolean,
    content: @Composable (AnimatedVisibilityScope.() -> Unit),
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
    exit = fadeOut(animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE)),
    content = content,
)

@Composable
fun MenuScrim(
    visible: Boolean,
    onClick: () -> Unit,
) = MenuScrimAnimatedVisibility(visible = visible) {
    Scrim(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
        onClick = onClick,
    )
}
/* ----------------------------------- */


/* ------------- GENERIC MENU COMPONENTS -------------- */
@Composable
fun MenuAnimatedVisibility(
    visible: Boolean,
    content: @Composable (AnimatedVisibilityScope.() -> Unit),
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
            expandFrom = Alignment.Top,
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE),
            shrinkTowards = Alignment.Top,
        ),
        content = content,
    )
}

@Composable
fun MenuCard(
    modifier: Modifier = Modifier,
    menu: FlagsMenu,
    content: @Composable (ColumnScope.() -> Unit)
) = Card(
    modifier = modifier,
    shape = Shapes.large,
    colors = CardDefaults.cardColors(containerColor = menu.color()),
    content = content,
)

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    icon: ImageVector?,
    isSelected: Boolean,
    buttonColorsSelect: ButtonColors,
    buttonColorsUnselect: ButtonColors,
    onClick: () -> Unit,
) {
    val colors = if (isSelected) buttonColorsSelect else buttonColorsUnselect

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = MaterialTheme.shapes.medium,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }

        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(start = Dimens.extraSmall4),
        )
    }
}

@Composable
fun MenuItemCard(
    modifier: Modifier = Modifier,
    innerPadding: Dp = Dimens.extraSmall4,
    cardColors: CardColors,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.padding(horizontal = Dimens.small10),
        shape = Shapes.large,
        colors = cardColors,
    ) {
        Column(modifier = Modifier.padding(vertical = innerPadding)) {
            content()
        }
    }
}

@Composable
fun MenuItemExpandableArrowIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    isMenuButton: Boolean = false,
    iconSize: Dp,
    iconPadding: Dp,
    tint: Color = Color.Unspecified,
) {
    val endPadding = if (!isMenuButton) iconPadding else 0.dp

    Icon(
        imageVector =
            if (isExpanded) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown,
        contentDescription =
            if (isExpanded) stringResource(R.string.menu_sub_icon_collapse)
            else stringResource(R.string.menu_sub_icon_expand),
        modifier = modifier
            .padding(end = endPadding)
            .size(iconSize),
        tint = tint,
    )
}


/* ------------- CATEGORY MENU COMPONENTS -------------- */
@Composable
fun MenuCategoryItem(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.padding(vertical = Dimens.textButtonVertPad)
    ) {
        MenuCategoryItemContent(
            textButtonStyle = textButtonStyle,
            buttonColors = buttonColors,
            fontWeight = fontWeight,
            category = category,
            postTextContent = postTextContent,
        )
    }
}

@Composable
fun MenuCategoryItemCentred(
    modifier: Modifier = Modifier,
    isMenuButton: Boolean = false,
    buttonTitle: String? = null,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    lastItemPadding: Dp = 0.dp,
    iconPaddingButton: Dp = 0.dp,
    iconSizePadding: Dp,
    iconsTotalSize: Dp,
    onSingleLineCount: (Boolean) -> Unit = {},
    preTextContent: @Composable (RowScope.() -> Unit)? = null,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    val boxModifier =
        if (isMenuButton) modifier.fillMaxSize()
        else modifier
            .fillMaxWidth()
            .padding(
                top = Dimens.textButtonVertPad,
                bottom = Dimens.textButtonVertPad + lastItemPadding
            )

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(modifier = boxModifier) {
        /* Manage dynamic spacer size for button title center alignment */
        val buttonWidth = maxWidth /* (BoxWithConstraintsScope usage) */
        var textWidth by remember { mutableStateOf(value = 0.dp) }

        MenuCategoryItemContent(
            isMenuButton = isMenuButton,
            buttonTitle = buttonTitle,
            textButtonStyle = textButtonStyle,
            buttonColors = buttonColors,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            iconPaddingButton = iconPaddingButton,
            category = category,
            onTextWidth = { textWidth = it },
            onSingleLineCount = onSingleLineCount,
            preTextContent = {
                preTextContent?.let {
                    preTextContent()
                } ?: Spacer(
                    modifier = Modifier.width(width =
                        if (textWidth + iconsTotalSize < buttonWidth) iconSizePadding
                        else 0.dp
                    )
                )
            },
            postTextContent = postTextContent,
        )
    }
}

@Composable
fun MenuCategoryItemContent(
    modifier: Modifier = Modifier,
    isMenuButton: Boolean = false,
    buttonTitle: String? = null,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    iconPaddingButton: Dp = 0.dp,
    category: FlagCategoryBase?,
    onTextWidth: (Dp) -> Unit = {},
    onSingleLineCount: (Boolean) -> Unit = {},
    preTextContent: (@Composable RowScope.() -> Unit)? = null,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    val density = LocalDensity.current
    val rowModifier = if (isMenuButton) modifier.fillMaxSize() else modifier.fillMaxWidth()
    val textPaddingModifier =
        if (isMenuButton) Modifier.padding(horizontal = iconPaddingButton)
        else Modifier.padding(paddingValues = ButtonDefaults.TextButtonContentPadding)

    val rowBackgroundColor = if (isMenuButton) Color.Transparent else buttonColors.containerColor
    val textColor = if (isMenuButton) LocalContentColor.current else buttonColors.contentColor

    Row(
        modifier = rowModifier.background(color = rowBackgroundColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        preTextContent?.let { preTextContent() }

        Text(
            text = buttonTitle ?: when (category) {
                is FlagSuperCategory -> stringResource(category.title)
                is FlagCategoryWrapper -> stringResource(category.enum.title)
                else -> stringResource(R.string.saved_flags)
            },
            modifier = when (preTextContent) {
                null -> textPaddingModifier
                else -> textPaddingModifier.weight(weight = 1f, fill = false)
            },
            color = textColor,
            fontWeight = fontWeight,
            textAlign = textAlign,
            onTextLayout = when (preTextContent) {
                null -> null
                else -> { textLayoutResult ->
                    with(receiver = density) { onTextWidth(textLayoutResult.size.width.toDp()) }

                    if (isMenuButton && textLayoutResult.lineCount > 1) {
                        onSingleLineCount(false)
                    } else if (isMenuButton) {
                        onSingleLineCount(true)
                    }
                }
            },
            style = textButtonStyle,
        )

        postTextContent()
    }
}
/* ------------------------------------- */


/* ------------- FLAG MENU COMPONENTS -------------- */
@Composable
fun MenuFlagItem(
    modifier: Modifier = Modifier,
    flag: FlagView,
    selectedFlag: FlagView?,
    menu: FlagsMenu,
    isShowDates: Boolean,
    onFlagSelect: (FlagView) -> Unit,
) {
    val isSelectedLiteral = flag == selectedFlag
    val isSelectedPolitical = menu == FlagsMenu.POLITICAL &&
            inverseFlagViewMap.getValue(flag) == selectedFlag?.previousFlagOfKey
    val isSelected = isSelectedLiteral || isSelectedPolitical


    val fontScale = LocalConfiguration.current.fontScale
    val verticalPadding = Dimens.small8
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale
    val imageHeight = dynamicHeight - verticalPadding * 2

    val color = if (isSelected) menu.colorSelect() else menu.color()

    val fromToYear =
        if (flag.isDated && isShowDates) {
            buildString {
                append(stringResource(id = R.string.string_whitespace))
                append(flagDatesString(flag))
            }
        } else null

    TextButton(
        onClick = { onFlagSelect(flag) },
        modifier = modifier,
        shape = RoundedCornerShape(size = 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(weight = 1f)) {
                Row(
                    modifier = Modifier
                        /* Separate text from image */
                        .padding(end = Dimens.small8)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(text = stringResource(id = flag.flagOf))

                    fromToYear?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    modifier = Modifier.height(imageHeight),
                    contentScale = ContentScale.Fit,
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier.matchParentSize()
                            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(size = Dimens.standardIconSize24 * fontScale),
                        tint = surfaceLight,
                    )
                }
            }
        }
    }
}
/* ------------------------------------- */