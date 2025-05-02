package dev.aftly.flags.ui.screen.list

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.ExpandableTopAppBar
import dev.aftly.flags.ui.component.FilterFlagsButton
import dev.aftly.flags.ui.component.ScrollToTopButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.util.getFlagNavArg
import kotlinx.coroutines.launch


/* Displays list of flags. Upon flag select, pass up it's navArg and navigate to FlagScreen() */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFlagsScreen(
    viewModel: ListFlagsViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateDetails: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehaviour = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val configuration = LocalConfiguration.current

    /* Update (alphabetical) order of flag lists when language changes */
    val locale = configuration.locales[0]
    LaunchedEffect(locale) { viewModel.sortFlagsAlphabetically() }

    ListFlagsScaffold(
        currentScreen = currentScreen,
        scrollBehaviour = scrollBehaviour,
        canNavigateBack = canNavigateBack,
        fontScale = configuration.fontScale,
        currentCategoryTitle = uiState.currentCategoryTitle,
        currentSuperCategory = uiState.currentSuperCategory,
        currentFlagsList = uiState.currentFlags,
        onNavigateUp = onNavigateUp,
        onCategorySelect = { newSuperCategory: FlagSuperCategory?, newSubCategory: FlagCategory? ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
        },
        onFlagSelect = { onNavigateDetails(getFlagNavArg(flag = it)) },
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
    fontScale: Float,
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    currentFlagsList: List<FlagResources>,
    onNavigateUp: () -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onFlagSelect: (FlagResources) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion amd tracks current button height */
    var buttonExpanded by rememberSaveable { mutableStateOf(value = false) }
    var buttonHeight by remember { mutableStateOf(0.dp) }

    /* Properties for resetting scroll position when category is selected in FilterFlagsButton or
     * ScrollToTopButton clicked */
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    /* So FilterFlagsButton can access Scaffold() padding */
    var scaffoldTopPadding by remember { mutableStateOf(value = 0.dp) }
    var scaffoldBottomPadding by remember { mutableStateOf(value = 0.dp) }


    /* Scaffold within box so FilterFlagsButton & it's associated surface can overlay it */
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier,
            topBar = {
                ExpandableTopAppBar(
                    currentScreen = currentScreen,
                    scrollBehaviour = scrollBehaviour,
                    canNavigateBack = canNavigateBack,
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
            scaffoldTopPadding = scaffoldPadding.calculateTopPadding()
            scaffoldBottomPadding = scaffoldPadding.calculateBottomPadding()

            ListFlagsContent(
                modifier = Modifier.fillMaxSize()
                    .padding(
                        top = scaffoldPadding.calculateTopPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16,
                    ),
                filterButtonHeight = buttonHeight,
                scaffoldPadding = scaffoldPadding,
                scrollBehaviour = scrollBehaviour,
                listState = listState,
                fontScale = fontScale,
                currentFlagsList = currentFlagsList,
                onFlagSelect = onFlagSelect,
            )
        }


        /* Surface to receive taps when FilterFlagsButton is expanded, to collapse it */
        AnimatedVisibility(
            visible = buttonExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
                    .clickable { buttonExpanded = !buttonExpanded },
                color = Color.Black.copy(alpha = 0.35f),
            ) { }
        }


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        FilterFlagsButton(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = scaffoldTopPadding,
                    bottom = scaffoldBottomPadding,
                    start = Dimens.marginHorizontal16,
                    end = Dimens.marginHorizontal16,
                ),
            onButtonHeightChange = { buttonHeight = it },
            buttonExpanded = buttonExpanded,
            onButtonExpand = { buttonExpanded = !buttonExpanded },
            containerColor1 = containerColor1,
            containerColor2 = containerColor2,
            fontScale = fontScale,
            currentCategoryTitle = currentCategoryTitle,
            currentSuperCategory = currentSuperCategory,
            onCategorySelect = { flagSuperCategory, flagSubCategory ->
                onCategorySelect(flagSuperCategory, flagSubCategory)
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
    scaffoldPadding: PaddingValues,
    scrollBehaviour: TopAppBarScrollBehavior,
    listState: LazyListState,
    fontScale: Float,
    currentFlagsList: List<FlagResources>,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val listItemVerticalPadding = Dimens.small8

    Column(modifier = modifier) {
        /* To make LazyColumn scroll disappear into center of FilterFlagsButton */
        Spacer(
            modifier = Modifier.fillMaxWidth()
                .height(filterButtonHeight / 2)
        )

        /* Flags list */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehaviour.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                bottom = scaffoldPadding.calculateBottomPadding(),
                top = filterButtonHeight / 2 + listItemVerticalPadding,
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
                    fontScale = fontScale,
                    verticalPadding = listItemVerticalPadding,
                    flag = currentFlagsList[index],
                    onFlagSelect = onFlagSelect,
                )
            }
        }
    }
}


@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    fontScale: Float,
    verticalPadding: Dp,
    flag: FlagResources,
    onFlagSelect: (FlagResources) -> Unit,
) {
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
                        modifier = Modifier.fillMaxWidth()
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