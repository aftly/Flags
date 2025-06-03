package dev.aftly.flags.ui.screen.list

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.ListScreenTopBar
import dev.aftly.flags.ui.component.FilterFlagsButton
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.ScrollToTopButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/* Displays list of flags. Upon flag select, pass up it's navArg and navigate to FlagScreen() */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFlagsScreen(
    viewModel: ListFlagsViewModel = viewModel(),
    searchModel: SearchViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateDetails: (Int, List<Int>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by searchModel.searchResults.collectAsStateWithLifecycle()

    val scrollBehaviour = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val configuration = LocalConfiguration.current

    /* Update (alphabetical) order of flag lists when language changes */
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.sortFlagsAlphabetically() }

    ListFlagsScaffold(
        currentScreen = currentScreen,
        scrollBehaviour = scrollBehaviour,
        canNavigateBack = canNavigateBack,
        currentCategoryTitle = uiState.currentCategoryTitle,
        currentSuperCategory = uiState.currentSuperCategory,
        currentSuperCategories = uiState.currentSuperCategories,
        currentSubCategories = uiState.currentSubCategories,
        userSearch = searchModel.searchQuery,
        isUserSearch = searchModel.isSearchQuery,
        searchResults = searchResults,
        onUserSearchChange = { searchModel.onSearchQueryChange(it) },
        currentFlagsList = uiState.currentFlags,
        onNavigateUp = onNavigateUp,
        onCategorySelect = { newSuperCategory, newSubCategory ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
            searchModel.updateCurrentCategory(newSuperCategory, newSubCategory)
        },
        onCategoryMultiSelect = { selectSuperCategory, selectSubCategory ->
            viewModel.updateCurrentCategories(selectSuperCategory, selectSubCategory)
            searchModel.updateCurrentCategories(selectSuperCategory, selectSubCategory)
        },
        onFlagSelect = { flag ->
            val currentList = when (searchModel.isSearchQuery) {
                true -> searchResults.map { it.id }
                false -> uiState.currentFlags.map { it.id }
            }
            onNavigateDetails(flag.id, currentList)
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListFlagsScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    scrollBehaviour: TopAppBarScrollBehavior,
    canNavigateBack: Boolean,
    containerColor1: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor2: Color = MaterialTheme.colorScheme.secondary,
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    currentSuperCategories: List<FlagSuperCategory>?,
    currentSubCategories: List<FlagCategory>?,
    userSearch: String,
    isUserSearch: Boolean,
    searchResults: List<FlagResources>,
    onUserSearchChange: (String) -> Unit,
    currentFlagsList: List<FlagResources>,
    onNavigateUp: () -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onFlagSelect: (FlagResources) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion amd tracks current button height
     * Also for FilterFlagsButton to access Scaffold() padding */
    var buttonExpanded by rememberSaveable { mutableStateOf(value = false) }
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
    var isSearchBar by rememberSaveable { mutableStateOf(value = false) }
    LaunchedEffect(isSearchBar) {
        when (isSearchBar) {
            true -> {
                if (!currentFlagsList.containsAll(DataSource.allFlagsList)) {
                    onCategorySelect(FlagSuperCategory.All, null)
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
        if (isUserSearch && buttonExpanded) {
            buttonExpanded = false
        }
    }


    /* Scaffold within box so FilterFlagsButton & it's associated surface can overlay it */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd,
    ) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = modifier,
            topBar = {
                ListScreenTopBar(
                    currentScreen = currentScreen,
                    scrollBehaviour = scrollBehaviour,
                    canNavigateBack = canNavigateBack,
                    userSearch = userSearch,
                    isUserSearch = isUserSearch,
                    onUserSearchChange = {
                        if (it == "" && !isAtTop) {
                            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                        }
                        onUserSearchChange(it)
                    },
                    isSearchBar = isSearchBar,
                    onIsSearchBar = { isSearchBar = !isSearchBar },
                    onNavigateUp = onNavigateUp,
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
                searchResults = searchResults,
                currentFlagsList = currentFlagsList,
                onFlagSelect = onFlagSelect,
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        FilterFlagsButton(
            modifier = Modifier.fillMaxSize(),
            scaffoldPadding = scaffoldPaddingValues,
            buttonHorizontalPadding = Dimens.marginHorizontal16,
            screen = currentScreen,
            flagCount = when (isUserSearch) {
                false -> currentFlagsList.size
                true -> searchResults.size
            },
            onButtonHeightChange = { buttonHeight = it },
            buttonExpanded = buttonExpanded,
            onButtonExpand = { buttonExpanded = !buttonExpanded },
            containerColor1 = containerColor1,
            containerColor2 = containerColor2,
            currentCategoryTitle = currentCategoryTitle,
            currentSuperCategory = currentSuperCategory,
            currentSuperCategories = currentSuperCategories,
            currentSubCategories = currentSubCategories,
            onCategorySelect = { flagSuperCategory, flagSubCategory ->
                onCategorySelect(flagSuperCategory, flagSubCategory)
                coroutineScope.launch { listState.animateScrollToItem(index = 0) }
            },
            onCategoryMultiSelect = { flagSuperCategory, flagSubCategory ->
                onCategoryMultiSelect(flagSuperCategory, flagSubCategory)
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
                    NoResultsFound(modifier = Modifier.fillMaxSize())
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
                        isSearch = true,
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