package dev.aftly.flags.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    screen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    SettingsScaffold(
        screen = screen,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
    )
}


@Composable
private fun SettingsScaffold(
    modifier: Modifier = Modifier,
    screen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingsTopBar(
                screen = screen,
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
            )
        },
    ) { scaffoldPadding ->
        SettingsContent(modifier = Modifier.padding(scaffoldPadding))
    }
}


@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = Dimens.marginHorizontal16,
                vertical = Dimens.small8
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        // TODO
        Text(text = "To be implemented...")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    modifier: Modifier = Modifier,
    screen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    /* Ensures navigationIcon persists throughout the lifecycle */
    val canNavigateBackStatic by remember { mutableStateOf(canNavigateBack) }

    TopAppBar(
        title = {
            screen.title?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
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
    )
}


// Preview screen in Android Studio
/*
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        SettingsScreen(
            currentScreen = Screen.Settings,
            canNavigateBack = true,
        ) { }
    }
}
 */