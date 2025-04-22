package dev.aftly.flags.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.FlagsTheme


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    SettingsScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
    )
}


@Composable
private fun SettingsScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StaticTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = canNavigateBack,
                onNavigateUp = navigateUp,
                onAction = { }
            )
        }
    ) { scaffoldPadding ->
        SettingsContent(
            modifier = Modifier.padding(scaffoldPadding)
        )
    }
}


@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal24, vertical = Dimens.small8),
        verticalArrangement = Arrangement.Center,
    ) {
        // TODO
        Text(text = "To be implemented...")
    }
}


// Preview screen in Android Studio
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