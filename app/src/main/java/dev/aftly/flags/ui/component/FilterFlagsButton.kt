package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.superCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.NonAdministrative
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timings


@Composable
fun FilterFlagsButton(
    modifier: Modifier = Modifier,
    getString: (Int) -> String,
    onButtonHeightChange: (Dp) -> Unit,
    buttonExpanded: Boolean,
    onButtonExpand: () -> Unit,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    cardColors1: CardColors = CardDefaults.cardColors(containerColor = containerColor1),
    cardColors2: CardColors = CardDefaults.cardColors(containerColor = containerColor2),
    buttonColors1: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor1),
    buttonColors2: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor2),
    fontScale: Float,
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    /* Determines the expanded menu */
    var expandMenu by remember { mutableStateOf<FlagSuperCategory?>(value = null) }

    /* When menu collapse, return expandMenu state to current selected category (if ui differs) */
    LaunchedEffect(buttonExpanded) {
        /* Collapse sub-menu if it's super is selected */
        if (!buttonExpanded && currentCategoryTitle == currentSuperCategory.title) {
            expandMenu = null
        } else if (!buttonExpanded && expandMenu != currentSuperCategory &&
            currentCategoryTitle != expandMenu?.title &&
            currentSuperCategory != Political) {
            /* Expand sub-menu of current super when differs from current expanded menu */
            expandMenu = currentSuperCategory

        } else if (!buttonExpanded && currentSuperCategory == Political) {
            /* As Political contains supers, expandMenu cannot just be determined from
             * currentSuperCategory. Loop through each super to find the selected category and
             * set expandMenu state to the sub-super */
            for (superCategory in currentSuperCategory.subCategories) {
                superCategory as FlagSuperCategory
                if (superCategory.subCategories.any { it as FlagCategory
                        it.title == currentCategoryTitle }) {
                    expandMenu = superCategory
                }
            }
        }
    }

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

    /* Manage button title & exceptions */
    val buttonTitle = when (currentSuperCategory) {
        SovereignCountry -> stringResource(R.string.category_super_sovereign_country_title)
        Political -> stringResource(currentCategoryTitle) +
                stringResource(R.string.button_title_state_flags)
        International -> stringResource(R.string.category_international_organization_title) +
                stringResource(R.string.button_title_flags)
        else -> stringResource(currentCategoryTitle) + stringResource(R.string.button_title_flags)
    }

    /* Manage dynamic spacer size for button title center alignment */
    val density = LocalDensity.current
    var buttonWidth by remember { mutableStateOf(value = 0.dp) }
    var textWidth by remember { mutableStateOf(value = 0.dp) }
    var spacerWidth by remember { mutableStateOf(value = 0.dp) }
    val iconSize = Dimens.standardIconSize24 * fontScale
    val iconPadding = 2.dp * fontScale

    LaunchedEffect(textWidth) { spacerWidth =
        if (buttonWidth != 0.dp && textWidth != 0.dp &&
            textWidth + (iconSize + iconPadding + 1.dp) * 2 < buttonWidth) {
            iconSize + iconPadding
        } else 0.dp
    }


    /* Filter button content */
    Column(modifier = modifier) {
        Button(
            onClick = { onButtonExpand() },
            modifier = Modifier.padding(bottom = Dimens.small8) /* Separate Button from Menu */
                .height(buttonHeight),
            shape = MaterialTheme.shapes.large,
            colors = buttonColors2,
            contentPadding = PaddingValues(horizontal = Dimens.medium16),
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
                    .onSizeChanged {
                        with(density) { buttonWidth = it.width.toDp() }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(
                    modifier = Modifier.width(spacerWidth)
                )

                Box(
                    modifier = Modifier.weight(weight = 1f, fill = false)
                        .onSizeChanged {
                            with(density) { textWidth = it.width.toDp() }
                        },
                ) {
                    Text(
                        text = buttonTitle,
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.lineCount > 1) {
                                buttonHeight = twoLineButtonHeight
                            } else if (buttonHeight != oneLineButtonHeight) {
                                buttonHeight = oneLineButtonHeight
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Box(modifier = Modifier.padding(start = iconPadding)) {
                    if (!buttonExpanded) {
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
                colors = cardColors1,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        top = Dimens.small8,
                        bottom = Dimens.small10,
                    ),
                ) {
                    items(items = superCategoryList) { superCategory ->
                        val buttonColors = when (superCategory) {
                            currentSuperCategory -> buttonColors2
                            else -> buttonColors1
                        }

                        if (superCategory.subCategories.size == 1 || superCategory == All) {
                            /* If superCategory has 1 sub category use 1 tier (static) menu item
                             * (where the superCategory is meant to represent a sub/FlagCategory) */
                            MenuItemStatic(
                                buttonColors = buttonColors,
                                superCategory = superCategory,
                                onCategorySelect = { newSuperCategory ->
                                    expandMenu = null
                                    if (newSuperCategory != currentSuperCategory) spacerWidth = 0.dp
                                    onCategorySelect(newSuperCategory, null)
                                    onButtonExpand()
                                },
                            )
                        } else if (superCategory.subCategories
                            .filterIsInstance<FlagCategory>().isNotEmpty()) {
                            /* If superCategory has any FlagCategories (ie. actual sub-categories)
                             * use 2 tier expandable menu */
                            MenuItemExpandable(
                                buttonColors1 = buttonColors,
                                buttonColors2 = buttonColors2,
                                cardColors2 = cardColors2,
                                density = density,
                                fontScale = fontScale,
                                isSuperCategorySelectable = true,
                                currentCategoryTitle = currentCategoryTitle,
                                superCategory = superCategory,
                                menuExpanded = superCategory == expandMenu,
                                onMenuSelect = { expandMenu = it },
                                onCategorySelect = { newSuperCategory, newSubCategory ->
                                    if (!isNewCategoryCurrent(
                                        newSuperCategory = newSuperCategory,
                                        newSubCategory = newSubCategory,
                                        currentCategoryTitle = currentCategoryTitle,
                                        getString = getString,
                                    )) { spacerWidth = 0.dp }

                                    onCategorySelect(newSuperCategory,newSubCategory)
                                    onButtonExpand()
                                },
                            )
                        } else {
                            /* If superCategory only contains superCategories use 3 tier menu */
                            MenuSuperItem(
                                buttonColors1 = buttonColors1,
                                buttonColors2 = buttonColors2,
                                cardColors1 = cardColors1,
                                cardColors2 = cardColors2,
                                density = density,
                                fontScale = fontScale,
                                isChildSelectable = false,
                                currentCategoryTitle = currentCategoryTitle,
                                parentSuperCategory = superCategory,
                                expandMenuState = expandMenu,
                                onMenuSelect = { expandMenu = it },
                                onCategorySelect = { newSuperCategory, newSubCategory ->
                                    spacerWidth = 0.dp
                                    onCategorySelect(newSuperCategory,newSubCategory)
                                    onButtonExpand()
                                },
                            )
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
    buttonColors: ButtonColors,
    superCategory: FlagSuperCategory,
    onCategorySelect: (FlagSuperCategory) -> Unit,
) {
    val fontWeight = when (superCategory) {
        All -> FontWeight.ExtraBold
        else -> null
    }
    val lastItemPadding = when (superCategory) {
        Historical -> 5.dp
        else -> 0.dp
    }

    Column(modifier = modifier.padding(bottom = lastItemPadding)) {
        TextButton(
            onClick = { onCategorySelect(superCategory) },
            shape = RoundedCornerShape(0.dp),
            colors = buttonColors,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(superCategory.title),
                    fontWeight = fontWeight,
                )
            }
        }
    }
}


@Composable
private fun MenuItemExpandable(
    modifier: Modifier = Modifier,
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    cardColors2: CardColors,
    density: Density,
    fontScale: Float,
    isSuperCategorySelectable: Boolean,
    @StringRes currentCategoryTitle: Int,
    superCategory: FlagSuperCategory,
    menuExpanded: Boolean,
    onMenuSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    val iconModifier = when (isSuperCategorySelectable) {
        true -> Modifier.clickable {
            when (menuExpanded) {
                false -> onMenuSelect(superCategory)
                true -> onMenuSelect(null)
            }
        }
        false -> Modifier
    }

    /* Alignment properties for top level TextButton */
    val textAlign = when (isSuperCategorySelectable) {
        true -> null
        false -> TextAlign.Center
    }
    val boxAlignment = when (isSuperCategorySelectable) {
        true -> Alignment.CenterStart
        false -> Alignment.Center
    }

    /* Bottom padding when last item is expandable and expanded */
    val lastItemPadding = when (superCategory to menuExpanded) {
        NonAdministrative to true -> 9.dp
        else -> 0.dp
    }

    /* Manage dynamic spacer size for button title center alignment */
    var buttonWidth by remember { mutableStateOf(value = 0.dp) }
    var textWidth by remember { mutableStateOf(value = 0.dp) }
    var spacerWidth by remember { mutableStateOf(value = 0.dp) }
    val iconSize = Dimens.standardIconSize24 * fontScale
    val iconPadding = 2.dp * fontScale

    LaunchedEffect(textWidth) { spacerWidth =
        if (buttonWidth != 0.dp && textWidth != 0.dp &&
            textWidth + (iconSize + iconPadding + 1.dp) * 2 < buttonWidth) {
            iconSize + iconPadding
        } else 0.dp
    }


    /* Menu item content */
    Column(modifier = modifier.padding(bottom = lastItemPadding)) {
        TextButton(
            onClick = {
                /* Reminder: expandMenu state contains the superCategory whose menu is to expand */
                /* Reminder: Menu uses a superCategory to select and/or show it's sub categories */
                if (isSuperCategorySelectable) {
                    /* If super is selectable make expandMenu null and select super as category */
                    onMenuSelect(null)
                    onCategorySelect(superCategory, null)
                } else if (!menuExpanded) {
                    /* If current menu is not expanded, update expandMenu state with item's cat */
                    onMenuSelect(superCategory)
                } else if (superCategoryList.any { superCategory in it.subCategories }) {
                    /* If item's cat is in a superCategory, update expandMenu state with the super */
                    onMenuSelect(superCategoryList.find { superCategory in it.subCategories })
                } else {
                    /* Else, make expandMenu state null */
                    onMenuSelect(null)
                }
            },
            shape = RoundedCornerShape(0.dp),
            colors = buttonColors1,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .onSizeChanged {
                        with(density) { buttonWidth = it.width.toDp() }
                    },
                horizontalArrangement = Arrangement.SpaceBetween, // TODO: was rowArrangement
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isSuperCategorySelectable) {
                    Spacer(
                        modifier = Modifier.width(spacerWidth)
                    )
                }

                Box(
                    modifier = Modifier.weight(weight = 1f, fill = false)
                        .padding(end = 2.dp * fontScale)
                        .onSizeChanged {
                            with(density) { textWidth = it.width.toDp() }
                        },
                ) {
                    Text(
                        text = stringResource(superCategory.title),
                        textAlign = textAlign,
                    )
                }

                if (!menuExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.menu_sub_icon_expand),
                        modifier = iconModifier.size(Dimens.standardIconSize24 * fontScale),
                        tint = buttonColors1.contentColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.menu_sub_icon_collapse),
                        modifier = iconModifier.size(Dimens.standardIconSize24 * fontScale),
                        tint = buttonColors1.contentColor,
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = menuExpanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
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
                    superCategory.subCategories.forEach { subCategory ->
                        subCategory as FlagCategory

                        TextButton(
                            onClick = { onCategorySelect(null, subCategory) },
                            shape = RoundedCornerShape(0.dp),
                            colors = buttonColors2,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(subCategory.title),
                                        fontWeight = FontWeight.Normal,
                                    )
                                }

                                if (currentCategoryTitle == subCategory.title) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
                                        modifier = Modifier
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
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    cardColors1: CardColors,
    cardColors2: CardColors,
    density: Density,
    fontScale: Float,
    isChildSelectable: Boolean,
    @StringRes currentCategoryTitle: Int,
    parentSuperCategory: FlagSuperCategory,
    expandMenuState: FlagSuperCategory?,
    onMenuSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    val parentExpanded = parentSuperCategory == expandMenuState || parentSuperCategory
        .subCategories.filterIsInstance<FlagSuperCategory>().contains(element = expandMenuState)

    /* Manage dynamic spacer size for button title center alignment */
    var buttonWidth by remember { mutableStateOf(value = 0.dp) }
    var textWidth by remember { mutableStateOf(value = 0.dp) }
    var spacerWidth by remember { mutableStateOf(value = 0.dp) }
    val iconSize = Dimens.standardIconSize24 * fontScale
    val iconPadding = 2.dp * fontScale

    LaunchedEffect(textWidth) { spacerWidth =
        if (buttonWidth != 0.dp && textWidth != 0.dp &&
            textWidth + (iconSize + iconPadding + 1.dp) * 2 < buttonWidth) {
            iconSize + iconPadding
        } else 0.dp
    }


    /* Menu item content */
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.padding(horizontal = Dimens.small10),
            shape = Shapes.large,
            colors = cardColors2,
        ) {
            TextButton(
                onClick = {
                    when (expandMenuState) {
                        in parentSuperCategory.subCategories
                            .filterIsInstance<FlagSuperCategory>() -> onMenuSelect(null)
                        parentSuperCategory -> onMenuSelect(null)
                        else -> onMenuSelect(parentSuperCategory)
                    }
                },
                shape = RoundedCornerShape(0.dp),
                colors = buttonColors2,
            ) {
                Row(
                    modifier = modifier.fillMaxWidth()
                        .onSizeChanged {
                            with(density) { buttonWidth = it.width.toDp() }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(
                        modifier = Modifier.width(spacerWidth)
                    )

                    Box(
                        modifier = Modifier.weight(weight = 1f, fill = false)
                            .padding(end = 2.dp * fontScale)
                            .onSizeChanged {
                                with(density) { textWidth = it.width.toDp() }
                            },
                    ) {
                        Text(
                            text = stringResource(parentSuperCategory.title),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )
                    }

                    if (!parentExpanded) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.menu_sub_icon_expand),
                            modifier = Modifier.size(Dimens.standardIconSize24 * fontScale),
                            tint = buttonColors1.contentColor,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.menu_sub_icon_collapse),
                            modifier = Modifier.size(Dimens.standardIconSize24 * fontScale),
                            tint = buttonColors1.contentColor,
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = parentExpanded,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                    expandFrom = Alignment.Top,
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                    shrinkTowards = Alignment.Top,
                )
            ) {
                Card(
                    shape = Shapes.large,
                    colors = cardColors2,
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        parentSuperCategory.subCategories.forEach { superCategory ->
                            superCategory as FlagSuperCategory

                            MenuItemExpandable(
                                buttonColors1 = buttonColors2,
                                buttonColors2 = buttonColors1,
                                cardColors2 = cardColors1,
                                density = density,
                                fontScale = fontScale,
                                isSuperCategorySelectable = isChildSelectable,
                                currentCategoryTitle = currentCategoryTitle,
                                superCategory = superCategory,
                                menuExpanded = superCategory == expandMenuState,
                                onMenuSelect = onMenuSelect,
                                onCategorySelect = onCategorySelect,
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun isNewCategoryCurrent(
    newSuperCategory: FlagSuperCategory?,
    newSubCategory: FlagCategory?,
    currentCategoryTitle: Int,
    getString: (Int) -> String,
): Boolean {
    val currentCategoryString = getString(currentCategoryTitle)

    return if (newSuperCategory != null &&
        getString(newSuperCategory.title) == currentCategoryString) {
        true
    } else if (newSubCategory != null &&
        getString(newSubCategory.title) == currentCategoryString) {
        true
    } else {
        false
    }
}