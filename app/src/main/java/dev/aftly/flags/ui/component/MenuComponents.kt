package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.color
import dev.aftly.flags.ui.util.colorSelect
import dev.aftly.flags.ui.util.flagDatesString
import dev.aftly.flags.ui.util.getButtonColors
import dev.aftly.flags.ui.util.getCardColors
import dev.aftly.flags.ui.util.textButtonEndPadding

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
        modifier = Modifier
            .fillMaxSize()
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
    nestLevel: Int = 0,
    shape: Shape? = null,
    content: @Composable (ColumnScope.() -> Unit)
) = Card(
    modifier = modifier,
    shape = shape ?: Shapes.large,
    colors = getCardColors(containerColor =
        if (nestLevel % 2 == 0) menu.color() else menu.colorSelect()
    ),
    content = content,
)

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    menu: FlagsMenu,
    isSelected: Boolean,
    isContentSelected: Boolean = false,
    @StringRes title: Int,
    icon: ImageVector?,
    onClick: () -> Unit,
) {
    val colors = getButtonColors(containerColor =
        if (isSelected) menu.colorSelect() else menu.color()
    )

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

        if (isContentSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(start = Dimens.extraSmall6)
                    .size(Dimens.iconSize24 * 0.7f)
                    .clip(shape = MaterialTheme.shapes.large)
                    .background(color = colors.contentColor)
                    .padding(all = 1.dp),
                tint = colors.containerColor,
            )
        }
    }
}

@Composable
fun MenuItemExpandableArrowIcon(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isExpanded: Boolean,
    isMenuButton: Boolean = false,
) {
    val endPadding = if (isMenuButton) 0.dp else {
        textButtonEndPadding(layDir = LocalLayoutDirection.current)
    }

    Icon(
        imageVector =
            if (isExpanded) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown,
        contentDescription =
            if (isExpanded) stringResource(R.string.menu_sub_icon_collapse)
            else stringResource(R.string.menu_sub_icon_expand),
        modifier = modifier.padding(end = endPadding),
        tint = getNestedColors(nestLevel).contentColor,
    )
}

/* ------------- CATEGORY MENU COMPONENTS -------------- */
@Composable
fun MenuCategoryItemCard(
    modifier: Modifier = Modifier,
    menu: FlagsMenu = FlagsMenu.FILTER,
    nestLevel: Int,
    isNested: Boolean = true,
    shape: Shape? = null,
    innerPadding: Dp = Dimens.extraSmall4,
    content: @Composable () -> Unit,
) {
    val horizontalPadding = if (isNested) Dimens.small10 else 0.dp

    MenuCard(
        modifier = modifier.padding(horizontal = horizontalPadding),
        menu = menu,
        nestLevel = nestLevel,
        shape = shape,
    ) {
        Column(modifier = Modifier.padding(vertical = innerPadding)) {
            content()
        }
    }
}

@Composable
fun MenuCategoryItem(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    isChildSelected: Boolean = false,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.padding(vertical = Dimens.menuItemVertPad)
    ) {
        MenuCategoryItemContent(
            modifier = Modifier.combinedClickable(
                enabled = isEnabled,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
            nestLevel = nestLevel,
            isEnabled = isEnabled,
            isSelected = isSelected,
            isChildSelected = isChildSelected,
            fontWeight = fontWeight,
            category = category,
            postTextContent = postTextContent,
        )
    }
}

@Composable
fun MenuCategoryItemCentred(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isSelected: Boolean = false,
    isChildSelected: Boolean = false,
    isMenuButton: Boolean = false,
    buttonTitle: String? = null,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    onSingleLineCount: (Boolean) -> Unit = {},
    onClick: () -> Unit,
    preTextContent: @Composable (RowScope.() -> Unit)? = null,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    val boxModifier = if (isMenuButton) modifier.fillMaxSize() else modifier
        .fillMaxWidth()
        .padding(vertical = Dimens.menuItemVertPad)

    val buttonModifier = if (isMenuButton) Modifier else Modifier.clickable(onClick = onClick)

    val iconPadding = if (isMenuButton) 2.dp else {
        textButtonEndPadding(layDir = LocalLayoutDirection.current)
    }
    val iconSizePadding = Dimens.iconSize24 + iconPadding
    val iconSizePaddingDuo = (iconSizePadding + 1.dp) * 2

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(modifier = boxModifier) {
        /* Manage dynamic spacer size for button title center alignment */
        val buttonWidth = maxWidth /* (BoxWithConstraintsScope usage) */
        var textWidth by remember { mutableStateOf(value = 0.dp) }

        MenuCategoryItemContent(
            modifier = buttonModifier,
            nestLevel = nestLevel,
            isSelected = isSelected,
            isChildSelected = isChildSelected,
            isMenuButton = isMenuButton,
            buttonTitle = buttonTitle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            category = category,
            onTextWidth = { textWidth = it },
            onSingleLineCount = onSingleLineCount,
            preTextContent = {
                preTextContent?.let {
                    preTextContent()
                } ?: Spacer(
                    modifier = Modifier.width(width =
                        if (textWidth + iconSizePaddingDuo < buttonWidth) iconSizePadding else 0.dp
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
    nestLevel: Int,
    isEnabled: Boolean = true,
    isSelected: Boolean,
    isChildSelected: Boolean,
    isMenuButton: Boolean = false,
    buttonTitle: String? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    category: FlagCategoryBase?,
    onTextWidth: (Dp) -> Unit = {},
    onSingleLineCount: (Boolean) -> Unit = {},
    preTextContent: (@Composable RowScope.() -> Unit)? = null,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    val buttonColors = getNestedColors(
        nestLevel = nestLevel,
        isSelected = isSelected,
        isChildSelected = isChildSelected,
    )

    val density = LocalDensity.current
    val rowModifier = if (isMenuButton) modifier.fillMaxSize() else modifier.fillMaxWidth()
    val textPaddingModifier =
        if (isMenuButton) Modifier.padding(horizontal = 2.dp)
        else Modifier.padding(paddingValues = ButtonDefaults.TextButtonContentPadding)

    val rowBackgroundColor = if (isMenuButton) Color.Transparent else buttonColors.containerColor
    val textColor = when {
        isMenuButton -> LocalContentColor.current
        isEnabled -> buttonColors.contentColor
        else -> lerp(
            start = buttonColors.contentColor,
            stop = buttonColors.containerColor,
            fraction = 0.5f,
        )
    }

    val textButtonStyle =
        if (isMenuButton) MaterialTheme.typography.titleMedium
        else MaterialTheme.typography.bodyMedium
    val textButtonFontWeight = fontWeight ?: FontWeight.Medium /* When null imitate TextButton() */

    Row(
        modifier = rowModifier.background(color = rowBackgroundColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        preTextContent?.let { preTextContent() }

        Text(
            text = buttonTitle ?: when (category) {
                is FlagSuperCategory -> stringResource(id = category.title)
                is FlagCategoryWrapper -> stringResource(id = category.enum.title)
                else -> stringResource(R.string.saved_flags)
            },
            modifier = when (preTextContent) {
                null -> textPaddingModifier
                else -> textPaddingModifier.weight(weight = 1f, fill = false)
            },
            color = textColor,
            fontWeight = textButtonFontWeight,
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

@Composable
fun getNestedColors(
    nestLevel: Int,
    menu: FlagsMenu = FlagsMenu.FILTER,
    isSelected: Boolean = false,
    isChildSelected: Boolean = false,
): ButtonColors {
    val isDarkTheme = LocalDarkTheme.current
    val baseColor1 = menu.color()
    val baseColor2 = menu.colorSelect()

    val color = if (nestLevel % 2 == 0) baseColor1 else baseColor2

    val colorSelect = lerp(
        start = if (nestLevel == 0 || nestLevel % 2 == 1) baseColor2 else baseColor1,
        stop = when {
            nestLevel == 0 -> baseColor2
            isDarkTheme -> Color.Black
            else -> Color.White
        },
        fraction = if (isDarkTheme) 0.13f else 0.175f,
    )

    val colorChildSelect = lerp(
        start = color,
        stop = if (isDarkTheme) Color.White else Color.Black,
        /* Variant only necessary for secondary color because of unique contrast */
        fraction = when (nestLevel % 2) {
            1 -> if (isDarkTheme) 0.275f else 0.12f
            else -> if (isDarkTheme) 0.25f else 0.165f
        },
    )

    return getButtonColors(
        containerColor = when {
            isSelected -> colorSelect
            isChildSelected -> colorChildSelect
            else -> color
        }
    )
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

    val verticalPadding = Dimens.small8
    val imageHeight = Dimens.listItemHeight48 - verticalPadding * 2

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
                        modifier = Modifier
                            .matchParentSize()
                            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(size = Dimens.iconSize24),
                        tint = surfaceLight,
                    )
                }
            }
        }
    }
}
/* ------------------------------------- */