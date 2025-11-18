package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
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
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.OtherParameters
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.model.menu.filtermenu.FilterMode
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
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
    isSavedFlags: Boolean,
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
    val isFilterByCountry = filterByCountry != null
    val isFilterByCategory = !(superCategories.isNotEmpty() && superCategories.all { it == All } &&
            subCategories.isEmpty())

    /* Color properties */
    val nestLevel = 0 /* Sets the starting nest level to control colors */
    val menuButtonNestLevel = nestLevel.inc() /* For alternative color */
    val menuButtonColors = getNestedColors(nestLevel = menuButtonNestLevel, menu = FlagsMenu.FILTER)

    /* Button height properties */
    val fontScale = LocalConfiguration.current.fontScale
    val buttonHeightOneLine = Dimens.filterButtonHeight30 * fontScale
    val buttonHeightTwoLine = Dimens.filterButtonHeightTwoLine50 * fontScale
    var isButtonOneLine by remember { mutableStateOf(value = true) }
    var buttonHeight by remember { mutableStateOf(value = buttonHeightOneLine) }

    /* Manage button title & exceptions */
    val buttonTitle = buildString {
        getCategoriesTitleIds(superCategories, subCategories, filterByCountry).forEach { resId ->
            append(stringResource(resId))
        }
    }

    var flagCountWidth by remember { mutableStateOf(value = Dimens.iconSize24) }
    val filterModeButtonPadding = Dimens.small10 + 1.dp

    val lazyColumnModifier = Modifier.fillMaxWidth()
    val lazyColumnContentPadding = PaddingValues(bottom = Dimens.small10)

    /* Button height effects */
    LaunchedEffect(key1 = buttonHeight) {
        when (buttonHeight) {
            buttonHeightOneLine -> onButtonHeightChange(buttonHeightOneLine)
            buttonHeightTwoLine -> onButtonHeightChange(buttonHeightTwoLine)
        }
    }

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
                colors = menuButtonColors,
                contentPadding = PaddingValues(horizontal = Dimens.medium16),
            ) {
                MenuCategoryItemUnselectable(
                    nestLevel = menuButtonNestLevel,
                    isMenuButton = true,
                    buttonTitle = buttonTitle,
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
                    onClick = {}, /* Parent button handles onClick */
                    preTextContent = {
                        if (flagCount != null) {
                            FlagCounter(
                                parentColors = menuButtonColors,
                                isButtonOneLine = isButtonOneLine,
                                flagCount = flagCount,
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
                                nestLevel = menuButtonNestLevel,
                                isExpanded = isMenuExpanded,
                                isMenuButton = true,
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
                            val isFiltered = when (filterMode) {
                                FilterMode.CATEGORY -> isFilterByCategory
                                FilterMode.COUNTRY -> isFilterByCountry
                            }

                            MenuButton(
                                modifier = Modifier
                                    .padding(end = filterModeButtonPadding)
                                    .weight(1f),
                                menu = FlagsMenu.FILTER,
                                isSelected = filterMode == filterModeSelect,
                                isContentSelected = isFiltered,
                                isLongClickEnabled = isFiltered,
                                title = filterMode.title,
                                icon = FlagsMenu.FILTER.icon,
                                onClick = { filterModeSelect = filterMode },
                                onLongClick = {
                                    when (filterMode) {
                                        FilterMode.CATEGORY -> onCategorySelectSingle(All)
                                        FilterMode.COUNTRY ->
                                            filterByCountry?.let { onFilterByCountry(it) }
                                    }
                                },
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
                                    onMenuExpand = onMenuExpand,
                                    onFlagSelect = onFilterByCountry,
                                )
                            }
                            FilterMode.CATEGORY -> {
                                CategoryLazyList(
                                    modifier = lazyColumnModifier,
                                    contentPadding = lazyColumnContentPadding,
                                    nestLevel = nestLevel,
                                    isMenuExpanded = isMenuExpanded,
                                    subMenuExpand = subMenuExpand,
                                    isSavedFlags = isSavedFlags,
                                    isFilterByCountry = isFilterByCountry,
                                    superCategories = superCategories,
                                    subCategories = subCategories,
                                    onMenuExpand = onMenuExpand,
                                    onSubMenuExpand = { subMenuExpand = it },
                                    onSavedFlagsSelect = onSavedFlagsSelect,
                                    onCategorySelectSingle = onCategorySelectSingle,
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
private fun FlagCounter(
    modifier: Modifier = Modifier,
    parentColors: ButtonColors,
    isButtonOneLine: Boolean,
    flagCount: Int,
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
                .background(color = parentColors.contentColor)
                .padding(vertical = 2.dp, horizontal = 6.dp),
            textAlign = TextAlign.Center,
            color = parentColors.containerColor,
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
    onMenuExpand: () -> Unit,
    onFlagSelect: (FlagView) -> Unit,
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
    nestLevel: Int,
    isMenuExpanded: Boolean,
    subMenuExpand: FlagSuperCategory?,
    isSavedFlags: Boolean,
    isFilterByCountry: Boolean,
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    onMenuExpand: () -> Unit,
    onSubMenuExpand: (FlagSuperCategory?) -> Unit,
    onSavedFlagsSelect: () -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        /* Saved flags item */
        if (isSavedFlags) {
            item {
                MenuCategoryItemSelectable(
                    nestLevel = nestLevel,
                    isSelected = superCategories.isEmpty() && subCategories.isEmpty(),
                    fontWeight = FontWeight.Normal,
                    category = null,
                    onClick = {
                        onSubMenuExpand(null)
                        onSavedFlagsSelect()
                        onMenuExpand()
                    },
                    onLongClick = {} // TODO
                )
            }
        }

        /* Flag category items */
        items(items = menuSuperCategoryList) { superCategory ->
            val isSelected = superCategory in superCategories
            val isChildSelected = superCategory.allEnums().any { it in subCategories } ||
                    superCategory.supers().any { it in superCategories }

            val isEnabled = !(isFilterByCountry && superCategory == SovereignCountry)

            val isItemNested = superCategory == Political
            val tier3MenuTopPadding = if (isItemNested) Dimens.extraSmall4 else 0.dp

            if (superCategory.enums().size == 1 || superCategory == All) {
                /* If superCategory has 1 subcategory use 1 tier (static) menu item */
                SuperItemStatic(
                    nestLevel = nestLevel,
                    isEnabled = isEnabled,
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
                    nestLevel = nestLevel,
                    isSelected = isSelected,
                    isChildSelected = isChildSelected,
                    isCategoriesMenuExpanded = isMenuExpanded,
                    isSuperCategorySelectable = superCategory != OtherParameters,
                    itemSuperCategory = superCategory,
                    subCategoriesSelected = subCategories,
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
                    modifier = Modifier.padding(top = tier3MenuTopPadding),
                    nestLevel = nestLevel,
                    isSelected = isSelected,
                    isChildSelected = isChildSelected,
                    isCategoriesMenuExpanded = isMenuExpanded,
                    isItemNested = isItemNested,
                    isSuperSelectable = superCategory != Political,
                    itemSuperCategory = superCategory,
                    superCategoriesSelected = superCategories,
                    subCategoriesSelected = subCategories,
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
    nestLevel: Int,
    subCategory: FlagCategory,
    selectedSubCategories: List<FlagCategory>,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    MenuCategoryItemSelectable(
        modifier = modifier,
        nestLevel = nestLevel,
        fontWeight = FontWeight.Normal,
        category = subCategory.toWrapper(),
        onClick = { onCategorySelectSingle(subCategory.toWrapper()) },
        onLongClick = { onCategorySelectMultiple(subCategory.toWrapper()) },
    ) {
        if (subCategory in selectedSubCategories) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = textButtonEndPadding(layDir = LocalLayoutDirection.current))
                    .size(Dimens.iconSize24 * 0.9f),
                tint = getNestedColors(nestLevel).contentColor,
            )
        }
    }
}

@Composable
private fun SuperItemStatic(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isEnabled: Boolean = true,
    isSelected: Boolean,
    superCategory: FlagSuperCategory,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val fontWeight = when (superCategory) {
        All -> FontWeight.ExtraBold
        else -> null
    }

    MenuCategoryItemSelectable(
        modifier = modifier,
        nestLevel = nestLevel,
        isEnabled = isEnabled,
        isSelected = isSelected,
        fontWeight = fontWeight,
        category = superCategory,
        onClick = { onCategorySelectSingle(superCategory) },
        onLongClick = { onCategorySelectMultiple(superCategory) },
    )
}

@Composable
private fun SuperItemExpandable(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isSelected: Boolean,
    isChildSelected: Boolean,
    isCategoriesMenuExpanded: Boolean,
    isSuperCategorySelectable: Boolean,
    itemSuperCategory: FlagSuperCategory,
    subCategoriesSelected: List<FlagCategory>,
    isMenuExpandedParentState: Boolean,
    onSuperItemSelect: (FlagSuperCategory?) -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
) {
    val subCategories = itemSuperCategory.enums()

    /* Per-item boolean for holding menu expansion state, eg. when any of it's subs are selected */
    var isMenuExpandedLocalState: Boolean? by remember { mutableStateOf(value = null) }

    /* Bottom padding when last item is expandable and expanded */
    val lastItems = listOf(Political.subCategories.last(), Institution.subCategories.last())
    val lastItemPadding = if (itemSuperCategory in lastItems &&
        (isMenuExpandedParentState || isMenuExpandedLocalState == true)) 9.dp else 0.dp

    val onExpandClick = {
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

    val childNestLevel = nestLevel.inc()

    /* When the categories menu is open, upon subcategory change or categories menu open
     * expand the local menu if any of it's subcategories are selected */
    LaunchedEffect(
        key1 = subCategoriesSelected,
        key2 = isCategoriesMenuExpanded,
    ) {
        if (isCategoriesMenuExpanded) {
            isMenuExpandedLocalState =
                if (subCategoriesSelected.any { it in itemSuperCategory.enums() }) true else null
        }
    }


    /* Parent content */
    if (isSuperCategorySelectable) {
        MenuCategoryItemSelectable(
            modifier = modifier,
            nestLevel = nestLevel,
            isSelected = isSelected,
            isChildSelected = isChildSelected,
            category = itemSuperCategory,
            onClick = {
                onSuperItemSelect(null)
                onCategorySelectSingle(itemSuperCategory)
            },
            onLongClick = {
                onCategorySelectMultiple(itemSuperCategory)
            },
        ) {
            /* Local state takes priority for icon shown */
            MenuItemExpandableArrowIcon(
                modifier = Modifier.clickable(onClick = onExpandClick),
                nestLevel = nestLevel,
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
            )
        }
    } else {
        MenuCategoryItemUnselectable(
            nestLevel = nestLevel,
            isChildSelected = isChildSelected,
            category = itemSuperCategory,
            onClick = onExpandClick,
        ) {
            /* Local state takes priority for icon shown */
            MenuItemExpandableArrowIcon(
                nestLevel = nestLevel,
                isExpanded = isMenuExpandedLocalState ?: isMenuExpandedParentState,
            )
        }
    }

    /* Child content */
    MenuAnimatedVisibility(
        visible = isMenuExpandedLocalState ?: isMenuExpandedParentState
    ) {
        MenuCategoryItemCard(
            modifier = Modifier.padding(vertical = 2.25.dp),
            nestLevel = childNestLevel,
        ) {
            subCategories.forEach { subCategory ->
                SubItem(
                    nestLevel = childNestLevel,
                    subCategory = subCategory,
                    selectedSubCategories = subCategoriesSelected,
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
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isNested: Boolean,
    content: @Composable () -> Unit,
) {
    if (isNested) {
        MenuCategoryItemCard(
            modifier = modifier,
            nestLevel = nestLevel,
            innerPadding = 0.dp,
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun SuperItemOfSupers(
    modifier: Modifier = Modifier,
    nestLevel: Int,
    isSelected: Boolean,
    isChildSelected: Boolean,
    isCategoriesMenuExpanded: Boolean,
    isItemNested: Boolean,
    isSuperSelectable: Boolean,
    itemSuperCategory: FlagSuperCategory,
    superCategoriesSelected: List<FlagSuperCategory>,
    subCategoriesSelected: List<FlagCategory>,
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

    val onExpandClick = {
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

    val parentNestLevel = if (isItemNested) nestLevel.inc() else nestLevel
    val childNestLevel = nestLevel.inc()

    /* When the categories menu is open, upon subcategory change or categories menu open
     * expand the local menu if any of it's subcategories are selected */
    LaunchedEffect(
        key1 = subCategoriesSelected,
        key2 = isCategoriesMenuExpanded,
    ) {
        if (isCategoriesMenuExpanded) {
            isMenuExpandedLocalState =
                if ((subCategoriesSelected.any { subCat ->
                        itemSuperCategory.supers().any { superCat ->
                            subCat in superCat.enums() } }) ||
                    itemSuperCategory.supers().any { it in superCategoriesSelected })
                    true else null
        }
    }

    /* Content */
    SuperItemsContainer(
        modifier = modifier,
        nestLevel = parentNestLevel,
        isNested = isItemNested,
    ) {
        /* Parent content */
        if (isSuperSelectable) {
            MenuCategoryItemSelectable(
                nestLevel = parentNestLevel,
                isSelected = isSelected,
                isChildSelected = isChildSelected,
                category = itemSuperCategory,
                onClick = { onCategorySelectSingle(itemSuperCategory) },
                onLongClick = { onCategorySelectMultiple(itemSuperCategory) },
                ) {
                /* Local state takes priority for icon shown */
                MenuItemExpandableArrowIcon(
                    modifier = Modifier.clickable(onClick = onExpandClick),
                    nestLevel = parentNestLevel,
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                )
            }
        } else {
            MenuCategoryItemUnselectable(
                nestLevel = parentNestLevel,
                fontWeight = FontWeight.ExtraBold,
                category = itemSuperCategory,
                onClick = onExpandClick,
            ) {
                MenuItemExpandableArrowIcon(
                    nestLevel = parentNestLevel,
                    isExpanded = isMenuExpandedLocalState ?: isSuperItemExpanded,
                )
            }
        }

        /* Child content */
        MenuAnimatedVisibility(
            visible = isMenuExpandedLocalState ?: isSuperItemExpanded
        ) {
            MenuCategoryItemCard(
                nestLevel = childNestLevel,
                isNested = !isItemNested,
                shape = if (isItemNested) RoundedCornerShape(size = 0.dp) else null,
                innerPadding = 0.dp,
            ) {
                itemSuperCategory.supers().forEach { superCategory ->
                    val isSuperSelected = superCategory in superCategoriesSelected
                    val isSuperChildSelected =
                        superCategory.enums().any { it in subCategoriesSelected }

                    SuperItemExpandable(
                        nestLevel = childNestLevel,
                        isSelected = isSuperSelected,
                        isChildSelected = isSuperChildSelected,
                        isCategoriesMenuExpanded = isCategoriesMenuExpanded,
                        isSuperCategorySelectable = superCategory !in Political.supers(),
                        itemSuperCategory = superCategory,
                        subCategoriesSelected = subCategoriesSelected,
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