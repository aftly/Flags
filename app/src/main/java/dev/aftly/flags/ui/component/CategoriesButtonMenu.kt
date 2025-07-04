package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.NonAdministrative
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagSuperCategory.AutonomousRegion
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing


@Composable
fun CategoriesButtonMenu(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    buttonHorizontalPadding: Dp,
    screen: Screen,
    flagCount: Int = 0,
    onButtonHeightChange: (Dp) -> Unit,
    isMenuExpanded: Boolean,
    onMenuButtonClick: () -> Unit,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    cardColors1: CardColors = CardDefaults.cardColors(containerColor = containerColor1),
    cardColors2: CardColors = CardDefaults.cardColors(containerColor = containerColor2),
    buttonColors1: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor1),
    buttonColors2: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor2),
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    currentSuperCategories: List<FlagSuperCategory>?,
    currentSubCategories: List<FlagCategory>?,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    /* Determines the expanded menu(s) */
    var expandMenu by remember { mutableStateOf<FlagSuperCategory?>(value = null) }

    /* When menu collapse, return expandMenu state to current selected category (if ui differs) */
    LaunchedEffect(isMenuExpanded) {
        /* Collapse sub-menu if it's super is selected */
        if (!isMenuExpanded && currentCategoryTitle == currentSuperCategory.title) {
            expandMenu = null
        } else if (!isMenuExpanded && expandMenu != currentSuperCategory &&
            currentCategoryTitle != expandMenu?.title &&
            currentSuperCategory != Political) {
            /* Expand sub-menu of current super when differs from current expanded menu */
            expandMenu = currentSuperCategory

        } else if (!isMenuExpanded && currentSuperCategory == Political) {
            /* As Political contains supers, expandMenu cannot just be determined from
             * currentSuperCategory. Loop through each super to find the selected category and
             * set expandMenu state to the sub-super */
            for (superCategory in currentSuperCategory.subCategories) {
                superCategory as FlagSuperCategory
                if (superCategory.enums().any { it.title == currentCategoryTitle }) {
                    expandMenu = superCategory
                }
            }
        }
    }

    /* Manage button height */
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val oneLineButtonHeight = Dimens.defaultFilterButtonHeight30 * fontScale
    val twoLineButtonHeight = Dimens.defaultFilterButtonHeight50 * fontScale
    var isOneLine by remember { mutableStateOf(value = true) }
    var buttonHeight by remember {
        mutableStateOf(value = Dimens.defaultFilterButtonHeight30 * fontScale)
    }
    LaunchedEffect(buttonHeight) {
        when (buttonHeight) {
            oneLineButtonHeight -> onButtonHeightChange(oneLineButtonHeight)
            twoLineButtonHeight -> onButtonHeightChange(twoLineButtonHeight)
        }
    }

    /* Manage button title & exceptions */
    val buttonTitle = if (currentSuperCategories != null && currentSubCategories != null) {
        /*
        filterSuperCategories(
            superCategories = currentSuperCategories,
            subCategories = currentSubCategories,
        ).let { superCategoriesFiltered ->
            var title = ""
            val commaWhitespace = stringResource(R.string.string_comma_whitespace)
            superCategoriesFiltered.forEach { superCategory ->
                title += stringResource(superCategory.title) +
                        stringResource(R.string.string_comma_whitespace)
            }
            currentSubCategories.forEach { subCategory ->
                title += stringResource(subCategory.title) +
                        stringResource(R.string.string_comma_whitespace)
            }

            return@let when (title.endsWith(commaWhitespace)) {
                true -> title.dropLast(commaWhitespace.length)
                false -> title
            }
        }
         */
        stringResource(R.string.menu_multiple_selection)

    } else {
        when (currentSuperCategory) {
            SovereignCountry -> stringResource(R.string.category_super_sovereign_country_title)
            Political -> stringResource(currentCategoryTitle) +
                    stringResource(R.string.button_title_state_flags)
            International -> stringResource(R.string.category_international_organization_title) +
                    stringResource(R.string.button_title_flags)
            else -> stringResource(currentCategoryTitle) + stringResource(R.string.button_title_flags)
        }
    }

    val textButtonStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
    )

    var flagCountWidth by remember { mutableStateOf(Dimens.standardIconSize24) }
    val flagCountShape = when (isOneLine) {
        true -> MaterialTheme.shapes.large
        false -> MaterialTheme.shapes.medium
    }

    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current


    Box(modifier = modifier) {
        /* Scrim behind expanded menu, tap gestures close menu */
        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
        ) {
            Scrim(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                onAction = onMenuButtonClick,
            )
        }


        /* Filter button content */
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    bottom = scaffoldPadding.calculateBottomPadding(),
                    start = buttonHorizontalPadding,
                    end = buttonHorizontalPadding,
                ),
        ) {
            Button(
                onClick = onMenuButtonClick,
                modifier = Modifier.padding(bottom = Dimens.small8) /* Separate Button from Menu */
                    .height(buttonHeight),
                shape = MaterialTheme.shapes.large,
                colors = buttonColors2,
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
                        /* If not game screen show flag count indicator,
                         * else spacer (for text centering) */
                        if (screen != Screen.Game) {
                            Box(
                                modifier = Modifier.onSizeChanged { size ->
                                    flagCountWidth = with(density) { size.width.toDp() }
                                },
                            ) {
                                Text(
                                    text = "$flagCount",
                                    modifier = Modifier
                                        .requiredWidthIn(min = Dimens.standardIconSize24)
                                        .clip(flagCountShape)
                                        .background(buttonColors2.contentColor)
                                        .padding(
                                            vertical = 2.dp * fontScale,
                                            horizontal = 6.dp * fontScale
                                        ),
                                    textAlign = TextAlign.Center,
                                    color = buttonColors2.containerColor,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier.width(width =
                                    if (textWidth + iconsTotalSize < buttonWidth) {
                                        iconSizePadding
                                    } else 0.dp
                                )
                            )
                        }

                        /* Button title */
                        Text(
                            text = buttonTitle,
                            modifier = Modifier.weight(weight = 1f, fill = false)
                                .padding(horizontal = iconPadding),
                            textAlign = TextAlign.Center,
                            onTextLayout = { textLayoutResult ->
                                with(density) { textWidth = textLayoutResult.size.width.toDp() }

                                if (textLayoutResult.lineCount > 1) {
                                    buttonHeight = twoLineButtonHeight
                                    isOneLine = false
                                } else {
                                    buttonHeight = oneLineButtonHeight
                                    isOneLine = true
                                }
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )

                        /* Drop down icon, minimum size of standard icon size * font scale,
                         * otherwise width of flag counter, for button title centering */
                        Box(
                            modifier = Modifier.width(flagCountWidth),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            if (!isMenuExpanded) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = stringResource(R.string.menu_icon_expand),
                                    modifier = Modifier.size(iconSize),
                                    tint = buttonColors1.contentColor,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.menu_icon_collapse),
                                    modifier = Modifier.size(iconSize),
                                    tint = buttonColors1.contentColor,
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isMenuExpanded,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                    expandFrom = Alignment.Top,
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE),
                    shrinkTowards = Alignment.Top,
                ),
            ) {
                Card(
                    shape = Shapes.large,
                    colors = cardColors1,
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            top = Dimens.small8,
                            bottom = Dimens.small10,
                        ),
                    ) {
                        items(items = menuSuperCategoryList) { superCategory ->
                            val buttonColors =
                                if (currentSuperCategories == null &&
                                    currentSubCategories == null &&
                                    superCategory == currentSuperCategory) {
                                    buttonColors2
                                } else if (
                                    currentSuperCategories != null &&
                                    currentSubCategories != null &&
                                    (superCategory in currentSuperCategories || (superCategory
                                        .subCategories.size == 1 && superCategory
                                            .firstCategoryEnumOrNull() in currentSubCategories))) {
                                    buttonColors2
                                } else if (currentSuperCategories != null &&
                                    currentSuperCategories.isEmpty() && superCategory == All) {
                                    buttonColors2
                                } else {
                                    buttonColors1
                                }


                            if (superCategory.subCategories.size == 1 || superCategory == All) {
                                /* If superCategory has 1 sub category use 1 tier (static) menu item
                                 * (where superCategory is meant to represent a sub/FlagCategory) */
                                MenuItemStatic(
                                    haptics = haptics,
                                    textButtonStyle = textButtonStyle,
                                    buttonColors = buttonColors,
                                    superCategory = superCategory,
                                    onCategorySelect = { newSuperCategory ->
                                        expandMenu = null
                                        onCategorySelect(newSuperCategory, null)
                                        onMenuButtonClick()
                                    },
                                    onCategoryMultiSelect = { selectSuperCategory ->
                                        onCategoryMultiSelect(selectSuperCategory, null)
                                    },
                                )
                            } else if (superCategory.subCategories
                                .filterIsInstance<FlagCategoryWrapper>().isNotEmpty()) {
                                /* If superCategory has any FlagCategories (ie. sub-categories)
                                 * use 2 tier expandable menu */
                                MenuItemExpandable(
                                    haptics = haptics,
                                    textButtonStyle = textButtonStyle,
                                    buttonColors1 = buttonColors,
                                    buttonColors2 = buttonColors2,
                                    cardColors2 = cardColors2,
                                    isSuperCategorySelectable = true,
                                    currentCategoryTitle = currentCategoryTitle,
                                    superCategory = superCategory,
                                    subCategories = currentSubCategories,
                                    menuExpanded = superCategory == expandMenu,
                                    onMenuSelect = { expandMenu = it },
                                    onCategorySelect = { newSuperCategory, newSubCategory ->
                                        onCategorySelect(newSuperCategory,newSubCategory)
                                        onMenuButtonClick()
                                    },
                                    onCategoryMultiSelect = onCategoryMultiSelect,
                                )
                            } else {
                                /* If superCategory only contains superCategories use 3 tier menu */
                                MenuSuperItem(
                                    haptics = haptics,
                                    textButtonStyle = textButtonStyle,
                                    buttonColors1 = buttonColors1,
                                    buttonColors2 = buttonColors2,
                                    cardColors1 = cardColors1,
                                    cardColors2 = cardColors2,
                                    isChildSelectable = false,
                                    currentCategoryTitle = currentCategoryTitle,
                                    parentSuperCategory = superCategory,
                                    subCategories = currentSubCategories,
                                    expandMenu = expandMenu,
                                    onMenuSelect = { expandMenu = it },
                                    onCategorySelect = { newSuperCategory, newSubCategory ->
                                        onCategorySelect(newSuperCategory,newSubCategory)
                                        onMenuButtonClick()
                                    },
                                    onCategoryMultiSelect = onCategoryMultiSelect,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MenuItemStatic(
    modifier: Modifier = Modifier,
    haptics: HapticFeedback,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    superCategory: FlagSuperCategory,
    onCategorySelect: (FlagSuperCategory) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory) -> Unit,
) {
    val fontWeight = when (superCategory) {
        All -> FontWeight.ExtraBold
        else -> null
    }
    val lastItemPadding = when (superCategory) {
        Historical -> 5.dp
        else -> 0.dp
    }


    Box(
        modifier = modifier
            .padding(
                top = Dimens.textButtonVertPad,
                bottom = Dimens.textButtonVertPad + lastItemPadding
            )
            .combinedClickable(
                onClick = { onCategorySelect(superCategory) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCategoryMultiSelect(superCategory)
                },
            )
            /*
            .indication(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ),
             */
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(color = buttonColors.containerColor)
        ) {
            Text(
                text = stringResource(superCategory.title),
                modifier = Modifier.padding(ButtonDefaults.TextButtonContentPadding),
                color = buttonColors.contentColor,
                fontWeight = fontWeight,
                style = textButtonStyle,
            )
        }
    }
}


@Composable
private fun MenuItemExpandable(
    modifier: Modifier = Modifier,
    haptics: HapticFeedback,
    textButtonStyle: TextStyle,
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    cardColors2: CardColors,
    isSuperCategorySelectable: Boolean,
    @StringRes currentCategoryTitle: Int,
    superCategory: FlagSuperCategory,
    subCategories: List<FlagCategory>?, /* for if multiple sub-categories are selected */
    menuExpanded: Boolean,
    onMenuSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    /* Separated boolean for controlling submenu expansion when multi select (subCategories) */
    var isMultiSelected: Boolean? by remember { mutableStateOf(value = null) }

    /* on subCategories change, if any subcategories in the menus super, expand the menu, else
     * null so that regular menu expansion property (menuExpanded) applies */
    LaunchedEffect(subCategories) {
        isMultiSelected = if (subCategories != null && subCategories.any {
            it in superCategory.enums()
        }) {
            true
        } else {
            null
        }
    }

    val iconModifier = when (isSuperCategorySelectable) {
        true -> Modifier.clickable {
            isMultiSelected?.let {
                isMultiSelected = !isMultiSelected!!
            } ?: when (menuExpanded) {
                true -> onMenuSelect(null)
                false -> onMenuSelect(superCategory)
            }
        }
        false -> Modifier
    }

    /* Alignment properties for top level TextButton */
    val textAlign = when (isSuperCategorySelectable) {
        true -> null
        false -> TextAlign.Center
    }

    /* Bottom padding when last item is expandable and expanded */
    val lastItemPadding = when (superCategory to menuExpanded) {
        NonAdministrative to true -> 9.dp
        else -> 0.dp
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale


    /* Menu item content */
    Column(modifier = modifier.padding(bottom = lastItemPadding)) {
        /* Top text button */
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = Dimens.textButtonVertPad,
                    bottom = Dimens.textButtonVertPad + lastItemPadding,
                )
                .combinedClickable(
                    onClick = {
                        if (isSuperCategorySelectable) {
                            /* If super is selectable make expandMenu null and select super
                             * as category */
                            onMenuSelect(null)
                            onCategorySelect(superCategory, null)
                        } else if (!menuExpanded) {
                            /* If current menu is not expanded, update expandMenu state with
                             * item's cat */
                            onMenuSelect(superCategory)
                        } else if (menuSuperCategoryList.any {
                            superCategory in it.subCategories }) {
                            /* If item's cat is in a superCategory, update expandMenu state with
                             * the super */
                            onMenuSelect(menuSuperCategoryList.find {
                                superCategory in it.subCategories
                            })
                        } else {
                            /* Else, make expandMenu state null */
                            onMenuSelect(null)
                        }
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCategoryMultiSelect(superCategory, null)
                    },
                ),
        ) {
            /* Manage dynamic spacer size for button title center alignment */
            val buttonWidth = maxWidth
            var textWidth by remember { mutableStateOf(value = 0.dp) }

            val iconSize = Dimens.standardIconSize24 * fontScale
            val iconSizePadding = iconSize + Dimens.textButtonHorizPad
            val iconsTotalSize = (iconSizePadding + 1.dp) * 2


            /* Top text button content */
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(color = buttonColors1.containerColor),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isSuperCategorySelectable) {
                    Spacer(
                        modifier = Modifier.width(width =
                            if (textWidth + iconsTotalSize < buttonWidth) {
                                iconSizePadding
                            } else 0.dp
                        )
                    )
                }

                Text(
                    text = stringResource(superCategory.title),
                    modifier = Modifier.padding(ButtonDefaults.TextButtonContentPadding)
                        .weight(weight = 1f, fill = false),
                    color = buttonColors1.contentColor,
                    textAlign = textAlign,
                    onTextLayout = { textLayoutResult ->
                        with(density) { textWidth = textLayoutResult.size.width.toDp() }
                    },
                    style = textButtonStyle,
                )

                if (!menuExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.menu_sub_icon_expand),
                        modifier = iconModifier
                            .padding(end = Dimens.textButtonHorizPad)
                            .size(iconSize),
                        tint = buttonColors1.contentColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.menu_sub_icon_collapse),
                        modifier = iconModifier
                            .padding(end = Dimens.textButtonHorizPad)
                            .size(iconSize),
                        tint = buttonColors1.contentColor,
                    )
                }
            }
        }

        /* Sub-menu content */
        AnimatedVisibility(
            visible = isMultiSelected ?: menuExpanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                shrinkTowards = Alignment.Top,
            )
        ) {
            Card(
                modifier = Modifier.padding(
                    start = Dimens.small10,
                    end = Dimens.small10,
                    top = 1.5.dp * fontScale * 1.5f,
                ),
                shape = Shapes.large,
                colors = cardColors2,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = Dimens.extraSmall4),
                ) {
                    superCategory.subCategories.filterIsInstance<FlagCategoryWrapper>()
                        .forEach { subWrapper ->

                        Box(
                            modifier = Modifier
                                .padding(
                                    top = Dimens.textButtonVertPad,
                                    bottom = Dimens.textButtonVertPad,
                                )
                                .combinedClickable(
                                    onClick = { onCategorySelect(null, subWrapper.enum) },
                                    onLongClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onCategoryMultiSelect(null, subWrapper.enum)
                                    }
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(color = buttonColors2.containerColor),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(subWrapper.enum.title),
                                        modifier = Modifier
                                            .padding(ButtonDefaults.TextButtonContentPadding),
                                        color = buttonColors2.contentColor,
                                        fontWeight = FontWeight.Normal,
                                        style = textButtonStyle,
                                    )
                                }

                                if (subCategories != null && subWrapper.enum in subCategories ||
                                    currentCategoryTitle == subWrapper.enum.title) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
                                        modifier = Modifier
                                            .padding(end = Dimens.textButtonHorizPad)
                                            .size(Dimens.standardIconSize24 * fontScale * 0.9f),
                                        tint = buttonColors2.contentColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MenuSuperItem(
    modifier: Modifier = Modifier,
    haptics: HapticFeedback,
    textButtonStyle: TextStyle,
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    cardColors1: CardColors,
    cardColors2: CardColors,
    isChildSelectable: Boolean,
    @StringRes currentCategoryTitle: Int,
    parentSuperCategory: FlagSuperCategory,
    subCategories: List<FlagCategory>?, /* for if multiple sub-categories are selected */
    expandMenu: FlagSuperCategory?,
    onMenuSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    val parentExpanded = parentSuperCategory == expandMenu || parentSuperCategory
        .subCategories.filterIsInstance<FlagSuperCategory>().contains(element = expandMenu)

    /* Separated boolean for controlling submenu expansion when multi select (subCategories) */
    var isMultiSelected: Boolean? by remember { mutableStateOf(value = null) }

    /* on subCategories change, if any subcategories in the menus super, expand the menu, else
     * null so that regular menu expansion property (menuExpanded) applies */
    LaunchedEffect(subCategories) {
        isMultiSelected = if (subCategories != null &&
            subCategories.any { subCategory ->
                parentSuperCategory.subCategories.filterIsInstance<FlagSuperCategory>().any {
                    superCategory ->
                    subCategory in superCategory.enums()
                }
            }) {
            true
        } else {
            null
        }
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale


    /* Menu item content */
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.padding(horizontal = Dimens.small10),
            shape = Shapes.large,
            colors = cardColors2,
        ) {

            TextButton(
                onClick = {
                    isMultiSelected?.let {
                        isMultiSelected = !isMultiSelected!!
                    } ?: when (expandMenu) {
                        in parentSuperCategory.subCategories
                            .filterIsInstance<FlagSuperCategory>() -> onMenuSelect(null)
                        parentSuperCategory -> onMenuSelect(null)
                        else -> onMenuSelect(parentSuperCategory)
                    }
                },
                shape = RoundedCornerShape(0.dp),
                colors = buttonColors2,
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    /* Manage dynamic spacer size for button title center alignment */
                    val buttonWidth = maxWidth
                    var textWidth by remember { mutableStateOf(value = 0.dp) }

                    val iconSize = Dimens.standardIconSize24 * fontScale
                    val iconPadding = 2.dp * fontScale
                    val iconSizePadding = iconSize + iconPadding
                    val iconsTotalSize = (iconSizePadding + 1.dp) * 2


                    Row(
                        modifier = modifier.fillMaxWidth(),
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
                            text = stringResource(parentSuperCategory.title),
                            modifier = Modifier.weight(weight = 1f, fill = false),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            onTextLayout = { textLayoutResult ->
                                with(density) { textWidth = textLayoutResult.size.width.toDp() }
                            }
                        )

                        if (isMultiSelected ?: parentExpanded) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.menu_sub_icon_collapse),
                                modifier = Modifier.size(iconSize)
                                    .padding(start = iconPadding),
                                tint = buttonColors1.contentColor,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.menu_sub_icon_expand),
                                modifier = Modifier.size(iconSize)
                                    .padding(start = iconPadding),
                                tint = buttonColors1.contentColor,
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = isMultiSelected ?: parentExpanded,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                    expandFrom = Alignment.Top,
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                    shrinkTowards = Alignment.Top,
                )
            ) {
                Card(
                    shape = Shapes.large,
                    colors = cardColors2,
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        parentSuperCategory.subCategories.forEach { superCategory ->
                            superCategory as FlagSuperCategory

                            MenuItemExpandable(
                                haptics = haptics,
                                textButtonStyle = textButtonStyle,
                                buttonColors1 = buttonColors2,
                                buttonColors2 = buttonColors1,
                                cardColors2 = cardColors1,
                                isSuperCategorySelectable = isChildSelectable,
                                currentCategoryTitle = currentCategoryTitle,
                                superCategory = superCategory,
                                subCategories = subCategories,
                                menuExpanded = superCategory == expandMenu,
                                onMenuSelect = onMenuSelect,
                                onCategorySelect = onCategorySelect,
                                onCategoryMultiSelect = onCategoryMultiSelect,
                            )
                        }
                    }
                }
            }
        }
    }
}


/* TODO: Centralize filter logic with GameViewModel's getScoreDataSupers() */
private fun filterSuperCategories(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): List<FlagSuperCategory> {
    return superCategories.filterNot { superCategory ->
        superCategory.enums().any { it in subCategories }

    }.let { superCategoriesNew ->
        when (superCategoriesNew.size to subCategories.isEmpty()) {
            0 to true -> superCategoriesNew
            else -> superCategoriesNew.filterNot { it == All }
        }
    }
}

private fun getCategoriesStringResIds(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): List<Int> {
    val superCategoriesFiltered = filterSuperCategories(
        superCategories = superCategories,
        subCategories = subCategories,
    )

    val politicalCategories = subCategories.filter { subCategory ->
        Political.subCategories.filterIsInstance<FlagSuperCategory>().any {
            subCategory in it.enums()
        }
    }
    val nonPoliticalCategories = subCategories.filterNot { it in politicalCategories }

    val isHistorical =
        Historical in superCategoriesFiltered || FlagCategory.HISTORICAL in subCategories

    val isAssociatedCountrySupers = superCategoriesFiltered.contains(AutonomousRegion) &&
            (superCategoriesFiltered.contains(SovereignCountry) ||
                    subCategories.contains(FlagCategory.SOVEREIGN_STATE))

    val isAutonomousRegionalSupers = superCategoriesFiltered.containsAll(
        elements = listOf(AutonomousRegion, Regional)
    )

    val isAutonomousRegionalSub = superCategoriesFiltered.contains(AutonomousRegion) &&
            subCategories.any { it in Regional.enums() }

    val stringResIds = mutableListOf<Int>()

    if (isHistorical) {
        stringResIds.add(Historical.title)
        stringResIds.add(R.string.string_whitespace)
    }

    politicalCategories.forEach { politicalCategory ->
        when (stringResIds.isEmpty()) {
            true -> stringResIds.add(politicalCategory.title)
            false -> stringResIds.add(politicalCategory.string)
        }
        when (politicalCategory == politicalCategories.last()) {
            true -> stringResIds.add(R.string.string_whitespace)
            false -> stringResIds.add(R.string.string_comma_whitespace)
        }
    }
}