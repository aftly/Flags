package dev.aftly.flags.ui.component

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
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.getCategoriesTitleIds
import dev.aftly.flags.ui.util.toWrapper


@Composable
fun CategoriesButtonMenu(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    buttonHorizontalPadding: Dp,
    flagCount: Int?,
    onButtonHeightChange: (Dp) -> Unit,
    isMenuEnabled: Boolean = true,
    isMenuExpanded: Boolean,
    onMenuButtonClick: () -> Unit,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    cardColors1: CardColors = CardDefaults.cardColors(containerColor = containerColor1),
    cardColors2: CardColors = CardDefaults.cardColors(containerColor = containerColor2),
    buttonColors1: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor1),
    buttonColors2: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor2),
    isSavedFlagsNotEmpty: Boolean,
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
    onSavedFlagsSelect: () -> Unit,
) {
    /* Expand single sub-menu at a time when it's category's are not selected. State represented by
     * the sub-menu's FlagSuperCategory */
    var expandSubMenu by remember { mutableStateOf<FlagSuperCategory?>(value = null) }

    /* When menu collapse, reset expandSubMenu state */
    LaunchedEffect(isMenuExpanded) {
        if (!isMenuExpanded) expandSubMenu = null
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
    val buttonTitle = buildString {
        getCategoriesTitleIds(superCategories, subCategories).forEach { resId ->
            append(stringResource(resId))
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
    val isDarkTheme = LocalDarkTheme.current
    val containerColor3 = when (isDarkTheme) {
        true -> Color.White
        false -> Color.Black
    }
    val buttonColors3 = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = buttonColors1.containerColor,
            stop = containerColor3,
            fraction = if (isDarkTheme) 0.25f else 0.165f,
        )
    )


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
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    bottom = scaffoldPadding.calculateBottomPadding(),
                    start = buttonHorizontalPadding,
                    end = buttonHorizontalPadding,
                ),
        ) {
            Button(
                onClick = onMenuButtonClick,
                modifier = Modifier
                    .padding(bottom = Dimens.small8) /* Separate Button from Menu */
                    .height(buttonHeight),
                enabled = isMenuEnabled,
                shape = MaterialTheme.shapes.large,
                colors = buttonColors2,
                contentPadding = PaddingValues(horizontal = Dimens.medium16),
            ) {
                @Suppress("UnusedBoxWithConstraintsScope")
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    /* Manage dynamic spacer size for button title center alignment */
                    val buttonWidth = maxWidth /* (BoxWithConstraintsScope usage) */
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
                        /* If not null (eg. on game screen) show flag count indicator,
                         * else spacer (for text centering) */
                        flagCount?.let { flagCount ->
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
                                            horizontal = 6.dp * fontScale,
                                        ),
                                    textAlign = TextAlign.Center,
                                    color = buttonColors2.containerColor,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        } ?: Spacer(
                            modifier = Modifier.width(width =
                                if (textWidth + iconsTotalSize < buttonWidth) iconSizePadding
                                else 0.dp
                            )
                        )

                        /* Button title */
                        Text(
                            text = buttonTitle,
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
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
                            Icon(
                                imageVector =
                                    if (isMenuExpanded) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                contentDescription =
                                    if (isMenuExpanded) stringResource(R.string.menu_icon_collapse)
                                    else stringResource(R.string.menu_icon_expand),
                                modifier = Modifier.size(iconSize),
                                tint = buttonColors1.contentColor,
                            )
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
                        /* Saved flags item */
                        if (isSavedFlagsNotEmpty) {
                            item {
                                MenuItem(
                                    modifier = Modifier.clickable(
                                        onClick = {
                                            expandSubMenu = null
                                            onSavedFlagsSelect()
                                            onMenuButtonClick()
                                        }
                                    ),
                                    textButtonStyle = textButtonStyle,
                                    buttonColors =
                                        if (superCategories.isEmpty() && subCategories.isEmpty()) {
                                            buttonColors2
                                        } else {
                                            buttonColors1
                                        },
                                    category = null,
                                )
                            }
                        }

                        /* Flag category items */
                        items(items = menuSuperCategoryList) { superCategory ->
                            val buttonColors = if (superCategory in superCategories) {
                                buttonColors2
                            } else if (superCategory != All &&
                                (superCategory.allEnums().any { it in subCategories } ||
                                        superCategory.supers().any { it in superCategories })) {
                                buttonColors3
                            } else {
                                buttonColors1
                            }


                            if (superCategory.subCategories.size == 1 || superCategory == All) {
                                /* If superCategory has 1 sub category use 1 tier (static) menu item
                                 * (where superCategory is meant to represent a sub/FlagCategory) */
                                SuperItemStatic(
                                    textButtonStyle = textButtonStyle,
                                    buttonColors = buttonColors,
                                    superCategory = superCategory,
                                    onCategorySelectSingle = { superCategory ->
                                        expandSubMenu = null
                                        onCategorySelectSingle(superCategory)
                                        onMenuButtonClick()
                                    },
                                    onCategorySelectMultiple = onCategorySelectMultiple,
                                )
                            } else if (superCategory.enums().isNotEmpty()) {
                                /* If superCategory has any FlagCategories (ie. sub-categories)
                                 * use 2 tier expandable menu */
                                SuperItemExpandable(
                                    textButtonStyle = textButtonStyle,
                                    buttonColors = buttonColors,
                                    buttonColorsChild = buttonColors2,
                                    cardColors = cardColors2,
                                    isCategoriesMenuExpanded = isMenuExpanded,
                                    isSuperCategorySelectable = true,
                                    itemSuperCategory = superCategory,
                                    selectedSubCategories = subCategories,
                                    isMenuExpandedParentState = superCategory == expandSubMenu,
                                    onSuperItemSelect = { expandSubMenu = it },
                                    onCategorySelectSingle = {
                                        onCategorySelectSingle(it)
                                        onMenuButtonClick()
                                    },
                                    onCategorySelectMultiple = onCategorySelectMultiple,
                                )
                            } else {
                                /* If superCategory only contains superCategories use 3 tier menu */
                                SuperItemOfSupers(
                                    textButtonStyle = textButtonStyle,
                                    buttonColors1 = buttonColors1,
                                    buttonColors2 = buttonColors2,
                                    cardColors1 = cardColors1,
                                    cardColors2 = cardColors2,
                                    isCategoriesMenuExpanded = isMenuExpanded,
                                    isSuperItemCardStyle = superCategory != Institution,
                                    itemSuperCategory = superCategory,
                                    selectedSubCategories = subCategories,
                                    isMenuExpandedParentState = expandSubMenu,
                                    onSuperItemExpand = { expandSubMenu = it },
                                    onCategorySelectSingle = {
                                        onCategorySelectSingle(it)
                                        onMenuButtonClick()
                                    },
                                    onCategorySelectMultiple = onCategorySelectMultiple,
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
private fun MenuItemContent(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    category: FlagCategoryBase?,
    onTextWidth: (Dp) -> Unit = {},
    preTextContent: (@Composable RowScope.() -> Unit)? = null,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = buttonColors.containerColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        preTextContent?.let { preTextContent() }

        Text(
            text = when (category) {
                is FlagSuperCategory -> stringResource(category.title)
                is FlagCategoryWrapper -> stringResource(category.enum.title)
                else -> stringResource(R.string.saved_flags)
            },
            modifier = when (preTextContent) {
                null -> Modifier.padding(ButtonDefaults.TextButtonContentPadding)
                else -> Modifier.padding(ButtonDefaults.TextButtonContentPadding)
                    .weight(weight = 1f, fill = false)
            },
            color = buttonColors.contentColor,
            fontWeight = fontWeight,
            textAlign = textAlign,
            onTextLayout = when (preTextContent) {
                null -> null
                else -> { textLayoutResult ->
                    with(density) { onTextWidth(textLayoutResult.size.width.toDp()) }
                }
            },
            style = textButtonStyle,
        )

        postTextContent()
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.padding(vertical = Dimens.textButtonVertPad)
        /*
        .indication(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current
        ),
         */
    ) {
        MenuItemContent(
            textButtonStyle = textButtonStyle,
            buttonColors = buttonColors,
            fontWeight = fontWeight,
            category = category,
            postTextContent = postTextContent,
        )
    }
}

@Composable
private fun MenuItemUnselectable(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    category: FlagCategoryBase?,
    lastItemPadding: Dp = 0.dp,
    iconSize: Dp,
    postTextContent: @Composable RowScope.() -> Unit = {},
) {
    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = Dimens.textButtonVertPad,
                bottom = Dimens.textButtonVertPad + lastItemPadding,
            )
            /*
            .indication(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ),
             */
    ) {
        /* Manage dynamic spacer size for button title center alignment */
        val buttonWidth = maxWidth /* (BoxWithConstraintsScope usage) */
        var textWidth by remember { mutableStateOf(value = 0.dp) }

        val iconSizePadding = iconSize + Dimens.textButtonHorizPad
        val iconsTotalSize = (iconSizePadding + 1.dp) * 2

        MenuItemContent(
            textButtonStyle = textButtonStyle,
            buttonColors = buttonColors,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            category = category,
            onTextWidth = { textWidth = it },
            preTextContent = {
                Spacer(
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
private fun MenuItemCard(
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
private fun SubItem(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    subCategory: FlagCategory,
    selectedSubCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val fontScale = LocalConfiguration.current.fontScale

    MenuItem(
        modifier = modifier.combinedClickable(
            onClick = { onCategorySelectSingle(subCategory.toWrapper()) },
            onLongClick = { onCategorySelectMultiple(subCategory.toWrapper()) },
        ),
        textButtonStyle = textButtonStyle,
        buttonColors = buttonColors,
        fontWeight = fontWeight,
        category = subCategory.toWrapper(),
    ) {
        if (subCategory in selectedSubCategories) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = Dimens.textButtonHorizPad)
                    .size(Dimens.standardIconSize24 * fontScale * 0.9f),
                tint = buttonColors.contentColor,
            )
        }
    }
}


@Composable
private fun SuperItemStatic(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    superCategory: FlagSuperCategory,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val fontWeight = when (superCategory) {
        All -> FontWeight.ExtraBold
        else -> null
    }

    MenuItem(
        modifier = modifier.combinedClickable(
            onClick = { onCategorySelectSingle(superCategory) },
            onLongClick = { onCategorySelectMultiple(superCategory) },
        ),
        textButtonStyle = textButtonStyle,
        buttonColors = buttonColors,
        fontWeight = fontWeight,
        category = superCategory,
    )
}


@Composable
private fun SuperItemExpandable(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    buttonColorsChild: ButtonColors,
    cardColors: CardColors,
    isCategoriesMenuExpanded: Boolean,
    isSuperCategorySelectable: Boolean,
    itemSuperCategory: FlagSuperCategory,
    selectedSubCategories: List<FlagCategory>,
    isMenuExpandedParentState: Boolean,
    onSuperItemSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val subCategories = itemSuperCategory.enums()

    /* Per-item boolean for holding menu expansion state, eg. when any of it's subs are selected */
    var isMenuExpandedLocalState: Boolean? by remember { mutableStateOf(value = null) }

    /* When the categories menu is open, upon subcategory change or categories menu open
     * expand the local menu if any of it's subcategories are selected */
    LaunchedEffect(
        key1 = selectedSubCategories,
        key2 = isCategoriesMenuExpanded,
    ) {
        if (isCategoriesMenuExpanded) {
            isMenuExpandedLocalState =
                if (selectedSubCategories.any { it in itemSuperCategory.enums() }) true else null
        }
    }

    /* Bottom padding when last item is expandable and expanded */
    val lastItemPadding = if (itemSuperCategory == Political.subCategories.last() &&
        (isMenuExpandedParentState || isMenuExpandedLocalState == true)) 9.dp else 0.dp

    val fontScale = LocalConfiguration.current.fontScale
    val iconSize = Dimens.standardIconSize24 * fontScale


    /* Menu item content */
    Column(modifier = modifier.padding(bottom = lastItemPadding)) {
        /* Header content */
        if (isSuperCategorySelectable) {
            MenuItem(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onSuperItemSelect(null)
                        onCategorySelectSingle(itemSuperCategory)
                    },
                    onLongClick = {
                        onCategorySelectMultiple(itemSuperCategory)
                    },
                ),
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColors,
                category = itemSuperCategory,
            ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    modifier = Modifier.clickable {
                        isMenuExpandedLocalState?.let {
                            isMenuExpandedLocalState = !isMenuExpandedLocalState!!
                        }

                        /* Manage parent expansion state */
                        when (isMenuExpandedParentState) {
                            true -> onSuperItemSelect(null)
                            false -> onSuperItemSelect(itemSuperCategory)
                        }
                    },
                    isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                    iconSize = iconSize,
                    tint = buttonColors.contentColor,
                )
            }

        } else {
            MenuItemUnselectable(
                modifier = Modifier.clickable(
                    onClick = {
                        if (isMenuExpandedLocalState != null) {
                            /* If (priority) local state not null, flip it's state */
                            isMenuExpandedLocalState = !isMenuExpandedLocalState!!

                        } else if (!isMenuExpandedParentState) {
                            /* If current menu is not expanded, update parent expand menu state with
                             * it's category */
                            onSuperItemSelect(itemSuperCategory)

                        } else if (menuSuperCategoryList
                                .any { itemSuperCategory in it.subCategories }
                        ) {
                            /* If item's category is in a menu superCategory, update parent expand
                             * menu state with the super (keeps super open upon item collapse) */
                            onSuperItemSelect(
                                menuSuperCategoryList.find { itemSuperCategory in it.subCategories }
                            )
                        } else {
                            /* Else, make expandMenu state null */
                            onSuperItemSelect(null)
                        }
                    }
                ),
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColors,
                category = itemSuperCategory,
                lastItemPadding = lastItemPadding,
                iconSize = iconSize,
            ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                    iconSize = iconSize,
                    tint = buttonColors.contentColor,
                )
            }
        }


        /* Sub-menu content */
        AnimatedVisibility(
            visible = isMenuExpandedLocalState ?: isMenuExpandedParentState,
            enter = expandVertically(
                animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                shrinkTowards = Alignment.Top,
            )
        ) {
            MenuItemCard(
                modifier = Modifier.padding(vertical = 1.5.dp * fontScale * 1.5f),
                cardColors = cardColors,
            ) {
                subCategories.forEach { subCategory ->
                    SubItem(
                        textButtonStyle = textButtonStyle,
                        buttonColors = buttonColorsChild,
                        fontWeight = FontWeight.Normal,
                        subCategory = subCategory,
                        selectedSubCategories = selectedSubCategories,
                        onCategorySelectSingle = onCategorySelectSingle,
                        onCategorySelectMultiple = onCategorySelectMultiple,
                    )
                }
            }
        }
    }
}


@Composable
private fun SuperItemsContainer(
    isCard: Boolean,
    cardColors: CardColors = CardDefaults.cardColors(),
    content: @Composable () -> Unit,
) {
    if (isCard) {
        MenuItemCard(
            innerPadding = 0.dp,
            cardColors = cardColors,
        ) {
            content()
        }
    } else {
        Column {
            content()
        }
    }
}

@Composable
private fun SuperItemOfSupers(
    textButtonStyle: TextStyle,
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    cardColors1: CardColors,
    cardColors2: CardColors,
    isCategoriesMenuExpanded: Boolean,
    isSuperItemCardStyle: Boolean,
    itemSuperCategory: FlagSuperCategory,
    selectedSubCategories: List<FlagCategory>,
    isMenuExpandedParentState: FlagSuperCategory?,
    onSuperItemExpand: (FlagSuperCategory?) -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    /* If parent expand menu state is the super or a subcategory of the super -> true */
    val isSuperItemExpanded = itemSuperCategory == isMenuExpandedParentState || itemSuperCategory
        .supers().contains(element = isMenuExpandedParentState)

    /* Per-item boolean for holding menu expansion state, eg. when any of it's subs are selected */
    var isMenuExpandedLocalState: Boolean? by remember { mutableStateOf(value = null) }

    /* When the categories menu is open, upon subcategory change or categories menu open
     * expand the local menu if any of it's subcategories are selected */
    LaunchedEffect(
        key1 = selectedSubCategories,
        key2 = isCategoriesMenuExpanded,
    ) {
        if (isCategoriesMenuExpanded) {
            isMenuExpandedLocalState =
                if (selectedSubCategories.any { subCat -> itemSuperCategory.supers().any {
                            superCat -> subCat in superCat.enums() } }) true else null
        }
    }

    val fontScale = LocalConfiguration.current.fontScale
    val iconSize = Dimens.standardIconSize24 * fontScale


    /* Menu item content */
    SuperItemsContainer(
        isCard = isSuperItemCardStyle,
        cardColors = cardColors2,
    ) {
        /* Header content */
        if (isSuperItemCardStyle) {
            MenuItemUnselectable(
                modifier = Modifier.clickable(
                    onClick = {
                        isMenuExpandedLocalState?.let {
                            isMenuExpandedLocalState = !isMenuExpandedLocalState!!
                        }

                        /* Manage parent expansion state */
                        when (isMenuExpandedParentState) {
                            in itemSuperCategory.supers() -> onSuperItemExpand(null)
                            itemSuperCategory -> onSuperItemExpand(null)
                            else -> onSuperItemExpand(itemSuperCategory)
                        }
                    }
                ),
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColors2,
                fontWeight = FontWeight.ExtraBold,
                category = itemSuperCategory,
                iconSize = iconSize,
            ) {
                MenuItemExpandableArrowIcon(
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                    iconSize = iconSize,
                    tint = buttonColors1.contentColor,
                )
            }

        } else {
            MenuItem(
                modifier = Modifier.combinedClickable(
                    onClick = { onCategorySelectSingle(itemSuperCategory) },
                    onLongClick = { onCategorySelectMultiple(itemSuperCategory) },
                ),
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColors1,
                category = itemSuperCategory,

            ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    modifier = Modifier.clickable {
                        isMenuExpandedLocalState?.let {
                            isMenuExpandedLocalState = !isMenuExpandedLocalState!!
                        }
                    },
                    isExpanded = isMenuExpandedLocalState ?: false,
                    iconSize = iconSize,
                    tint = buttonColors1.contentColor,
                )
            }
        }


        /* Sub-menu content */
        AnimatedVisibility(
            visible = isMenuExpandedLocalState ?: isSuperItemExpanded,
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
                modifier =
                    if (isSuperItemCardStyle) Modifier
                    else Modifier.padding(horizontal = Dimens.small10),
                shape = if (isSuperItemCardStyle) RoundedCornerShape(0.dp) else Shapes.large,
                colors = cardColors2
            ) {
                itemSuperCategory.supers().forEach { superCategory ->
                    SuperItemExpandable(
                        textButtonStyle = textButtonStyle,
                        buttonColors = if (isSuperItemCardStyle) buttonColors2 else buttonColors1,
                        buttonColorsChild =
                            if (isSuperItemCardStyle) buttonColors1 else buttonColors2,
                        cardColors = cardColors1,
                        isCategoriesMenuExpanded = isCategoriesMenuExpanded,
                        isSuperCategorySelectable = superCategory !in Political.supers(),
                        itemSuperCategory = superCategory,
                        selectedSubCategories = selectedSubCategories,
                        isMenuExpandedParentState = superCategory == isMenuExpandedParentState,
                        onSuperItemSelect = onSuperItemExpand,
                        onCategorySelectSingle = onCategorySelectSingle,
                        onCategorySelectMultiple = onCategorySelectMultiple,
                    )
                }
            }
        }
    }
}


@Composable
private fun MenuItemExpandableArrowIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    iconSize: Dp = Dp.Unspecified,
    tint: Color = Color.Unspecified,
) {
    Icon(
        imageVector =
            if (isExpanded) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown,
        contentDescription =
            if (isExpanded)
                stringResource(R.string.menu_sub_icon_collapse)
            else
                stringResource(R.string.menu_sub_icon_expand),
        modifier = modifier
            .padding(end = Dimens.textButtonHorizPad)
            .size(iconSize),
        tint = tint,
    )
}