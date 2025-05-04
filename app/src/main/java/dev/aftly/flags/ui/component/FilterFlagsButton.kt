package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.superCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timings


@Composable
fun FilterFlagsButton(
    modifier: Modifier = Modifier,
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
            currentSuperCategory != FlagSuperCategory.Political) {
            /* Expand sub-menu of current super when differs from current expanded menu */
            expandMenu = currentSuperCategory

        } else if (!buttonExpanded && currentSuperCategory == FlagSuperCategory.Political) {
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
        FlagSuperCategory.SovereignCountry -> stringResource(R.string.category_super_sovereign_country_title)
        FlagSuperCategory.Political -> stringResource(currentCategoryTitle) +
                stringResource(R.string.button_title_state_flags)
        FlagSuperCategory.International -> stringResource(R.string.category_international_organization_title) +
                stringResource(R.string.button_title_flags)
        else -> stringResource(currentCategoryTitle) + stringResource(R.string.button_title_flags)
    }


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
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buttonTitle,
                    style = MaterialTheme.typography.titleMedium,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.lineCount > 1) {
                            buttonHeight = twoLineButtonHeight
                        } else if (buttonHeight != oneLineButtonHeight) {
                            buttonHeight = oneLineButtonHeight
                        }
                    }
                )

                if (!buttonExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.menu_icon_expand),
                        tint = buttonColors1.contentColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.menu_icon_collapse),
                        tint = buttonColors1.contentColor,
                    )
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
                    contentPadding = PaddingValues(vertical = Dimens.small8),
                ) {
                    items(items = superCategoryList) { superCategory ->
                        val buttonColors = when (superCategory) {
                            currentSuperCategory -> buttonColors2
                            else -> buttonColors1
                        }

                        if (superCategory.subCategories.size == 1 ||
                            superCategory == FlagSuperCategory.All) {
                            /* If superCategory has 1 sub category use 1 tier (static) menu item
                             * (where the superCategory is meant to represent a sub/FlagCategory) */
                            MenuItemStatic(
                                buttonColors = buttonColors,
                                superCategory = superCategory,
                                onCategorySelect = { newSuperCategory ->
                                    expandMenu = null
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
                                isSuperCategorySelectable = true,
                                currentCategoryTitle = currentCategoryTitle,
                                superCategory = superCategory,
                                menuExpanded = superCategory == expandMenu,
                                onMenuSelect = { expandMenu = it },
                                onCategorySelect = { newSuperCategory, newSubCategory ->
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
                                isChildSelectable = false,
                                currentCategoryTitle = currentCategoryTitle,
                                parentSuperCategory = superCategory,
                                expandMenuState = expandMenu,
                                onMenuSelect = { expandMenu = it },
                                onCategorySelect = { newSuperCategory, newSubCategory ->
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
        FlagSuperCategory.All -> FontWeight.ExtraBold
        else -> null
    }

    Column(modifier = modifier) {
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

    val rowArrangement = when (isSuperCategorySelectable) {
        true -> Arrangement.SpaceBetween
        else -> Arrangement.Center
    }


    Column(modifier = modifier) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = rowArrangement,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(superCategory.title))

                if (!menuExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.menu_sub_icon_expand),
                        modifier = iconModifier,
                        tint = buttonColors1.contentColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.menu_sub_icon_collapse),
                        modifier = iconModifier,
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
                modifier = Modifier.padding(horizontal = Dimens.small10),
                shape = Shapes.large,
                colors = cardColors2,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                                Text(
                                    text = stringResource(subCategory.title),
                                    fontWeight = FontWeight.Normal,
                                )
                                if (currentCategoryTitle == subCategory.title) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
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
    isChildSelectable: Boolean,
    @StringRes currentCategoryTitle: Int,
    parentSuperCategory: FlagSuperCategory,
    expandMenuState: FlagSuperCategory?,
    onMenuSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    val parentExpanded = parentSuperCategory == expandMenuState || parentSuperCategory
        .subCategories.filterIsInstance<FlagSuperCategory>().contains(element = expandMenuState)

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
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(parentSuperCategory.title),
                        fontWeight = FontWeight.ExtraBold,
                    )

                    if (!parentExpanded) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.menu_sub_icon_expand),
                            tint = buttonColors1.contentColor,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.menu_sub_icon_collapse),
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
                        .padding(bottom = Dimens.small10)
                    ) {
                        parentSuperCategory.subCategories.forEach { superCategory ->
                            superCategory as FlagSuperCategory

                            MenuItemExpandable(
                                buttonColors1 = buttonColors2,
                                buttonColors2 = buttonColors1,
                                cardColors2 = cardColors1,
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