package dev.aftly.flags.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentScreen: Screen?,
    onClose: () -> Unit,
    onNavigateDetails: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.medium16)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.padding(Dimens.medium16),
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = Screen.List.title?.let { stringResource(it) } ?: "",
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        selected = when (currentScreen) {
                            Screen.List -> true
                            else -> false
                        },
                        onClick = {
                            if (currentScreen != Screen.List) {
                                onNavigateDetails(Screen.List)
                            }
                            onClose()
                        },
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = Screen.Game.title?.let { stringResource(it) } ?: "",
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        selected = when (currentScreen) {
                            Screen.Game -> true
                            else -> false
                        },
                        onClick = {
                            if (currentScreen != Screen.Game) {
                                onNavigateDetails(Screen.Game)
                            }
                            onClose()
                        },
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = Screen.Settings.title?.let { stringResource(it) } ?: "",
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        selected = when (currentScreen) {
                            Screen.Settings -> true
                            else -> false
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        },
                        onClick = {
                            if (currentScreen != Screen.Settings) {
                                onNavigateDetails(Screen.Settings)
                            }
                            onClose()
                        },
                    )
                }
            }
        },
        drawerState = drawerState,
    ) {
        content()
    }
}