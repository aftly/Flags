package dev.aftly.flags.ui.screen.startmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens


@Composable
fun StartMenuScreen(
    modifier: Modifier = Modifier,
    screen: Screen,
    onNavigateDetails: (Screen) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StartMenuTopBar(onNavigateDetails = onNavigateDetails)
        },
    ) { scaffoldPadding ->
        StartMenuContent (
            modifier = Modifier.padding(scaffoldPadding),
            screen = screen,
            onNavigateDetails = onNavigateDetails,
        )
    }
}


@Composable
private fun StartMenuContent(
    modifier: Modifier = Modifier,
    screen: Screen,
    onNavigateDetails: (Screen) -> Unit,
) {
    val annotatedTitle = if (screen.title != null) {
        buildAnnotatedString {
            val title = stringResource(screen.title)
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
    } else buildAnnotatedString { append(text = "") }


    Column(
        modifier = modifier.fillMaxSize()
            .padding(
                start = Dimens.marginHorizontal16,
                end = Dimens.marginHorizontal16,
            )
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = annotatedTitle,
            modifier = Modifier.padding(
                start = Dimens.extraSmall4,
                top = Dimens.large24,
                bottom = Dimens.large24,
            ),
            style = MaterialTheme.typography.displaySmall,
        )

        ScreenCard(
            screen = Screen.List,
            selectScreen = onNavigateDetails,
        )

        Spacer(modifier = Modifier.height(Dimens.medium16))

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
            },
    ) {
        Box {
            Column(
                modifier = Modifier.padding(Dimens.medium16)
            ) {
                Text(
                    text = stringResource(screen.title ?: 0),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartMenuTopBar(
    modifier: Modifier = Modifier,
    onNavigateDetails: (Screen) -> Unit,
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        actions = {
            IconButton(
                onClick = { onNavigateDetails(Screen.Settings) }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "settings",
                )
            }
        },
    )
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