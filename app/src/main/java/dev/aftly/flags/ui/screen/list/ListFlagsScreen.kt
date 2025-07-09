package dev.aftly.flags.ui.screen.list

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.navigation.Screen
//import dev.aftly.flags.ui.component.CategoriesButtonMenu TODO
import dev.aftly.flags.ui.component.CategoriesButtonMenu2
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.ResultsType
import dev.aftly.flags.ui.component.ScrollToTopButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/* Displays list of flags. Upon flag select, pass up it's navArg and navigate to FlagScreen() */
@Composable
fun ListFlagsScreen(
    viewModel: ListFlagsViewModel = viewModel(),
    searchModel: SearchViewModel = viewModel(),
    screen: Screen,
    onNavigateUp: () -> Unit,
    onNavigateDetails: (Int, List<Int>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchState by searchModel.searchResults.collectAsStateWithLifecycle()

    /* Update (alphabetical) order of flag lists when language changes */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    /*
    LaunchedEffect(locale) {
        viewModel.sortFlagsAlphabetically()
        searchModel.sortFlagsAlphabetically()
    }
     */

    ListFlagsScreen(
        uiState = uiState,
        searchState = searchState,
        screen = screen,
        userSearch = searchModel.searchQuery,
        isUserSearch = searchModel.isSearchQuery,
        onUserSearchChange = { searchModel.onSearchQueryChange(it) },
        onNavigateUp = onNavigateUp,
        onCategorySelectSingle = { newSuperCategory, newSubCategory ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
            searchModel.updateCurrentCategory(newSuperCategory, newSubCategory) // TODO
        },
        onCategorySelectMultiple = { selectSuperCategory, selectSubCategory ->
            viewModel.updateCurrentCategories(selectSuperCategory, selectSubCategory)
            searchModel.updateCurrentCategories(selectSuperCategory, selectSubCategory) // TODO
        },
        onFlagSelect = { flag ->
            val currentList = when (searchModel.isSearchQuery) {
                true -> searchState.map { it.id }
                false -> uiState.currentFlags.map { it.id }
            }
            onNavigateDetails(flag.id, currentList)
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsScreen(
    modifier: Modifier = Modifier,
    uiState: ListFlagsUiState,
    searchState: List<FlagResources>,
    screen: Screen,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    userSearch: String,
    isUserSearch: Boolean,
    onUserSearchChange: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onCategorySelectSingle: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategorySelectMultiple: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onFlagSelect: (FlagResources) -> Unit,
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

    LaunchedEffect(isMenuExpanded) {
        if (isMenuExpanded) focusManager.clearFocus()
    }

    LaunchedEffect(isSearchBar) {
        when (isSearchBar) {
            true -> {
                if (!uiState.currentFlags.containsAll(DataSource.allFlagsList)) {
                    onCategorySelectSingle(FlagSuperCategory.All, null)
                    if (!isAtTop) {
                        coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                    }
                }
            }
            false -> {
                delay(Timing.MENU_COLLAPSE.toLong())
                if (!isSearchBar && isUserSearch) {
                    onUserSearchChange("")
                    if (!isAtTop) {
                        coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                    }
                }
            }
        }
    }

    LaunchedEffect(userSearch) {
        /* Reset scroll state after each userSearch character entry unless empty */
        if (isUserSearch && !isAtTop) {
            coroutineScope.launch { listState.scrollToItem(index = 0) }
        }

        /* Minimize filter menu when keyboard input */
        if (isUserSearch && isMenuExpanded) {
            isMenuExpanded = false
        }
    }

    /* For expandable/collapsable Scaffold TopBar */
    val scrollBehaviour = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


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
                    currentScreen = screen,
                    scrollBehaviour = scrollBehaviour,
                    userSearch = userSearch,
                    isUserSearch = isUserSearch,
                    onUserSearchChange = {
                        if (it == "" && !isAtTop) {
                            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                        }
                        onUserSearchChange(it)
                    },
                    onFocus = { isSearchBarFocused = true },
                    isMenuExpanded = isMenuExpanded,
                    isSearchBarFocused = isSearchBarFocused,
                    isSearchBar = isSearchBar,
                    onIsSearchBar = { isSearchBar = !isSearchBar },
                    onNavigateUp = {
                        focusManager.clearFocus()
                        onNavigateUp()
                    },
                )
            },
            floatingActionButton = {
                ScrollToTopButton(
                    isVisible = !isAtTop,
                    containerColor = containerColor2,
                    onClick = { coroutineScope.launch { listState.animateScrollToItem(index = 0) } }
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
                isUserSearch = isUserSearch,
                searchResults = searchState,
                currentFlagsList = uiState.currentFlags,
                onFlagSelect = {
                    focusManager.clearFocus()
                    onFlagSelect(it)
                },
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        CategoriesButtonMenu2(
            modifier = Modifier.fillMaxSize(),
            scaffoldPadding = scaffoldPaddingValues,
            buttonHorizontalPadding = Dimens.marginHorizontal16,
            screen = screen,
            flagCount = when (isUserSearch) {
                false -> uiState.currentFlags.size
                true -> searchState.size
            },
            onButtonHeightChange = { buttonHeight = it },
            isMenuExpanded = isMenuExpanded,
            onMenuButtonClick = { isMenuExpanded = !isMenuExpanded },
            containerColor1 = containerColor1,
            containerColor2 = containerColor2,
            currentSuperCategories = uiState.currentSuperCategories,
            currentSubCategories = uiState.currentSubCategories,
            onCategorySelectSingle = { flagSuperCategory, flagSubCategory ->
                onCategorySelectSingle(flagSuperCategory, flagSubCategory)
                coroutineScope.launch { listState.animateScrollToItem(index = 0) }
            },
            onCategorySelectMultiple = { flagSuperCategory, flagSubCategory ->
                onCategorySelectMultiple(flagSuperCategory, flagSubCategory)
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
    isUserSearch: Boolean,
    searchResults: List<FlagResources>,
    currentFlagsList: List<FlagResources>,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val listItemVerticalPadding = Dimens.small8

    Column(modifier = modifier) {
        /* To make LazyColumn scroll disappear into center of FilterFlagsButton */
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(filterButtonHeight / 2)
        )

        when (isUserSearch) {
            false -> {
                if (currentFlagsList.isNotEmpty()) {
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
                            count = currentFlagsList.size,
                            key = { index -> currentFlagsList[index].id }
                        ) { index ->
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                verticalPadding = listItemVerticalPadding,
                                flag = currentFlagsList[index],
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
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                verticalPadding = listItemVerticalPadding,
                                flag = searchResults[index],
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
    flag: FlagResources,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale

    Column(modifier = modifier) {
        FilledTonalButton(
            modifier = modifier,
            onClick = { onFlagSelect(flag) },
            shape = MaterialTheme.shapes.large,
            contentPadding = PaddingValues(Dimens.extraSmall4),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
        ) {
            Row(
                modifier = modifier
                    .padding(
                        vertical = verticalPadding,
                        horizontal = Dimens.small10,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                /* weight() uses available space after space taken by non-weighted children */
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(flag.flagOf),
                        modifier = Modifier
                            .fillMaxWidth()
                            /* Separate text from image */
                            .padding(end = Dimens.small8),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
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
        Spacer(modifier = modifier.height(verticalPadding))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsTopBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    scrollBehaviour: TopAppBarScrollBehavior,
    userSearch: String,
    isUserSearch: Boolean,
    onUserSearchChange: (String) -> Unit,
    onFocus: () -> Unit,
    isMenuExpanded: Boolean,
    isSearchBarFocused: Boolean,
    isSearchBar: Boolean,
    onIsSearchBar: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    @StringRes val title = when (currentScreen) {
        Screen.List -> Screen.StartMenu.title
        Screen.StartMenu -> null
        else -> currentScreen.title
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

    /* For search action button and topBar title to respect search bar animation */
    var isSearchBarDelay by rememberSaveable { mutableStateOf(value = true) }
    LaunchedEffect(isSearchBar) {
        when (isSearchBar) {
            true -> isSearchBarDelay = false
            false -> {
                delay(timeMillis = Timing.MENU_COLLAPSE.toLong())
                isSearchBarDelay = true
            }
        }
    }
    val isTopBarTitleDelay =
        if (scrollBehaviour.state.collapsedFraction >= 0.5f) isSearchBarDelay
        else true


    MediumTopAppBar(
        title = {
            title?.let {
                AnimatedVisibility(
                    visible = isTopBarTitleDelay,
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
                        Box(
                            modifier = Modifier.size(0.dp)
                                .weight(boxWeight)
                        )

                        Text(
                            text = stringResource(it),
                            style = titleStyle,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier.size(0.dp)
                                .weight(1f)
                        )
                    }
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                modifier = Modifier.onGloballyPositioned { layout ->
                    navigationIconOffsetX = layout.positionInParent().x
                    navigationIconWidth = layout.size.width
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            /* Search action button and search bar text field */
            if (isSearchBarDelay) {
                Box(modifier = Modifier
                    .padding(
                        end = TextFieldDefaults.contentPaddingWithoutLabel().calculateRightPadding(
                            layoutDirection = LocalLayoutDirection.current
                        ) - textFieldOffset
                    )
                ) {
                    IconButton(
                        onClick = onIsSearchBar,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search",
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchBar,
                enter = expandHorizontally(
                    animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
                ),
                exit = shrinkHorizontally(
                    animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE),
                ),
            ) {
                val focusRequesterSearch = remember { FocusRequester() }

                /* Focus search bar on nav back */
                LaunchedEffect(Unit) {
                    if (isSearchBarFocused) {
                        focusRequesterSearch.requestFocus()
                    }
                }
                /* Focus search bar on visibility */
                LaunchedEffect(isSearchBar) {
                    if (isSearchBar) {
                        focusRequesterSearch.requestFocus()
                    }
                }
                /* Focus search bar on menu collapse if previously focused */
                LaunchedEffect(isMenuExpanded) {
                    if (!isMenuExpanded && isSearchBarFocused) {
                        focusRequesterSearch.requestFocus()
                    }
                }

                Box {
                    /* Background for TextField as clipping issues when setting size directly */
                    Surface(
                        modifier = Modifier.offset(y = textFieldOffset)
                            .fillMaxWidth()
                            .padding(
                                end = Dimens.marginHorizontal16 - textFieldOffset,
                                start = textFieldStartPadding,
                            )
                            .height(48.dp * configuration.fontScale),
                        shape = TextFieldDefaults.shape,
                        color = TextFieldDefaults.colors().focusedContainerColor
                    ) { }

                    TextField(
                        value = userSearch,
                        onValueChange = onUserSearchChange,
                        modifier = Modifier//.height(48.dp * configuration.fontScale)
                            .fillMaxWidth()
                            .padding(
                                end = Dimens.marginHorizontal16 - textFieldOffset,
                                start = textFieldStartPadding,
                            )
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
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Row {
                                    AnimatedVisibility(
                                        visible = isUserSearch,
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
                                        IconButton(
                                            onClick = { onUserSearchChange("") }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription =
                                                    stringResource(R.string.search_clear),
                                                tint = MaterialTheme.colorScheme.onSurface,
                                            )
                                        }
                                    }
                                    Spacer(
                                        modifier = Modifier.width(Dimens.standardIconSize24 * 1.6f)
                                    )
                                }

                                IconButton(
                                    onClick = onIsSearchBar,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "search",
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )
                }
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