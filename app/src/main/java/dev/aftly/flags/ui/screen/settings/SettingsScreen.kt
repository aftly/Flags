package dev.aftly.flags.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.DialogActionButton
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
        SettingsContent(
            modifier = Modifier.padding(scaffoldPadding)
        )
    }
}


@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
) {
    var dynamicChecked by remember { mutableStateOf(value = false) }
    var themeDialog by remember { mutableStateOf(value = false) }
    val halfMarginPadding = Dimens.marginHorizontal16 / 2

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = halfMarginPadding,
                vertical = Dimens.medium16,
            ),
    ) {
        /* Theme section label */
        Text(
            text = stringResource(R.string.settings_colors),
            modifier = Modifier.padding(horizontal = halfMarginPadding),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(modifier = Modifier.height(Dimens.medium16))

        /* Material You colors toggle row */
        Surface(
            onClick = { dynamicChecked = !dynamicChecked },
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = halfMarginPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Dynamic color / Material You",
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = "Use colors from system palette on Android 12+",
                        fontSize = 14.sp,
                        lineHeight = 12.sp
                    )
                }

                Switch(
                    checked = dynamicChecked,
                    onCheckedChange = { dynamicChecked = !dynamicChecked },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(halfMarginPadding))

        /* Theme selector Dialog */
        Surface(
            onClick = { themeDialog = true },
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(halfMarginPadding),
            ) {
                Column {
                    Text(
                        text = "Theme",
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = "System",
                        fontSize = 14.sp,
                        lineHeight = 12.sp
                    )
                }
            }

            if (themeDialog) {
                ThemeDialog(onDismiss = { themeDialog = false })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDialog(
    onDismiss: () -> Unit,
) {
    val fullPadding = Dimens.large24
    val halfPadding = Dimens.large24 / 2

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(
                    top = fullPadding,
                    start = halfPadding,
                    end = halfPadding,
                ),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                /* Title */
                Row(modifier = Modifier.padding(horizontal = halfPadding)) {
                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(
                            top = Dimens.medium16,
                            bottom = Dimens.small8,
                        )
                        .selectableGroup()
                ) {
                    val radioOptions = listOf(
                        stringResource(R.string.settings_theme_system),
                        stringResource(R.string.settings_theme_light),
                        stringResource(R.string.settings_theme_dark)
                    )
                    val (selectedOption, onOptionSelected) =
                        remember { mutableStateOf(radioOptions[0]) }

                    radioOptions.forEach { string ->
                        Box(
                            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                .selectable(
                                    selected = (string == selectedOption),
                                    onClick = { onOptionSelected(string) },
                                    role = Role.RadioButton,
                                ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(
                                        horizontal = halfPadding,
                                        vertical = halfPadding / 2,
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = string)

                                RadioButton(
                                    selected = (string == selectedOption),
                                    onClick = null,
                                )
                            }
                        }
                    }
                }


                /* Action buttons */
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            end = Dimens.medium16,
                            bottom = Dimens.medium16,
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    DialogActionButton(
                        onClick = onDismiss,
                        buttonStringResId = R.string.dialog_cancel,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
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