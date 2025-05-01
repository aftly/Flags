package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.FlagsTheme
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableTopAppBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    scrollBehaviour: TopAppBarScrollBehavior,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    /* Ensures navigationIcon persists throughout the lifecycle */
    val canNavigateBackStatic by remember { mutableStateOf(canNavigateBack) }

    @StringRes val title = when (currentScreen) {
        Screen.List -> Screen.StartMenu.title
        Screen.StartMenu -> null
        else -> currentScreen.title
    }

    /* Manage TitleStyle transition between expanded (start) and collapsed (stop) TopBar title */
    val titleStyle = lerp(
        start = MaterialTheme.typography.headlineLarge,
        stop = MaterialTheme.typography.headlineSmall,
        fraction = scrollBehaviour.state.collapsedFraction
    )

    /* Manage padding transition between expanded (start) and collapsed (stop) TopBar title */
    val titleStartPadding = lerp(
        start = 0.dp,
        stop = Dimens.large24,
        fraction = scrollBehaviour.state.collapsedFraction,
    )

    var titleBoxWidth by remember { mutableIntStateOf(value = 0) }
    var titleTextWidth by remember { mutableIntStateOf(value = 0) }

    val horiztonalOffset by animateIntAsState(
        targetValue = lerp(
            start = 0,
            stop = - (titleBoxWidth - titleTextWidth) / 2,
            fraction = scrollBehaviour.state.collapsedFraction,
        )
    )


    MediumTopAppBar(
        title = {
            title?.let {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = Dimens.medium16)
                        .onSizeChanged { size ->
                            titleBoxWidth = size.width
                        }
                ) {
                    Text(
                        text = stringResource(it),
                        style = titleStyle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                            .onSizeChanged { size ->
                                titleTextWidth = size.width
                            }
                            .offset { IntOffset(x = horiztonalOffset, y = 0) }
                            .padding(start = titleStartPadding),
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBackStatic) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back",
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
fun StaticTopAppBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean = false,
    canNavigateBackTitlePadding: Dp = Dimens.canNavigateBack0,
    onNavigateUp: () -> Unit,
    onAction: (Screen) -> Unit,
) {
    /* Ensures navigationIcon persists throughout the lifecycle */
    val canNavigateBackStatic by remember { mutableStateOf(canNavigateBack) }

    @StringRes val title = when (currentScreen) {
        Screen.StartMenu -> null
        else -> currentScreen.title
    }

    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = canNavigateBackTitlePadding),
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBackStatic) {
                IconButton(onClick = onNavigateUp) {
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
                        onClick = { /* onAction(Screen.Settings) */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "settings"
                        )
                    }
                }
                Screen.Flag -> {
                    // TODO: Implement (persistent) Favourites list
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "make favorite",
                        )
                    }
                }
                else -> { }
            }
        },
    )
}


/* Preview screen in Android Studio */

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