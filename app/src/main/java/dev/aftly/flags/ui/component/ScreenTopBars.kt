package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceDark
import dev.aftly.flags.ui.theme.surfaceLight
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreenTopBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    scrollBehaviour: TopAppBarScrollBehavior,
    canNavigateBack: Boolean,
    userSearch: String,
    isUserSearch: Boolean,
    onUserSearchChange: (String) -> Unit,
    isSearchBar: Boolean,
    onIsSearchBar: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    /* Ensures navigationIcon persists throughout the lifecycle */
    val canNavigateBackStatic by remember { mutableStateOf(canNavigateBack) }

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
    val focusRequester = remember { FocusRequester() }

    /* For search action button and topBar title to respect search bar animation */
    var isSearchBarDelay by rememberSaveable { mutableStateOf(value = true) }
    LaunchedEffect(isSearchBar) {
        when (isSearchBar) {
            true -> {
                isSearchBarDelay = false
                focusRequester.requestFocus()
            }
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
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(0.dp)
                                .weight(boxWeight)
                        )

                        Text(
                            text = stringResource(it),
                            style = titleStyle,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .size(0.dp)
                                .weight(1f)
                        )
                    }
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBackStatic) {
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
                        modifier = Modifier
                            //.height(48.dp * configuration.fontScale)
                            .fillMaxWidth()
                            .padding(
                                end = Dimens.marginHorizontal16 - textFieldOffset,
                                start = textFieldStartPadding,
                            )
                            .focusRequester(focusRequester),
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


/* Simple top bar with differing properties depending on Screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralTopBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    @StringRes currentTitle: Int? = null,
    canNavigateBack: Boolean = false,
    isActionOn: Boolean = false, /* For buttons with 2 icon states */
    isRelatedFlagsButton: Boolean = false,
    isPortraitOrientation: Boolean? = null,
    isGame: Boolean = false,
    buttonExpanded: Boolean = false,
    timer: String = "0:00", /* For game timer */
    onButtonExpand: () -> Unit = {},
    onButtonPosition: (Offset) -> Unit = {},
    onButtonWidth: (Int) -> Unit = {},
    onNavigateUp: () -> Unit,
    onNavigateDetails: (Screen) -> Unit,
    onAction: () -> Unit,
) {
    /* Ensures navigationIcon persists throughout the lifecycle */
    val canNavigateBackStatic by remember { mutableStateOf(canNavigateBack) }

    @StringRes val title = when (currentScreen) {
        Screen.StartMenu -> null
        Screen.Fullscreen -> if (isGame == true) null else currentTitle
        else -> currentScreen.title
    }


    TopAppBar(
        title = {
            when (currentScreen) {
                Screen.Fullscreen -> title?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(it),
                            textAlign = TextAlign.Center,
                            style = when (isPortraitOrientation) {
                                true -> MaterialTheme.typography.headlineSmall
                                else -> MaterialTheme.typography.headlineLarge
                            },
                        )
                    }
                }
                Screen.Flag ->
                    if (isRelatedFlagsButton) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            RelatedFlagsButton(
                                buttonExpanded = buttonExpanded,
                                onButtonExpand = onButtonExpand,
                                onButtonPosition = onButtonPosition,
                                onButtonWidth = onButtonWidth,
                            )
                        }
                    }
                else -> title?.let {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBackStatic) {
                IconButton(
                    onClick = onNavigateUp,
                    colors = when (isGame to currentScreen) {
                        true to Screen.Fullscreen -> IconButtonDefaults.iconButtonColors(
                            containerColor = surfaceDark.copy(alpha = 0.5f)
                        )
                        else -> IconButtonDefaults.iconButtonColors()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back",
                    )
                }
            }
        },
        actions = {
            when (currentScreen) {
                Screen.StartMenu -> {
                    // TODO: Implement InfoScreen() for "about app" info
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "about"
                        )
                    }
                    // TODO: Implement SettingsScreen()
                    IconButton(
                        onClick = { /* onNavigateDetails(Screen.Settings) */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "settings"
                        )
                    }
                }
                Screen.Flag ->
                    // TODO: Implement (persistent) saved list
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "make favorite",
                        )
                    }
                Screen.Game -> {
                    Text(
                        text = timer,
                        modifier = Modifier.padding(end = Dimens.extraSmall4),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(onClick = onAction) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.small8))
                }
                Screen.Fullscreen ->
                    IconButton(
                        onClick = onAction,
                        colors = when (isGame) {
                            true -> IconButtonDefaults.iconButtonColors(
                                containerColor = surfaceDark.copy(alpha = 0.5f)
                            )
                            false -> IconButtonDefaults.iconButtonColors()
                        },
                    ) {
                        when(isActionOn) {
                            true -> 
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                )
                            false ->
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                )
                        }
                    }
                else -> {}
            }
        },
        colors = when (currentScreen) {
            Screen.Fullscreen ->
                if (isGame) {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = surfaceLight,
                        actionIconContentColor = surfaceLight,
                    )
                } else {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceDark.copy(alpha = 0.5f),
                        navigationIconContentColor = surfaceLight,
                        titleContentColor = surfaceLight,
                        actionIconContentColor = surfaceLight,
                    )
                }
            else -> TopAppBarDefaults.topAppBarColors()
        }
    )
}


/* Preview screen in Android Studio */
/*
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun AppBarPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ExpandableTopAppBar (
                    currentScreen = Screen.List,
                    scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                    canNavigateBack = false,
                    onNavigateUp = { },
                )
            },
        ) { }
    }
}
 */