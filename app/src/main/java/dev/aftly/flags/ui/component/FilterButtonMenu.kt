package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.data.DataSource.countryFlagsList
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.model.menu.filtermenu.FilterMode
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.color
import dev.aftly.flags.ui.util.colorSelect
import dev.aftly.flags.ui.util.getCategoriesTitleIds
import dev.aftly.flags.ui.util.textButtonEndPadding
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
    onMenuExpand: () -> Unit,
    colorPrimary: Color = FlagsMenu.FILTER.color(),
    colorSecondary: Color = FlagsMenu.FILTER.colorSelect(),
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
    var subMenuExpand by remember { mutableStateOf<FlagSuperCategory?>(value = null) }
    var filterModeSelect by rememberSaveable { mutableStateOf(value = FilterMode.CATEGORY) }
    val countriesListState = rememberLazyListState()
    val filterCountries = countryFlagsList

    /* Color properties */
    val cardColorsPrimary: CardColors = CardDefaults.cardColors(containerColor = colorPrimary)
    val cardColorsSecondary: CardColors = CardDefaults.cardColors(containerColor = colorSecondary)
    val buttonColorsPrimary: ButtonColors =
        ButtonDefaults.buttonColors(containerColor = colorPrimary)
    val buttonColorsSecondary: ButtonColors =
        ButtonDefaults.buttonColors(containerColor = colorSecondary)

    val isDarkTheme = LocalDarkTheme.current
    val containerColorSubSelected = if (isDarkTheme) Color.White else Color.Black
    val buttonColorsSubSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = colorPrimary,
            stop = containerColorSubSelected,
            fraction = if (isDarkTheme) 0.25f else 0.165f,
        )
    )
    val containerColorInCardSubSelected = if (isDarkTheme) Color.White else Color.Black
    val buttonColorsInCardSubSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = colorSecondary,
            stop = containerColorInCardSubSelected,
            fraction = if (isDarkTheme) 0.275f else 0.12f,
        )
    )
    val containerColorSuperInCardSelected = if (isDarkTheme) Color.Black else Color.White
    val buttonColorsSuperInCardSelected = ButtonDefaults.buttonColors(
        containerColor = lerp(
            start = colorSecondary,
            stop = containerColorSuperInCardSelected,
            fraction = if (isDarkTheme) 0.13f else 0.175f,
        )
    )

    /* Button height properties */
    val fontScale = LocalConfiguration.current.fontScale
    val buttonHeightOneLine = Dimens.filterButtonHeight30 * fontScale
    val buttonHeightTwoLine = Dimens.filterButtonHeightTwoLine50 * fontScale
    var isButtonOneLine by remember { mutableStateOf(value = true) }
    var buttonHeight by remember { mutableStateOf(value = buttonHeightOneLine) }
    LaunchedEffect(key1 = buttonHeight) {
        when (buttonHeight) {
            buttonHeightOneLine -> onButtonHeightChange(buttonHeightOneLine)
            buttonHeightTwoLine -> onButtonHeightChange(buttonHeightTwoLine)
        }
    }

    /* Manage button title & exceptions */
    val buttonTitle = buildString {
        getCategoriesTitleIds(superCategories, subCategories).forEach { resId ->
            append(stringResource(resId))
        }
    }

    var flagCountWidth by remember { mutableStateOf(value = Dimens.iconSize24) }
    val filterModeButtonPadding = Dimens.small10 + 1.dp

    val lazyColumnModifier = Modifier.fillMaxWidth()
    val lazyColumnContentPadding = PaddingValues(bottom = Dimens.small10)

    /* When menu collapse, reset ui state */
    LaunchedEffect(key1 = isMenuExpanded) {
        if (!isMenuExpanded) {
            delay(timeMillis = Timing.MENU_COLLAPSE.toLong())

            subMenuExpand = null
            filterModeSelect = FilterMode.CATEGORY
            countriesListState.scrollToItem(index = 0)
        }
    }

    /* Country list scroll effects */
    LaunchedEffect(key1 = filterModeSelect) {
        if (filterModeSelect == FilterMode.COUNTRY && filterByCountry != null) {

            delay(timeMillis = Timing.MENU_EXPAND.toLong() * 2)
            countriesListState.animateScrollToItem(
                index = filterCountries.indexOfFirst { it.id == filterByCountry.id }
            )
        }
    }


    Box(modifier = modifier) {
        /* Scrim behind expanded menu, tap gestures close menu */
        MenuScrim(
            visible = isMenuExpanded,
            onClick = onMenuExpand,
        )

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
            /* MENU BUTTON */
            Button(
                onClick = onMenuExpand,
                modifier = Modifier
                    .padding(bottom = Dimens.small8) /* Separate Button from Menu */
                    .height(buttonHeight),
                enabled = isMenuEnabled,
                shape = MaterialTheme.shapes.large,
                colors = buttonColorsSecondary,
                contentPadding = PaddingValues(horizontal = Dimens.medium16),
            ) {
                MenuCategoryItemCentred(
                    isMenuButton = true,
                    buttonTitle = buttonTitle,
                    buttonColors = buttonColorsSecondary,
                    category = null,
                    onSingleLineCount = { isSingleLine ->
                        if (isSingleLine) {
                            buttonHeight = buttonHeightOneLine
                            isButtonOneLine = true
                        } else {
                            buttonHeight = buttonHeightTwoLine
                            isButtonOneLine = false
                        }
                    },
                    preTextContent = {
                        if (flagCount != null) {
                            FlagCounter(
                                flagCount = flagCount,
                                textColor = buttonColorsSecondary.containerColor,
                                backgroundColor = buttonColorsSecondary.contentColor,
                                isButtonOneLine = isButtonOneLine,
                                onWidth = { flagCountWidth = it },
                            )
                        } else {
                            /* To ensure button title centering */
                            Spacer(modifier = Modifier.width(Dimens.iconSize24))
                        }
                    },
                    postTextContent = {
                        Box(
                            modifier = Modifier.width(flagCountWidth),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            MenuItemExpandableArrowIcon(
                                isExpanded = isMenuExpanded,
                                isMenuButton = true,
                                tint = buttonColorsSecondary.contentColor
                            )
                        }
                    },
                )
            }

            /* MENU CARD */
            MenuAnimatedVisibility(visible = isMenuExpanded) {
                MenuCard(menu = FlagsMenu.FILTER) {
                    /* FilterMode buttons */
                    Row(
                        modifier = Modifier
                            .padding(
                                top = Dimens.small8 - 1.dp,
                                bottom = Dimens.extraSmall4,
                                start = Dimens.medium12,
                                end = Dimens.medium12 - filterModeButtonPadding,
                            )
                            .fillMaxWidth()
                    ) {
                        FilterMode.entries.forEach { filterMode ->
                            MenuButton(
                                modifier = Modifier.padding(end = filterModeButtonPadding)
                                    .weight(1f),
                                title = filterMode.title,
                                icon = FlagsMenu.FILTER.icon,
                                isSelected = filterMode == filterModeSelect,
                                buttonColorsSelect = buttonColorsSecondary,
                                buttonColorsUnselect = buttonColorsPrimary,
                                onClick = { filterModeSelect = filterMode },
                            )
                        }
                    }

                    /* Category / Country content */
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
                        when (targetFilterMode) {
                            FilterMode.COUNTRY -> {
                                CountryLazyList(
                                    modifier = lazyColumnModifier,
                                    listState = countriesListState,
                                    contentPadding = lazyColumnContentPadding,
                                    countries = filterCountries,
                                    selectedFlag = filterByCountry,
                                    onFlagSelect = onFilterByCountry,
                                    onMenuExpand = onMenuExpand,
                                )
                            }
                            FilterMode.CATEGORY -> {
                                CategoryLazyList(
                                    modifier = lazyColumnModifier,
                                    contentPadding = lazyColumnContentPadding,
                                    cardColorsPrimary = cardColorsPrimary,
                                    cardColorsSecondary = cardColorsSecondary,
                                    buttonColorsPrimary = buttonColorsPrimary,
                                    buttonColorsSecondary = buttonColorsSecondary,
                                    buttonColorsSubSelected = buttonColorsSubSelected,
                                    buttonColorsInCardSubSelected = buttonColorsInCardSubSelected,
                                    buttonColorsSuperInCardSelected =
                                        buttonColorsSuperInCardSelected,
                                    superCategories = superCategories,
                                    subCategories = subCategories,
                                    isMenuExpanded = isMenuExpanded,
                                    isSavedFlagsNotEmpty = isSavedFlagsNotEmpty,
                                    subMenuExpand = subMenuExpand,
                                    onSubMenuExpand = { subMenuExpand = it },
                                    onCategorySelectSingle = onCategorySelectSingle,
                                    onCategorySelectMultiple = onCategorySelectMultiple,
                                    onSavedFlagsSelect = onSavedFlagsSelect,
                                    onMenuExpand = onMenuExpand,
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
private fun FlagCounter(
    modifier: Modifier = Modifier,
    flagCount: Int,
    textColor: Color,
    backgroundColor: Color,
    isButtonOneLine: Boolean,
    onWidth: (Dp) -> Unit,
) {
    val density = LocalDensity.current
    val shape = if (isButtonOneLine) MaterialTheme.shapes.large else MaterialTheme.shapes.medium

    Box(
        modifier = modifier.onSizeChanged { size ->
            val width = with(receiver = density) { size.width.toDp() }
            onWidth(width)
        },
    ) {
        Text(
            text = flagCount.toString(),
            modifier = Modifier
                .requiredWidthIn(min = Dimens.iconSize24)
                .clip(shape = shape)
                .background(color = backgroundColor)
                .padding(vertical = 2.dp, horizontal = 6.dp),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
private fun CountryLazyList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    contentPadding: PaddingValues,
    countries: List<FlagView>,
    selectedFlag: FlagView?,
    onFlagSelect: (FlagView) -> Unit,
    onMenuExpand: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        /* Flag category items */
        items(
            count = countries.size,
            key = { index -> countries[index].id }
        ) { index ->
            val flag = countries[index]

            MenuFlagItem(
                flag = flag,
                selectedFlag = selectedFlag,
                menu = FlagsMenu.FILTER,
                isShowDates = false,
                onFlagSelect = { flag ->
                    val current = selectedFlag
                    onFlagSelect(flag)
                    if (flag != current) onMenuExpand()
                },
            )
        }
    }
}

@Composable
private fun CategoryLazyList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    cardColorsPrimary: CardColors,
    cardColorsSecondary: CardColors,
    buttonColorsPrimary: ButtonColors,
    buttonColorsSecondary: ButtonColors,
    buttonColorsSubSelected: ButtonColors,
    buttonColorsInCardSubSelected: ButtonColors,
    buttonColorsSuperInCardSelected: ButtonColors,
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    isMenuExpanded: Boolean,
    isSavedFlagsNotEmpty: Boolean,
    subMenuExpand: FlagSuperCategory?,
    onSubMenuExpand: (FlagSuperCategory?) -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
    onSavedFlagsSelect: () -> Unit,
    onMenuExpand: () -> Unit,
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        /* Saved flags item */
        if (isSavedFlagsNotEmpty) {
            item {
                MenuCategoryItem(
                    modifier = Modifier.clickable(
                        onClick = {
                            onSubMenuExpand(null)
                            onSavedFlagsSelect()
                            onMenuExpand()
                        }
                    ),
                    buttonColors =
                        if (superCategories.isEmpty() && subCategories.isEmpty()) {
                            buttonColorsSecondary
                        } else {
                            buttonColorsPrimary
                        },
                    fontWeight = FontWeight.Light,
                    category = null,
                )
            }
        }

        /* Flag category items */
        items(items = menuSuperCategoryList) { superCategory ->
            val isSelected = superCategory in superCategories

            val buttonColors = if (superCategory in superCategories) {
                buttonColorsSecondary
            } else if (superCategory != All &&
                (superCategory.allEnums().any { it in subCategories } ||
                        superCategory.supers().any { it in superCategories })) {
                buttonColorsSubSelected
            } else {
                buttonColorsPrimary
            }


            if (superCategory.enums().size == 1 || superCategory == All) {
                /* If superCategory has 1 subcategory use 1 tier (static) menu item */
                SuperItemStatic(
                    //buttonColors = buttonColors,
                    isSelected = isSelected,
                    superCategory = superCategory,
                    onCategorySelectSingle = { superCategory ->
                        onSubMenuExpand(null)
                        onCategorySelectSingle(superCategory)
                        onMenuExpand()
                    },
                    onCategorySelectMultiple = onCategorySelectMultiple,
                )
            } else if (superCategory.enums().isNotEmpty()) {
                /* If superCategory has multiple subcategories
                 * use 2 tier (expandable) menu item */
                SuperItemExpandable(
                    buttonColors = buttonColors,
                    buttonColorsChild = buttonColorsSecondary,
                    cardColors = cardColorsSecondary,
                    isCategoriesMenuExpanded = isMenuExpanded,
                    isSuperCategorySelectable = true,
                    itemSuperCategory = superCategory,
                    selectedSubCategories = subCategories,
                    isMenuExpandedParentState = superCategory == subMenuExpand,
                    onSuperItemSelect = { onSubMenuExpand(it) },
                    onCategorySelectSingle = {
                        onCategorySelectSingle(it)
                        onMenuExpand()
                    },
                    onCategorySelectMultiple = onCategorySelectMultiple,
                )
            } else {
                /* If superCategory only contains superCategories use 3 tier menu */
                SuperItemOfSupers(
                    buttonColorsSelectable = buttonColors,
                    buttonColorsDark = buttonColorsPrimary,
                    buttonColorsLight = buttonColorsSecondary,
                    buttonColorsInCardSubSelected = buttonColorsInCardSubSelected,
                    buttonColorsSuperInCardSelected =
                        buttonColorsSuperInCardSelected,
                    cardColorsDark = cardColorsPrimary,
                    cardColorsLight = cardColorsSecondary,
                    isCategoriesMenuExpanded = isMenuExpanded,
                    isSuperItemCardStyle = superCategory != Institution,
                    itemSuperCategory = superCategory,
                    selectedSuperCategories = superCategories,
                    selectedSubCategories = subCategories,
                    isMenuExpandedParentState = subMenuExpand,
                    onSuperItemExpand = { onSubMenuExpand(it) },
                    onCategorySelectSingle = {
                        onCategorySelectSingle(it)
                        onMenuExpand()
                    },
                    onCategorySelectMultiple = onCategorySelectMultiple,
                )
            }
        }
    }
}

@Composable
private fun SubItem(
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors,
    subCategory: FlagCategory,
    selectedSubCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    MenuCategoryItem(
        modifier = modifier.combinedClickable(
            onClick = { onCategorySelectSingle(subCategory.toWrapper()) },
            onLongClick = { onCategorySelectMultiple(subCategory.toWrapper()) },
        ),
        buttonColors = buttonColors,
        category = subCategory.toWrapper(),
    ) {
        if (subCategory in selectedSubCategories) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = textButtonEndPadding(layDir = LocalLayoutDirection.current))
                    .size(Dimens.iconSize24 * 0.9f),
                tint = buttonColors.contentColor,
            )
        }
    }
}

@Composable
private fun SuperItemStatic(
    modifier: Modifier = Modifier,
    //buttonColors: ButtonColors,
    isSelected: Boolean,
    superCategory: FlagSuperCategory,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val color = if (isSelected) FlagsMenu.FILTER.colorSelect() else FlagsMenu.FILTER.color()
    //val buttonColors = ButtonDefaults.buttonColors(containerColor = FlagsMenu.FILTER.color())
    val fontWeight = when (superCategory) {
        All -> FontWeight.ExtraBold
        else -> null
    }

    MenuCategoryItem(
        modifier = modifier.combinedClickable(
            onClick = { onCategorySelectSingle(superCategory) },
            onLongClick = { onCategorySelectMultiple(superCategory) },
        ),
        buttonColors = ButtonDefaults.buttonColors(containerColor = color),
        fontWeight = fontWeight,
        category = superCategory,
    )
}

@Composable
private fun SuperItemExpandable(
    buttonColors: ButtonColors,
    buttonColorsChild: ButtonColors,
    cardColors: CardColors,
    //isSelected: Boolean,
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
        MenuCategoryItem(
            modifier = Modifier.combinedClickable(
                onClick = {
                    onSuperItemSelect(null)
                    onCategorySelectSingle(itemSuperCategory)
                },
                onLongClick = {
                    onCategorySelectMultiple(itemSuperCategory)
                },
            ),
            buttonColors = buttonColors,
            category = itemSuperCategory,
        ) {
            /* Local state takes priority for icon shown */
            MenuItemExpandableArrowIcon(
                modifier = expandSuperClickableModifier,
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                tint = buttonColors.contentColor,
            )
        }
    } else {
        MenuCategoryItemCentred(
            modifier = expandSuperClickableModifier,
            buttonColors = buttonColors,
            category = itemSuperCategory,
        ) {
            /* Local state takes priority for icon shown */
            MenuItemExpandableArrowIcon(
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
                tint = buttonColors.contentColor,
            )
        }
    }

    /* Child content */
    MenuAnimatedVisibility(
        visible = isMenuExpandedLocalState ?: isMenuExpandedParentState
    ) {
        MenuItemCard(
            modifier = Modifier.padding(vertical = 2.25.dp),
            cardColors = cardColors,
        ) {
            subCategories.forEach { subCategory ->
                SubItem(
                    buttonColors = buttonColorsChild,
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
    buttonColorsSelectable: ButtonColors,
    buttonColorsDark: ButtonColors,
    buttonColorsLight: ButtonColors,
    buttonColorsInCardSubSelected: ButtonColors,
    buttonColorsSuperInCardSelected: ButtonColors,
    cardColorsDark: CardColors,
    cardColorsLight: CardColors,
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
            MenuCategoryItemCentred(
                modifier = expandSuperClickableModifier,
                buttonColors = buttonColorsLight,
                fontWeight = FontWeight.ExtraBold,
                category = itemSuperCategory,
            ) {
                MenuItemExpandableArrowIcon(
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                    tint = buttonColorsLight.contentColor,
                )
            }
        } else {
            MenuCategoryItem(
                modifier = Modifier.combinedClickable(
                    onClick = { onCategorySelectSingle(itemSuperCategory) },
                    onLongClick = { onCategorySelectMultiple(itemSuperCategory) },
                ),
                buttonColors = buttonColorsSelectable,
                category = itemSuperCategory,

            ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    modifier = expandSuperClickableModifier,
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
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
                        buttonColors = buttonColors,
                        buttonColorsChild = buttonColorsDark,
                        cardColors = cardColorsDark,
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