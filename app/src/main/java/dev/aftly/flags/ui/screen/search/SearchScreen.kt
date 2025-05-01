package dev.aftly.flags.ui.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.ScrollToTopButton
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.util.getFlagNavArg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalConfiguration


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateDetails: (String) -> Unit,
) {
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    /* Update (alphabetical) order of flag lists when language changes */
    val currentLocale = LocalConfiguration.current.locales[0]
    LaunchedEffect(currentLocale) { viewModel.sortFlagsAlphabetically() }

    SearchScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        userSearch = viewModel.searchQuery,
        isUserSearch = viewModel.isSearchQuery,
        searchResults = searchResults,
        onUserSearchChange = { viewModel.onSearchQueryChange(it) },
        onNavigateUp = onNavigateUp,
        onFlagSelect = { onNavigateDetails(getFlagNavArg(flag = it)) }
    )
}


@Composable
private fun SearchScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    userSearch: String,
    isUserSearch: Boolean,
    searchResults: List<FlagResources>,
    onUserSearchChange: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onFlagSelect: (FlagResources) -> Unit,
) {
    /* Properties for ScrollToTopButton */
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }
    /* Reset scroll state after clearing user search & LazyColumn animation finishes */
    LaunchedEffect(isUserSearch) {
        if (!isUserSearch && !isAtTop) {
            delay(timeMillis = Timings.MENU_EXPAND.toLong())
            coroutineScope.launch { listState.scrollToItem(index = 0) }
        }
    }
    /* Reset scroll state after each userSearch character entry unless empty */
    LaunchedEffect(userSearch) {
        if (isUserSearch && !isAtTop) {
            coroutineScope.launch { listState.scrollToItem(index = 0) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StaticTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                onAction = { },
            )
        },
        floatingActionButton = {
            ScrollToTopButton(
                isVisible = !isAtTop,
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = { coroutineScope.launch { listState.animateScrollToItem(index = 0) } }
            )
        }
    ) { scaffoldPadding ->
        SearchContent(
            modifier = Modifier.fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            scaffoldPadding = scaffoldPadding,
            listState = listState,
            userSearch = userSearch,
            isUserSearch = isUserSearch,
            searchResults = searchResults,
            onUserSearchChange = onUserSearchChange,
            onFlagSelect = onFlagSelect,
        )
    }
}


@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    listState: LazyListState,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    userSearch: String,
    isUserSearch: Boolean,
    searchResults: List<FlagResources>,
    onUserSearchChange: (String) -> Unit,
    onFlagSelect: (FlagResources) -> Unit,
) {
    /* Properties for (animated) TextField colors */
    val textFieldColors = TextFieldDefaults.colors()

    val animatedTextFieldContainerColor by animateColorAsState(
        targetValue = if (isUserSearch) surfaceColor else textFieldColors.focusedContainerColor,
        animationSpec = tween(durationMillis = Timings.MENU_EXPAND / 2),
    )


    /* Search content */
    Column(
        modifier = modifier.padding(horizontal = Dimens.marginHorizontal16),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = userSearch,
            onValueChange = onUserSearchChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(text = stringResource(R.string.flag_search_bar_placeholder))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            trailingIcon = {
                if (isUserSearch) {
                    IconButton(
                        onClick = { onUserSearchChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.flag_search_clear),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = animatedTextFieldContainerColor,
                unfocusedContainerColor = animatedTextFieldContainerColor,
            )
        )

        AnimatedVisibility(
            visible = isUserSearch,
            enter = expandVertically(
                animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = Timings.MENU_EXPAND),
                shrinkTowards = Alignment.Top,
            ),
        ) {
            if (searchResults.isEmpty()) {
                NoResultsFoundContent(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = Dimens.small8,
                        bottom = scaffoldPadding.calculateBottomPadding()
                    ),
                ) {
                    items(
                        count = searchResults.size,
                        key = { index -> searchResults[index].id }
                    ) { index ->
                        val flag = searchResults[index]

                        SearchItem(
                            modifier = Modifier.fillMaxWidth(),
                            flag = flag,
                            onFlagSelect = { onFlagSelect(flag) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    flag: FlagResources,
    onFlagSelect: (FlagResources) -> Unit,
) {
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
            var rowHeight by remember { mutableStateOf(Dimens.listItemHeight48) }

            Row(
                modifier = modifier
                    .height(rowHeight)
                    .padding(
                        vertical = Dimens.small8,
                        horizontal = Dimens.small10,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                /* weight() uses available space after space taken by non-weighted children */
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(flag.flagOf),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.lineCount > 1) {
                                rowHeight = Dimens.listItemHeight64
                            }
                        },
                    )
                }
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    /* Make standard image height == standard button height - height padding */
                    modifier = Modifier.height(Dimens.listItemHeight48 - Dimens.small8 * 2),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        Spacer(modifier = modifier.height(Dimens.small8))
    }
}


@Composable
fun NoResultsFoundContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.flag_search_no_results_title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.flag_search_no_results_description),
            modifier = Modifier.padding(vertical = Dimens.extraSmall6),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.flag_search_no_results_description_2),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(Dimens.bottomSpacer80))
    }
}


/* Preview screen in Android Studio */
/* TODO */