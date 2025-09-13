package dev.aftly.flags.ui.screen.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.ui.component.CategoriesButtonMenu
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.ResultsType
import dev.aftly.flags.ui.component.ScrollToTopButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.flagDatesString
import dev.aftly.flags.ui.util.getFlagFromId
import dev.aftly.flags.ui.util.getSavedFlagView
import kotlinx.coroutines.launch


/* Displays list of flags. Upon flag select, pass up it's navArg and navigate to FlagScreen() */
@Composable
fun ListFlagsScreen(
    viewModel: ListFlagsViewModel = viewModel(),
    currentBackStackEntry: NavBackStackEntry?,
    onDrawerNavigateToList: Boolean,
    onResetDrawerNavigateToList: () -> Unit,
    onNavigationDrawer: () -> Unit,
    onNavigateToFlagScreen: (FlagView, List<FlagView>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val savedFlags = viewModel.sortFlagsAlphabetically(
        flags = getSavedFlagView(uiState.savedFlags)
    )

    /* Update (alphabetical) order of flag lists when language changes */
    //val configuration = LocalConfiguration.current
    //val locale = configuration.locales[0]
    /* LaunchedEffect(locale) {
        viewModel.sortFlagsAlphabetically()
        searchModel.sortFlagsAlphabetically()
    } */

    ListFlagsScreen(
        uiState = uiState,
        currentBackStackEntry = currentBackStackEntry,
        onDrawerNavigateToList = onDrawerNavigateToList,
        onResetDrawerNavigateToList = onResetDrawerNavigateToList,
        searchResults = searchResults,
        savedFlags = savedFlags,
        searchQueryValue = viewModel.searchQueryValue,
        onSearchQueryValueChange = { viewModel.onSearchQueryValueChange(it) },
        onIsSearchBarInit = { viewModel.toggleIsSearchBarInit(it) },
        onIsSearchBarInitTopBar = { viewModel.toggleIsSearchBarInitTopBar(it) },
        onNavigationDrawer = onNavigationDrawer,
        onCategorySelectSingle = { superCategory, subCategory ->
            viewModel.updateCurrentCategory(superCategory, subCategory)
        },
        onCategorySelectMultiple = { superCategory, subCategory ->
            viewModel.updateCurrentCategories(superCategory, subCategory)
        },
        onSavedFlagsSelect = { viewModel.selectSavedFlags(on = it) },
        onSaveFlag = { viewModel.onSaveFlag(flag = it) },
        onResetScreen = { viewModel.resetScreen() },
        onFlagSelect = { flag ->
            val flags =
                if (uiState.isSearchQuery) searchResults
                else if (uiState.isViewSavedFlags) savedFlags
                else uiState.currentFlags

            onNavigateToFlagScreen(flag, flags)
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsScreen(
    modifier: Modifier = Modifier,
    uiState: ListFlagsUiState,
    currentBackStackEntry: NavBackStackEntry?,
    onDrawerNavigateToList: Boolean,
    onResetDrawerNavigateToList: () -> Unit,
    searchResults: List<FlagView>,
    savedFlags: List<FlagView>,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    searchQueryValue: TextFieldValue,
    onSearchQueryValueChange: (TextFieldValue) -> Unit,
    onIsSearchBarInit: (Boolean) -> Unit,
    onIsSearchBarInitTopBar: (Boolean) -> Unit,
    onNavigationDrawer: () -> Unit,
    onCategorySelectSingle: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategorySelectMultiple: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onSavedFlagsSelect: (Boolean) -> Unit,
    onSaveFlag: (FlagView) -> Unit,
    onResetScreen: () -> Unit,
    onFlagSelect: (FlagView) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion amd tracks current button height
     * Also for FilterFlagsButton to access Scaffold() padding */
    var isMenuExpanded by rememberSaveable { mutableStateOf(value = false) }
    var buttonHeight by remember { mutableStateOf(0.dp) }
    var scaffoldPaddingValues by remember { mutableStateOf(value = PaddingValues()) }

    /* Properties for ScrollToTopButton & reset scroll position when category changed in menu */
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    /* Controls search bar and list state */
    val focusManager = LocalFocusManager.current
    var isSearchBarFocused by rememberSaveable { mutableStateOf(value = false) }
    var isSearchBar by rememberSaveable { mutableStateOf(value = false) }

    /* For expandable/collapsable Scaffold TopBar */
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    /* Properties for save item mode */
    var isSaveMode by rememberSaveable { mutableStateOf(value = false) }


    /* Trigger navigation effects (eg. scroll to flag from FlagScreen if present) */
    LaunchedEffect(key1 = currentBackStackEntry) {
        currentBackStackEntry?.savedStateHandle?.get<Int>(key = "scrollToFlagId")?.let { flagId ->
            if (flagId > 0) {
                val flag = getFlagFromId(flagId)
                val flags =
                    if (uiState.isSearchQuery) searchResults
                    else if (uiState.isViewSavedFlags) savedFlags
                    else uiState.currentFlags

                /* If not returning to saved flags from a removed saved flag */
                if (!(uiState.isViewSavedFlags && flag !in savedFlags)) {
                    val index = flags.indexOf(flag)
                    coroutineScope.launch { listState.scrollToItem(index = index) }
                }
            }
            currentBackStackEntry.savedStateHandle.set(key = "scrollToFlagId", value = 0)
        }
    }

    LaunchedEffect(onDrawerNavigateToList) {
        if (onDrawerNavigateToList) {
            val isOnlySovereign = uiState.currentSuperCategories
                .none { it != SovereignCountry } && uiState.currentSubCategories.isEmpty()

            if (!isOnlySovereign) onResetScreen()
            else coroutineScope.launch { listState.animateScrollToItem(index = 0) }

            onResetDrawerNavigateToList()
        }
    }

    LaunchedEffect(key1 = isMenuExpanded) {
        if (isMenuExpanded) focusManager.clearFocus()
    }

    LaunchedEffect(key1 = searchQueryValue.text) {
        /* Minimize filter menu when keyboard input */
        if (uiState.isSearchQuery && isMenuExpanded) {
            isMenuExpanded = false
        }
    }

    /* Handle isSearchBar effects */
    LaunchedEffect(key1 = isSearchBar) {
        if (!isSearchBar) {
            onIsSearchBarInit(false)
            onIsSearchBarInitTopBar(false)
            onSearchQueryValueChange(TextFieldValue())

        } else if (!uiState.isSearchBarInit) {
            /* If saved flags or not All super category */
            if (uiState.isViewSavedFlags || !(uiState.currentSuperCategories.all { it == All } &&
                        uiState.currentSubCategories.isEmpty())) {
                onCategorySelectSingle(All, null)
                onSavedFlagsSelect(false)
                if (!isAtTop) {
                    coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                }
            }
            onIsSearchBarInit(true)
        }
    }

    /* If returning from FlagScreen to SavedFlags and SavedFlags isEmpty() select All category */
    LaunchedEffect(key1 = uiState.savedFlags) {
        if (uiState.isViewSavedFlags && uiState.savedFlags.isEmpty()) {
            onCategorySelectSingle(All, null)
        }
    }

    LaunchedEffect(key1 = searchResults) {
        /* Get value from savedStateHandle and reset it if present */
        val isNavigatedBack =
            currentBackStackEntry?.savedStateHandle?.get<Boolean>(key = "isNavigateBack") ?: false

        if (uiState.isSearchQuery && !isNavigatedBack) {
            coroutineScope.launch { listState.scrollToItem(index = 0) }
        } else if (!isNavigatedBack) {
            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
        } else {
            currentBackStackEntry.savedStateHandle.set(key = "isNavigateBack", value = false)
        }
    }


    /* Scaffold within box so FilterFlagsButton & it's associated surface can overlay it */
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd,
    ) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                        isSearchBarFocused = false
                    }
                },
            topBar = {
                ListFlagsTopBar(
                    scrollBehaviour = scrollBehaviour,
                    searchQuery = searchQueryValue,
                    isSearchQuery = uiState.isSearchQuery,
                    onSearchQueryValueChange = onSearchQueryValueChange,
                    onFocus = { isSearchBarFocused = true },
                    onKeyboardDismiss = {
                        focusManager.clearFocus()
                        isSearchBarFocused = false
                    },
                    isMenuExpanded = isMenuExpanded,
                    isSearchBarFocused = isSearchBarFocused,
                    isSearchBarInit = uiState.isSearchBarInitTopBar,
                    isSearchBar = isSearchBar,
                    onIsSearchBar = { isSearchBar = it },
                    onIsSearchBarInitTopBar = onIsSearchBarInitTopBar,
                    isSaveMode = isSaveMode,
                    onSaveAction = { isSaveMode = !isSaveMode },
                    onNavigationDrawer = {
                        focusManager.clearFocus()
                        onNavigationDrawer()
                    },
                )
            },
            floatingActionButton = {
                ScrollToTopButton(
                    isVisible = !isAtTop,
                    containerColor = containerColor2,
                    onClick = {
                        coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                    }
                )
            }
        ) { scaffoldPadding ->
            scaffoldPaddingValues = scaffoldPadding

            ListFlagsContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = scaffoldPadding.calculateTopPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16,
                    ),
                filterButtonHeight = buttonHeight,
                scaffoldBottomPadding = scaffoldPadding.calculateBottomPadding(),
                scrollBehaviour = scrollBehaviour,
                listState = listState,
                isSaveMode = isSaveMode,
                isSearchBar = isSearchBar,
                isSearchQuery = uiState.isSearchQuery,
                searchResults = searchResults,
                flags = if (uiState.isViewSavedFlags) savedFlags else uiState.currentFlags,
                savedFlags = savedFlags,
                onSearchQueryReplace = { flagOf ->
                    /* Replace searchQueryValue with item name and cursor at end */
                    onSearchQueryValueChange(
                        TextFieldValue(
                            text = flagOf,
                            selection = TextRange(index = flagOf.length)
                        )
                    )
                },
                onSaveFlag = onSaveFlag,
                onFlagSelect = {
                    focusManager.clearFocus()
                    onFlagSelect(it)
                },
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        CategoriesButtonMenu(
            modifier = Modifier.fillMaxSize(),
            scaffoldPadding = scaffoldPaddingValues,
            buttonHorizontalPadding = Dimens.marginHorizontal16,
            flagCount =
                if (uiState.isSearchQuery) searchResults.size
                else if (uiState.isViewSavedFlags) uiState.savedFlags.size
                else uiState.currentFlags.size,
            onButtonHeightChange = { buttonHeight = it },
            isMenuExpanded = isMenuExpanded,
            onMenuButtonClick = { isMenuExpanded = !isMenuExpanded },
            containerColor1 = containerColor1,
            containerColor2 = containerColor2,
            isSavedFlagsNotEmpty = uiState.savedFlags.isNotEmpty(),
            superCategories = uiState.currentSuperCategories,
            subCategories = uiState.currentSubCategories,
            onCategorySelectSingle = { flagSuperCategory, flagSubCategory ->
                onCategorySelectSingle(flagSuperCategory, flagSubCategory)
                coroutineScope.launch { listState.animateScrollToItem(index = 0) }
            },
            onCategorySelectMultiple = { flagSuperCategory, flagSubCategory ->
                onCategorySelectMultiple(flagSuperCategory, flagSubCategory)
                coroutineScope.launch { listState.animateScrollToItem(index = 0) }
            },
            onSavedFlagsSelect = {
                onSavedFlagsSelect(true)
                coroutineScope.launch { listState.animateScrollToItem(index = 0) }
            },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsContent(
    modifier: Modifier = Modifier,
    filterButtonHeight: Dp,
    scaffoldBottomPadding: Dp,
    scrollBehaviour: TopAppBarScrollBehavior,
    listState: LazyListState,
    isSaveMode: Boolean,
    isSearchBar: Boolean,
    isSearchQuery: Boolean,
    searchResults: List<FlagView>,
    flags: List<FlagView>,
    savedFlags: List<FlagView>,
    onSearchQueryReplace: (String) -> Unit,
    onSaveFlag: (FlagView) -> Unit,
    onFlagSelect: (FlagView) -> Unit,
) {
    val listItemVerticalPadding = Dimens.small8
    val savedFlagsSet = savedFlags.toSet()

    Column(modifier = modifier) {
        /* To make LazyColumn scroll disappear into center of FilterFlagsButton */
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(filterButtonHeight / 2)
        )

        when (isSearchQuery) {
            false -> {
                if (flags.isNotEmpty()) {
                    /* Flags list */
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehaviour.nestedScrollConnection),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = filterButtonHeight / 2 + listItemVerticalPadding,
                            bottom = scaffoldBottomPadding,
                        ),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        items(
                            count = flags.size,
                            key = { index -> flags[index].id }
                        ) { index ->
                            val flag = flags[index]

                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                verticalPadding = listItemVerticalPadding,
                                flag = flag,
                                isSaveMode = isSaveMode,
                                isSavedItem = savedFlagsSet.contains(flag),
                                isSearch = isSearchBar,
                                onSearchQueryReplace = onSearchQueryReplace,
                                onSaveFlag = onSaveFlag,
                                onFlagSelect = onFlagSelect,
                            )
                        }
                    }
                } else {
                    NoResultsFound(
                        modifier = Modifier.fillMaxSize(),
                        resultsType = ResultsType.CATEGORIES,
                        bottomSpacer = true,
                    )
                }
            }
            true -> {
                if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehaviour.nestedScrollConnection),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = filterButtonHeight / 2 + listItemVerticalPadding,
                            bottom = scaffoldBottomPadding,
                        ),
                    ) {
                        items(
                            count = searchResults.size,
                            key = { index -> searchResults[index].id }
                        ) { index ->
                            val flag = searchResults[index]

                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                verticalPadding = listItemVerticalPadding,
                                flag = flag,
                                isSaveMode = isSaveMode,
                                isSavedItem = savedFlagsSet.contains(flag),
                                isSearch = isSearchBar,
                                onSearchQueryReplace = onSearchQueryReplace,
                                onSaveFlag = onSaveFlag,
                                onFlagSelect = onFlagSelect,
                            )
                        }
                    }
                } else {
                    NoResultsFound(
                        modifier = Modifier.fillMaxSize(),
                        resultsType = ResultsType.SEARCH,
                        bottomSpacer = true,
                    )
                }
            }
        }
    }
}


@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    verticalPadding: Dp,
    flag: FlagView,
    isSaveMode: Boolean,
    isSavedItem: Boolean,
    isSearch: Boolean,
    onSearchQueryReplace: (String) -> Unit,
    onSaveFlag: (FlagView) -> Unit,
    onFlagSelect: (FlagView) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale
    val contentPadding = Dimens.extraSmall4
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    /* Item strings */
    val flagOf = stringResource(flag.flagOf)
    val descriptor = buildString {
        flag.flagOfDescriptor?.let {
            append(stringResource(R.string.string_whitespace))
            append(stringResource(it))
        }
    }
    val fromToYear = buildString {
        if (flag.previousFlagOfKey != null && flag.isDated) {
            append(stringResource(R.string.string_whitespace))
            append(flagDatesString(flag))
        }
    }

    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val saveFlagIcon = if (isSavedItem) Icons.Default.Check else Icons.Default.Add

    Row(
        modifier = modifier.padding(bottom = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(shape = MaterialTheme.shapes.large)
                .indication(interactionSource, ripple())
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            val press = PressInteraction.Press(offset)
                            interactionSource.emit(press)

                            val released = tryAwaitRelease()
                            val end =
                                if (released) PressInteraction.Release(press)
                                else PressInteraction.Cancel(press)
                            interactionSource.emit(end)
                        },
                        onTap = { onFlagSelect(flag) },
                        onLongPress = {
                            if (isSearch) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSearchQueryReplace(flagOf)
                            }
                        },
                    )
                },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
        ) {
            Row(
                modifier = modifier.padding(
                    vertical = verticalPadding + contentPadding,
                    horizontal = Dimens.small10 + contentPadding,
                ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                /* weight() uses available space after space taken by non-weighted children */
                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            /* Separate text from image */
                            .padding(end = Dimens.small8)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = flagOf,
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                        )
                        if (descriptor.isNotEmpty()) {
                            Text(
                                text = descriptor,
                                fontWeight = FontWeight.Light,
                                color = textColor,
                            )
                        }
                        if (fromToYear.isNotEmpty()) {
                            Text(
                                text = fromToYear,
                                modifier = Modifier.padding(top = 1.dp),
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Gray,
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    /* Limit image height to dynamicHeight minus item padding */
                    modifier = Modifier.height(dynamicHeight - verticalPadding * 2),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        AnimatedVisibility(
            visible = isSaveMode,
            enter = scaleIn(animationSpec = tween(durationMillis = Timing.SCALE_IN)),
            exit = ExitTransition.None,
        ) {
            IconButton(
                onClick = { onSaveFlag(flag) },
                modifier = Modifier
                    .padding(start = Dimens.marginHorizontal16 - Dimens.extraSmall4)
                    .width(Dimens.standardIconSize24 + Dimens.extraSmall4),
            ) {
                Icon(
                    imageVector = saveFlagIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsTopBar(
    modifier: Modifier = Modifier,
    scrollBehaviour: TopAppBarScrollBehavior,
    searchQuery: TextFieldValue,
    isSearchQuery: Boolean,
    onSearchQueryValueChange: (TextFieldValue) -> Unit,
    onFocus: () -> Unit,
    onKeyboardDismiss: () -> Unit,
    isMenuExpanded: Boolean,
    isSearchBarFocused: Boolean,
    isSearchBarInit: Boolean,
    isSearchBar: Boolean,
    onIsSearchBar: (Boolean) -> Unit,
    onIsSearchBarInitTopBar: (Boolean) -> Unit,
    isSaveMode: Boolean,
    onSaveAction: () -> Unit,
    onNavigationDrawer: () -> Unit,
) {
    val annotatedTitle = buildAnnotatedString {
        val title = stringResource(R.string.flags_of_the_world)
        val flags = stringResource(R.string.flags)
        val whitespace = stringResource(R.string.string_whitespace)
        val words: List<String> = title.split(whitespace)

        words.forEachIndexed { index, word ->
            if (word.contains(flags, ignoreCase = true)) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(text = word)
                }
            } else {
                append(text = word)
            }
            if (index < words.size - 1) append(text = whitespace)
        }
    }

    /* Manage TitleStyle transition between expanded (start) and collapsed (stop) TopAppBar title */
    val titleStyle = lerp(
        start = MaterialTheme.typography.headlineLarge,
        stop = MaterialTheme.typography.headlineSmall,
        fraction = scrollBehaviour.state.collapsedFraction
    )

    /* Manage transition of title from center to start position via box weight between
     * expanded (start) and collapsed (stop) TopAppBar */
    val boxWeight = lerp(
        start = 1f,
        stop = 0.001f,
        fraction = scrollBehaviour.state.collapsedFraction
    )

    /* For search bar size to respect navigationIcon & other */
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    var navigationIconOffsetX by remember { mutableFloatStateOf(value = 0f) }
    var navigationIconWidth by remember { mutableIntStateOf(value = 0) }
    val textFieldOffset = 4.dp
    val textFieldStartPadding = with(density) {
        (navigationIconOffsetX + navigationIconWidth).toDp() + textFieldOffset
    }

    /* Make top bar title visible when below search bar and when no search bar */
    val isTopBarTitle = if (scrollBehaviour.state.collapsedFraction < 0.5f) true else !isSearchBar

    /* Other action properties */
    val saveModeIcon = if (isSaveMode) Icons.Default.Close else Icons.Default.Save
    val searchIconEndPadding = TextFieldDefaults.contentPaddingWithoutLabel()
        .calculateRightPadding(layoutDirection = LocalLayoutDirection.current) - textFieldOffset


    MediumTopAppBar(
        title = {
            AnimatedVisibility(
                visible = isTopBarTitle,
                enter = expandHorizontally(
                    animationSpec = tween(durationMillis = Timing.MENU_EXPAND * 2),
                    expandFrom = Alignment.Start,
                ),
                exit = ExitTransition.None,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(modifier = Modifier
                        .size(0.dp)
                        .weight(boxWeight)
                    )

                    Text(
                        text = annotatedTitle,
                        style = titleStyle,
                    )

                    Box(modifier = Modifier
                        .size(0.dp)
                        .weight(1f)
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onNavigationDrawer,
                modifier = Modifier.onGloballyPositioned { layout ->
                    navigationIconOffsetX = layout.positionInParent().x
                    navigationIconWidth = layout.size.width
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_drawer_content_description),
                )
            }
        },
        actions = {
            /* Search bar text field */
            if (isSearchBar) {
                val focusRequesterSearch = remember { FocusRequester() }

                /* Focus search bar on nav back */
                LaunchedEffect(Unit) {
                    if (isSearchBarFocused) {
                        focusRequesterSearch.requestFocus()
                    }
                }
                /* Focus search bar on initial visibility */
                if (!isSearchBarInit) {
                    LaunchedEffect(Unit) {
                        focusRequesterSearch.requestFocus()
                        onIsSearchBarInitTopBar(true)
                    }
                }
                /* Focus search bar on menu collapse if previously focused */
                LaunchedEffect(isMenuExpanded) {
                    if (!isMenuExpanded && isSearchBarFocused) {
                        focusRequesterSearch.requestFocus()
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    /* Background for TextField as clipping issues when setting size directly */
                    Surface(
                        modifier = Modifier
                            .offset(y = textFieldOffset)
                            .fillMaxWidth()
                            .padding(start = textFieldStartPadding)
                            .height(48.dp * configuration.fontScale),
                        shape = TextFieldDefaults.shape,
                        color = TextFieldDefaults.colors().focusedContainerColor
                    ) { }

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = textFieldStartPadding)
                            .onFocusChanged {
                                if (it.isFocused) onFocus()
                            }
                            .focusRequester(focusRequesterSearch),
                        singleLine = true,
                        placeholder = {
                            Text(text = stringResource(R.string.search_bar_placeholder))
                        },
                        trailingIcon = {
                            Box(
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                /* Use box to place clear and search buttons closer together
                                 * due to internal IconButton padding */
                                Row {
                                    AnimatedVisibility(
                                        visible = isSearchQuery,
                                        enter = scaleIn(
                                            animationSpec = tween(
                                                durationMillis = Timing.TEXTFIELD_ACTION
                                            ),
                                        ),
                                        exit = scaleOut(
                                            animationSpec = tween(
                                                durationMillis = Timing.TEXTFIELD_ACTION
                                            ),
                                        ),
                                    ) {
                                        /* Clear search action button */
                                        IconButton(
                                            onClick = { onSearchQueryValueChange(TextFieldValue()) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription =
                                                    stringResource(R.string.search_clear),
                                                tint = MaterialTheme.colorScheme.onSurface,
                                            )
                                        }
                                    }
                                    /* Spacer enables centred animation of above button */
                                    Spacer(modifier = Modifier
                                        .width(Dimens.standardIconSize24 * 1.75f)
                                    )
                                }

                                /* Disable search bar action button */
                                IconButton(
                                    onClick = { onIsSearchBar(false) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onDone = { onKeyboardDismiss() }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )
                }
            } else {
                /* Search bar action button */
                IconButton(
                    onClick = { onIsSearchBar(true) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                }
            }

            /* Save mode action button */
            IconButton(
                onClick = onSaveAction,
                modifier = Modifier.padding(end = Dimens.small8),
            ) {
                Icon(
                    imageVector = saveModeIcon,
                    contentDescription = null,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehaviour,
    )
}


// Preview screen in Android Studio
/*
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun ListFlagsScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        ListFlagsScreen(
            currentScreen = Screen.List,
            canNavigateBack = true,
            onNavigateUp = {},
            onNavigateDetails = {}
        )
    }
}
 */