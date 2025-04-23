package dev.aftly.flags.ui.screen.startmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens


@Composable
fun StartMenuScreen(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onTopBarAction: (Screen) -> Unit,
    onNavigateDetails: (Screen) -> Unit,
) {
    StartMenuScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        onTopBarAction = onTopBarAction,
        onNavigateDetails = onNavigateDetails,
    )
}


@Composable
private fun StartMenuScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onTopBarAction: (Screen) -> Unit,
    onNavigateDetails: (Screen) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StaticTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = canNavigateBack,
                /* Padding for when can't navigate back */
                canNavigateBackTitlePadding = when (canNavigateBack) {
                    false -> Dimens.small8
                    true -> Dimens.canNavigateBack0
                },
                onNavigateUp = navigateUp,
                onAction = onTopBarAction,
            )
        },
    ) { scaffoldPadding ->
        StartMenuContent (
            modifier = Modifier.padding(scaffoldPadding),
            onNavigateDetails = onNavigateDetails,
        )
    }
}


@Composable
private fun StartMenuContent(
    modifier: Modifier = Modifier,
    onNavigateDetails: (Screen) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(
                start = Dimens.marginHorizontal24,
                end = Dimens.marginHorizontal24,
            )
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.start_menu_title),
            modifier = Modifier.padding(vertical = Dimens.large24),
            style = MaterialTheme.typography.displayMedium
        )
        ScreenCard(
            screen = Screen.List,
            selectScreen = onNavigateDetails,
        )
        ScreenCard(
            screen = Screen.Search,
            selectScreen = onNavigateDetails,
        )
        ScreenCard(
            screen = Screen.Game,
            selectScreen = onNavigateDetails,
        )
    }
}


@Composable
private fun ScreenCard(
    modifier: Modifier = Modifier,
    screen: Screen,
    selectScreen: (Screen) -> Unit,
) {
    Card(
        modifier = modifier
            .clickable {
                selectScreen(screen)
            }
            .padding(bottom = Dimens.medium16),
    ) {
        Box() {
            Column(modifier = Modifier.padding(Dimens.medium16)) {
                Text(
                    text = stringResource(screen.title_long ?: 0),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(screen.description ?: 0),
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = Dimens.medium16)
                )
            }
        }
    }
}


// Preview screen in Android Studio
/*
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun StartMenuScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        StartMenuScreen(
            currentScreen = Screen.StartMenu,
            canNavigateBack = false,
            navigateUp = { },
            onTopBarAction = { },
        ) { }
    }
}
 */