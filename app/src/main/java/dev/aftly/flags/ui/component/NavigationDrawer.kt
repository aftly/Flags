package dev.aftly.flags.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentBackStackEntryRoute: String?,
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
                        text = "Flags",
                        modifier = Modifier.padding(Dimens.medium16),
                        style = MaterialTheme.typography.titleLarge
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    NavigationDrawerItem(
                        label = { Text(text = "View") },
                        selected = when (currentBackStackEntryRoute) {
                            Screen.List.route -> true
                            else -> false
                        },
                        onClick = {
                            if (currentBackStackEntryRoute != Screen.List.route) {
                                onNavigateDetails(Screen.List)
                            }
                            onClose()
                        },
                    )

                    NavigationDrawerItem(
                        label = { Text(text = "Game") },
                        selected = currentBackStackEntryRoute?.contains(Screen.Game.route) == true,
                        onClick = {
                            if (currentBackStackEntryRoute?.contains(Screen.Game.route) == false) {
                                onNavigateDetails(Screen.Game)
                            }
                            onClose()
                        },
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    NavigationDrawerItem(
                        label = { Text(text = "Settings") },
                        selected = when (currentBackStackEntryRoute) {
                            Screen.Settings.route -> true
                            else -> false
                        },
                        onClick = {
                            if (currentBackStackEntryRoute != Screen.Settings.route) {
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