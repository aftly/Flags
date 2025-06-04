package dev.aftly.flags.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        navigateUp = onNavigateUp,
    )
}


@Composable
private fun SettingsScaffold(
    modifier: Modifier = Modifier,
    screen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {},
    ) { scaffoldPadding ->
        SettingsContent(modifier = Modifier.padding(scaffoldPadding))
    }
}


@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
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