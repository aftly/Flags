package dev.aftly.flags.ui.screen.settings

import android.app.Activity
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.SOURCE_URL
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.AppViewModelProvider
import dev.aftly.flags.ui.component.DialogActionButton
import dev.aftly.flags.ui.component.openWebLink
import dev.aftly.flags.ui.theme.AppThemePreference
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.SystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    screen: Screen,
    onNavigationDrawer: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val systemUiController = remember {
        SystemUiController(view, (view.context as Activity).window)
    }
    val isSystemInDarkTheme by rememberUpdatedState(newValue = isSystemInDarkTheme())
    val coroutineScope = rememberCoroutineScope()

    SettingsScreen(
        uiState = uiState,
        screen = screen,
        onCheckDynamicColor = { viewModel.saveDynamicColor(it) },
        onClickTheme = {
            viewModel.saveTheme(it)

            coroutineScope.launch {
                delay(timeMillis = 100)
                systemUiController.setLightStatusBar(
                    viewModel.systemBarsIsLight(isSystemInDarkTheme)
                )
            }
        },
        onNavigationDrawer = onNavigationDrawer,
    )
}


@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    screen: Screen,
    onCheckDynamicColor: (Boolean) -> Unit,
    onClickTheme: (AppThemePreference) -> Unit,
    onNavigationDrawer: () -> Unit,
) {
    var isThemeDialog by remember { mutableStateOf(value = false) }
    if (isThemeDialog) {
        ThemeDialog(
            theme = uiState.theme,
            isDynamicColor = uiState.isDynamicColor,
            onOptionSelected = onClickTheme,
            onDismiss = { isThemeDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingsTopBar(
                screen = screen,
                onNavigationDrawer = onNavigationDrawer,
            )
        },
    ) { scaffoldPadding ->
        SettingsContent(
            modifier = Modifier.padding(scaffoldPadding),
            isDynamicColor = uiState.isDynamicColor,
            theme = uiState.theme,
            onCheckDynamicColor = onCheckDynamicColor,
            onThemeDialog = { isThemeDialog = true },
        )
    }
}


@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    isDynamicColor: Boolean,
    theme: AppThemePreference,
    onThemeDialog: () -> Unit,
    onCheckDynamicColor: (Boolean) -> Unit,
) {
    val halfMarginPadding = Dimens.marginHorizontal16 / 2

    Column(
        modifier = modifier.fillMaxSize()
            .padding(
                horizontal = halfMarginPadding,
                vertical = Dimens.medium16,
            ),
    ) {
        ColorsSettings(
            halfMarginPadding = halfMarginPadding,
            isDynamicColor = isDynamicColor,
            theme = theme,
            onCheckDynamicColor = onCheckDynamicColor,
            onThemeDialog = onThemeDialog,
        )

        AboutContent(
            halfMarginPadding = halfMarginPadding
        )
    }
}


@Composable
private fun ColorsSettings(
    halfMarginPadding: Dp,
    isDynamicColor: Boolean,
    theme: AppThemePreference,
    onCheckDynamicColor: (Boolean) -> Unit,
    onThemeDialog: () -> Unit,
) {
    val isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    val isDynamicColorsSettingEnabled = when (theme) {
        AppThemePreference.SYSTEM_BLACK -> false
        AppThemePreference.BLACK -> false
        else -> true
    }

    /* Theme section label */
    SettingsLabel(
        modifier = Modifier.padding(horizontal = halfMarginPadding),
        label = R.string.settings_colors,
    )

    /* Material You colors toggle row (for devices using Api30+) */
    if (isApi30) {
        Surface(
            onClick = { onCheckDynamicColor(!isDynamicColor) },
            enabled = isDynamicColorsSettingEnabled,
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = halfMarginPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsBody(
                    modifier = Modifier.weight(1f),
                    title = R.string.settings_dynamic_colors_title,
                    color = if (isDynamicColorsSettingEnabled) Color.Unspecified else Color.Gray,
                    description = R.string.settings_dynamic_colors_description,
                )

                Switch(
                    checked = isDynamicColor,
                    onCheckedChange = { onCheckDynamicColor(it) },
                    enabled = isDynamicColorsSettingEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(halfMarginPadding))
    }

    /* Theme selector Dialog */
    Surface(
        onClick = onThemeDialog,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(halfMarginPadding),
        ) {
            SettingsBody(
                title = R.string.settings_theme,
                description = theme.title,
            )
        }
    }

    Spacer(modifier = Modifier.height(halfMarginPadding * 4))
}


@Composable
private fun AboutContent(
    halfMarginPadding: Dp,
) {
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    SettingsLabel(
        modifier = Modifier.padding(horizontal = halfMarginPadding),
        label = R.string.settings_about,
    )

    /* Source code button */
    Surface(
        onClick = { openWebLink(context, SOURCE_URL) },
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(halfMarginPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsBody(
                modifier = Modifier.weight(1f),
                title = R.string.settings_about_source_code_title,
                description = R.string.settings_about_source_code_description,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Default.OpenInNew,
                contentDescription = null,
            )
        }
    }

    Spacer(modifier = Modifier.height(halfMarginPadding))

    /* Privacy policy button */
    Surface(
        onClick = {},
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(halfMarginPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsBody(
                title = R.string.settings_about_privacy_policy_title,
                description = R.string.settings_about_privacy_policy_description,
            )
        }
    }

    /* App version button */
    Surface(
        onClick = {},
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(halfMarginPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsBody(
                title = R.string.settings_about_app_version_title,
                description = R.string.settings_about_app_version_description,
                descriptionFormatArg = versionName,
            )
        }
    }
}


@Composable
private fun SettingsLabel(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
) {
    Text(
        text = stringResource(label),
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelMedium,
    )

    Spacer(modifier = Modifier.height(Dimens.small8))
}


@Composable
private fun SettingsBody(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes description: Int,
    color: Color = Color.Unspecified,
    descriptionFormatArg: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(title),
            color = color,
            fontWeight = FontWeight.Bold,
            lineHeight = 12.sp,
        )

        if (descriptionFormatArg == null) {
            Text(
                text = stringResource(description),
                color = color,
                fontSize = 14.sp,
                lineHeight = 12.sp,
            )
        } else {
            Text(
                text = stringResource(description, descriptionFormatArg),
                color = color,
                fontSize = 14.sp,
                lineHeight = 12.sp,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDialog(
    theme: AppThemePreference,
    isDynamicColor: Boolean,
    onOptionSelected: (AppThemePreference) -> Unit,
    onDismiss: () -> Unit,
) {
    val fullPadding = Dimens.large24
    val halfPadding = Dimens.large24 / 2
    val disabledTextColor = MaterialTheme.colorScheme.outline

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
                    val radioOptions = AppThemePreference.entries

                    radioOptions.forEach { themeOption ->
                        Box(
                            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                .selectable(
                                    selected = (themeOption == theme),
                                    enabled = when (themeOption) {
                                        AppThemePreference.SYSTEM_BLACK -> !isDynamicColor
                                        AppThemePreference.BLACK -> !isDynamicColor
                                        else -> true
                                    },
                                    onClick = {
                                        onOptionSelected(themeOption)
                                        onDismiss()
                                    },
                                    role = Role.RadioButton,
                                ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = halfPadding,
                                        vertical = halfPadding / 1.5f,
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(themeOption.title),
                                    color = when (isDynamicColor to themeOption) {
                                        true to AppThemePreference.SYSTEM_BLACK -> disabledTextColor
                                        true to AppThemePreference.BLACK -> disabledTextColor
                                        else -> Color.Unspecified
                                    }
                                )

                                RadioButton(
                                    selected = (themeOption == theme),
                                    onClick = null,
                                    enabled = when (themeOption) {
                                        AppThemePreference.SYSTEM_BLACK -> !isDynamicColor
                                        AppThemePreference.BLACK -> !isDynamicColor
                                        else -> true
                                    },
                                )
                            }
                        }
                    }
                }


                /* Action buttons */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
    onNavigationDrawer: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(screen.title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigationDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_drawer_content_description),
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