package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import dev.aftly.flags.data.DataSource.countryFlagsList
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.filtermenu.FilterMode
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.getCategoriesTitleIds
import dev.aftly.flags.ui.util.toWrapper
import kotlinx.coroutines.delay


@Composable
fun FilterButtonMenu(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    buttonHorizontalPadding: Dp,
    flagCount: Int?,
    onButtonHeightChange: (Dp) -> Unit,
    isMenuEnabled: Boolean = true,
    isMenuExpanded: Boolean,
    onMenuButtonClick: () -> Unit,
    containerColorDark: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColorLight: Color = MaterialTheme.colorScheme.secondary,
    isSavedFlagsNotEmpty: Boolean,
    filterByCountry: FlagView?,
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
    onSavedFlagsSelect: () -> Unit,
    onFilterByCountry: (FlagView) -> Unit,
) {
    /* UI state */
    var expandSubMenu by remember { mutableStateOf<FlagSuperCategory?>(value = null) }
    var filterModeSelect by rememberSaveable { mutableStateOf(value = FilterMode.CATEGORY) }
    val countriesListState = rememberLazyListState()
    val countriesList = countryFlagsList

    /* Color properties */
    val cardColorsDark: CardColors = CardDefaults.cardColors(containerColor = containerColorDark)
    val cardColorsLight: CardColors = CardDefaults.cardColors(containerColor = containerColorLight)
    val buttonColorsDark: ButtonColors =
        ButtonDefaults.buttonColors(containerColor = containerColorDark)
    val buttonColorsLight: ButtonColors =
        ButtonDefaults.buttonColors(containerColor = containerColorLight)

    val isDarkTheme = LocalDarkTheme.current
    val containerColorSubSelected = if (isDarkTheme) Color.White else Color.Black
    val buttonColorsSubSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = buttonColorsDark.containerColor,
            stop = containerColorSubSelected,
            fraction = if (isDarkTheme) 0.25f else 0.165f,
        )
    )
    val containerColorInCardSubSelected = if (isDarkTheme) Color.White else Color.Black
    val buttonColorsInCardSubSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = buttonColorsLight.containerColor,
            stop = containerColorInCardSubSelected,
            fraction = if (isDarkTheme) 0.275f else 0.12f,
        )
    )
    val containerColorSuperInCardSelected = if (isDarkTheme) Color.Black else Color.White
    val buttonColorsSuperInCardSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = buttonColorsLight.containerColor,
            stop = containerColorSuperInCardSelected,
            fraction = if (isDarkTheme) 0.13f else 0.175f,
        )
    )

    /* Button height properties */
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

    val density = LocalDensity.current

    /* Icon properties */
    val iconSize = Dimens.standardIconSize24 * fontScale

    val iconPaddingButton = 2.dp * fontScale
    val iconSizePaddingButton = iconSize + iconPaddingButton
    val iconsTotalSizeButton = (iconSizePaddingButton + 1.dp) * 2

    val iconPaddingMenu = Dimens.textButtonHorizPad
    val iconSizePaddingMenu = iconSize + iconPaddingMenu
    val iconsTotalSizeMenu = (iconSizePaddingMenu + 1.dp) * 2

    var flagCountWidth by remember { mutableStateOf(iconSize) }
    val flagCountShape = when (isOneLine) {
        true -> MaterialTheme.shapes.large
        false -> MaterialTheme.shapes.medium
    }

    /* When menu collapse, reset ui state */
    LaunchedEffect(key1 = isMenuExpanded) {
        if (!isMenuExpanded) {
            delay(timeMillis = Timing.MENU_COLLAPSE.toLong())

            expandSubMenu = null
            filterModeSelect = FilterMode.CATEGORY
            countriesListState.scrollToItem(index = 0)
        }
    }

    /* Country list scroll effects */
    LaunchedEffect(key1 = filterModeSelect) {
        if (filterModeSelect == FilterMode.COUNTRY && filterByCountry != null) {

            delay(timeMillis = Timing.MENU_EXPAND.toLong() * 2)
            countriesListState.animateScrollToItem(
                index = countriesList.indexOfFirst { it.id == filterByCountry.id }
            )
        }
    }


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
                    .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                onAction = onMenuButtonClick,
            )
        }

        /* Content */
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
            /* Parent content */
            Button(
                onClick = onMenuButtonClick,
                modifier = Modifier
                    .padding(bottom = Dimens.small8) /* Separate Button from Menu */
                    .height(buttonHeight),
                enabled = isMenuEnabled,
                shape = MaterialTheme.shapes.large,
                colors = buttonColorsLight,
                contentPadding = PaddingValues(horizontal = Dimens.medium16),
            ) {
                MenuItemCentred(
                    isMenuButton = true,
                    buttonTitle = buttonTitle,
                    textButtonStyle = MaterialTheme.typography.titleMedium,
                    buttonColors = buttonColorsLight,
                    category = null,
                    iconPaddingButton = iconPaddingButton,
                    iconSizePadding = iconSizePaddingButton,
                    iconsTotalSize = iconsTotalSizeButton,
                    onSingleLineCount = { isSingleLine ->
                        if (isSingleLine) {
                            buttonHeight = oneLineButtonHeight
                            isOneLine = true
                        } else {
                            buttonHeight = twoLineButtonHeight
                            isOneLine = false
                        }
                    },
                    preTextContent = {
                        /* If not null (eg. on game screen) show flag count indicator,
                         * else spacer (for text centering) */
                        flagCount?.let { flagCount ->
                            Box(
                                modifier = Modifier.onSizeChanged { size ->
                                    flagCountWidth = with(receiver = density) { size.width.toDp() }
                                },
                            ) {
                                Text(
                                    text = "$flagCount",
                                    modifier = Modifier
                                        .requiredWidthIn(min = Dimens.standardIconSize24)
                                        .clip(flagCountShape)
                                        .background(buttonColorsLight.contentColor)
                                        .padding(
                                            vertical = 2.dp * fontScale,
                                            horizontal = 6.dp * fontScale,
                                        ),
                                    textAlign = TextAlign.Center,
                                    color = buttonColorsLight.containerColor,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        } ?: Spacer(modifier = Modifier.width(iconSize))
                    },
                    postTextContent = {
                        Box(
                            modifier = Modifier.width(flagCountWidth),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            MenuItemExpandableArrowIcon(
                                isExpanded = isMenuExpanded,
                                isMenuButton = true,
                                iconSize = iconSize,
                                iconPadding = iconPaddingButton,
                                tint = buttonColorsLight.contentColor
                            )
                        }
                    },
                )
            }

            /* Child content */
            MenuAnimatedVisibility(
                visible = isMenuExpanded
            ) {
                Card(
                    shape = Shapes.large,
                    colors = cardColorsDark,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                top = Dimens.small8 - 1.dp,
                                bottom = Dimens.extraSmall4,
                                start = Dimens.medium12,
                                end = 1.dp,
                            )
                            .fillMaxWidth()
                    ) {
                        FilterMode.entries.forEach { filterMode ->
                            MenuButton(
                                modifier = Modifier
                                    .padding(end = Dimens.small10 + 1.dp)
                                    .weight(1f),
                                title = filterMode.title,
                                isSelected = filterMode == filterModeSelect,
                                buttonColorsSelect = buttonColorsLight,
                                buttonColorsUnselect = buttonColorsDark,
                                onClick = { filterModeSelect = filterMode },
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = filterModeSelect,
                        transitionSpec = {
                            slideInHorizontally {
                                when (filterModeSelect) {
                                    FilterMode.CATEGORY -> -it
                                    FilterMode.COUNTRY -> it
                                }
                            } togetherWith slideOutHorizontally {
                                when (filterModeSelect) {
                                    FilterMode.CATEGORY -> it
                                    FilterMode.COUNTRY -> -it
                                }
                            }
                        }
                    ) { targetFilterMode ->
                        val lazyColumnContentPadding = PaddingValues(bottom = Dimens.small10)

                        when (targetFilterMode) {
                            FilterMode.COUNTRY -> LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                state = countriesListState,
                                contentPadding = lazyColumnContentPadding,
                            ) {
                                /* Flag category items */
                                items(items = countriesList) { flag ->
                                    MenuFlagItem(
                                        flag = flag,
                                        selectedFlag = filterByCountry,
                                        menu = RelatedFlagsMenu.POLITICAL,
                                        isShowDates = false,
                                        buttonColors = buttonColorsDark,
                                        buttonColorsSelect = buttonColorsLight,
                                        onFlagSelect = { flag ->
                                            val current = filterByCountry
                                            onFilterByCountry(flag)
                                            if (flag != current) onMenuButtonClick()
                                        },
                                    )
                                }
                            }
                            FilterMode.CATEGORY -> LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = lazyColumnContentPadding,
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
                                                    buttonColorsLight
                                                } else {
                                                    buttonColorsDark
                                                },
                                            category = null,
                                        )
                                    }
                                }

                                /* Flag category items */
                                items(items = menuSuperCategoryList) { superCategory ->
                                    val buttonColors = if (superCategory in superCategories) {
                                        buttonColorsLight
                                    } else if (superCategory != All &&
                                        (superCategory.allEnums().any { it in subCategories } ||
                                                superCategory.supers().any { it in superCategories })) {
                                        buttonColorsSubSelected
                                    } else {
                                        buttonColorsDark
                                    }


                                    if (superCategory.enums().size == 1 || superCategory == All) {
                                        /* If superCategory has 1 subcategory use 1 tier (static) menu item */
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
                                        /* If superCategory has multiple subcategories
                                         * use 2 tier (expandable) menu item */
                                        SuperItemExpandable(
                                            textButtonStyle = textButtonStyle,
                                            buttonColors = buttonColors,
                                            buttonColorsChild = buttonColorsLight,
                                            cardColors = cardColorsLight,
                                            iconSize = iconSize,
                                            iconPadding = iconPaddingMenu,
                                            iconsTotalSize = iconsTotalSizeMenu,
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
                                            buttonColorsSelectable = buttonColors,
                                            buttonColorsDark = buttonColorsDark,
                                            buttonColorsLight = buttonColorsLight,
                                            buttonColorsInCardSubSelected = buttonColorsInCardSubSelected,
                                            buttonColorsSuperInCardSelected =
                                                buttonColorsSuperInCardSelected,
                                            cardColorsDark = cardColorsDark,
                                            cardColorsLight = cardColorsLight,
                                            iconSize = iconSize,
                                            iconPadding = iconPaddingMenu,
                                            iconsTotalSize = iconsTotalSizeMenu,
                                            isCategoriesMenuExpanded = isMenuExpanded,
                                            isSuperItemCardStyle = superCategory != Institution,
                                            itemSuperCategory = superCategory,
                                            selectedSuperCategories = superCategories,
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
    }
}

@Composable
private fun MenuButton(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
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
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
        )

        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(start = Dimens.extraSmall4),
        )
    }
}

@Composable
private fun MenuItemContent(
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
private fun MenuItemCentred(
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

        MenuItemContent(
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
private fun MenuAnimatedVisibility(
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
private fun SubItem(
    modifier: Modifier = Modifier,
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    fontWeight: FontWeight? = null,
    iconSize: Dp,
    iconPadding: Dp,
    subCategory: FlagCategory,
    selectedSubCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
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
                    .padding(end = iconPadding)
                    .size(iconSize * 0.9f),
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
    textButtonStyle: TextStyle,
    buttonColors: ButtonColors,
    buttonColorsChild: ButtonColors,
    cardColors: CardColors,
    iconSize: Dp,
    iconPadding: Dp,
    iconsTotalSize: Dp,
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
    val lastItems = listOf(Political.subCategories.last(), Institution.subCategories.last())
    val lastItemPadding = if (itemSuperCategory in lastItems &&
        (isMenuExpandedParentState || isMenuExpandedLocalState == true)) 9.dp else 0.dp

    val fontScale = LocalConfiguration.current.fontScale

    val expandSuperClickableModifier = Modifier.clickable(
        onClick = {
            if (isMenuExpandedLocalState != null) {
                /* If (priority) local state not null, flip it's state */
                isMenuExpandedLocalState = !isMenuExpandedLocalState!!

            } else if (!isMenuExpandedParentState) {
                /* If current menu is not expanded, update parent expand menu state with
                 * it's category */
                onSuperItemSelect(itemSuperCategory)

            } else if (menuSuperCategoryList
                    .any { itemSuperCategory in it.subCategories }) {
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
    )


    /* Parent content */
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
                modifier = expandSuperClickableModifier,
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                iconSize = iconSize,
                iconPadding = iconPadding,
                tint = buttonColors.contentColor,
            )
        }
    } else {
        MenuItemCentred(
            modifier = expandSuperClickableModifier,
            textButtonStyle = textButtonStyle,
            buttonColors = buttonColors,
            category = itemSuperCategory,
            lastItemPadding = lastItemPadding,
            iconSizePadding = iconSize + iconPadding,
            iconsTotalSize = iconsTotalSize,
        ) {
            /* Local state takes priority for icon shown */
            MenuItemExpandableArrowIcon(
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                iconSize = iconSize,
                iconPadding = iconPadding,
                tint = buttonColors.contentColor,
            )
        }
    }

    /* Child content */
    MenuAnimatedVisibility(
        visible = isMenuExpandedLocalState ?: isMenuExpandedParentState
    ) {
        MenuItemCard(
            modifier = Modifier.padding(vertical = 2.25.dp * fontScale),
            cardColors = cardColors,
        ) {
            subCategories.forEach { subCategory ->
                SubItem(
                    textButtonStyle = textButtonStyle,
                    buttonColors = buttonColorsChild,
                    fontWeight = FontWeight.Normal,
                    iconSize = iconSize,
                    iconPadding = iconPadding,
                    subCategory = subCategory,
                    selectedSubCategories = selectedSubCategories,
                    onCategorySelectSingle = onCategorySelectSingle,
                    onCategorySelectMultiple = onCategorySelectMultiple,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(lastItemPadding))
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
        content()
    }
}

@Composable
private fun SuperItemOfSupers(
    textButtonStyle: TextStyle,
    buttonColorsSelectable: ButtonColors,
    buttonColorsDark: ButtonColors,
    buttonColorsLight: ButtonColors,
    buttonColorsInCardSubSelected: ButtonColors,
    buttonColorsSuperInCardSelected: ButtonColors,
    cardColorsDark: CardColors,
    cardColorsLight: CardColors,
    iconSize: Dp,
    iconPadding: Dp,
    iconsTotalSize: Dp,
    isCategoriesMenuExpanded: Boolean,
    isSuperItemCardStyle: Boolean,
    itemSuperCategory: FlagSuperCategory,
    selectedSuperCategories: List<FlagSuperCategory>,
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
                if ((selectedSubCategories.any { subCat ->
                    itemSuperCategory.supers().any { superCat ->
                        subCat in superCat.enums() } }) ||
                    itemSuperCategory.supers().any { it in selectedSuperCategories })
                    true else null
        }
    }

    val expandSuperClickableModifier = Modifier.clickable(
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
    )

    /* Content */
    SuperItemsContainer(
        isCard = isSuperItemCardStyle,
        cardColors = cardColorsLight,
    ) {
        /* Parent content */
        if (isSuperItemCardStyle) {
            MenuItemCentred(
                modifier = expandSuperClickableModifier,
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColorsLight,
                fontWeight = FontWeight.ExtraBold,
                category = itemSuperCategory,
                iconSizePadding = iconSize + iconPadding,
                iconsTotalSize = iconsTotalSize,
            ) {
                MenuItemExpandableArrowIcon(
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                    iconSize = iconSize,
                    iconPadding = iconPadding,
                    tint = buttonColorsLight.contentColor,
                )
            }
        } else {
            MenuItem(
                modifier = Modifier.combinedClickable(
                    onClick = { onCategorySelectSingle(itemSuperCategory) },
                    onLongClick = { onCategorySelectMultiple(itemSuperCategory) },
                ),
                textButtonStyle = textButtonStyle,
                buttonColors = buttonColorsSelectable,
                category = itemSuperCategory,

            ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    modifier = expandSuperClickableModifier,
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                    iconSize = iconSize,
                    iconPadding = iconPadding,
                    tint = buttonColorsSelectable.contentColor,
                )
            }
        }

        /* Child content */
        MenuAnimatedVisibility(
            visible = isMenuExpandedLocalState ?: isSuperItemExpanded
        ) {
            Card(
                modifier =
                    if (isSuperItemCardStyle) Modifier
                    else Modifier.padding(horizontal = Dimens.small10),
                shape = if (isSuperItemCardStyle) RoundedCornerShape(0.dp) else Shapes.large,
                colors = cardColorsLight
            ) {
                itemSuperCategory.supers().forEach { superCategory ->
                    val buttonColors =
                        if (superCategory in selectedSuperCategories) {
                            buttonColorsSuperInCardSelected
                        } else if (superCategory.enums().any { it in selectedSubCategories }) {
                            buttonColorsInCardSubSelected
                        } else {
                            buttonColorsLight
                        }

                    SuperItemExpandable(
                        textButtonStyle = textButtonStyle,
                        buttonColors = buttonColors,
                        buttonColorsChild = buttonColorsDark,
                        cardColors = cardColorsDark,
                        iconSize = iconSize,
                        iconPadding = iconPadding,
                        iconsTotalSize = iconsTotalSize,
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