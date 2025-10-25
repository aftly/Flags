package dev.aftly.flags.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Games
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    isGesturesEnabled: Boolean,
    onClose: () -> Unit,
    onNavigateDetails: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(drawerState = drawerState) {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.medium16)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    /* -------------- TITLE SECTION -------------- */

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.flags_icon_circle),
                            contentDescription = null,
                            modifier = Modifier.height(Dimens.listItemHeight48),
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.padding(Dimens.medium16),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    /* ------------ END OF TITLE SECTION ------------ */


                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))


                    /* -------------- PRIMARY SECTION -------------- */

                    /* List screen item */
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(Screen.List.title),
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        selected = when (currentScreen) {
                            Screen.List -> true
                            else -> false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.List,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onNavigateDetails(Screen.List)
                            onClose()
                        },
                    )

                    /* Game screen item */
                    Box(contentAlignment = Alignment.CenterEnd) {
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = stringResource(Screen.Game.title),
                                    fontWeight = FontWeight.Medium,
                                )
                            },
                            selected = when (currentScreen) {
                                Screen.Game -> true
                                else -> false
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Games,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                onNavigateDetails(Screen.Game)
                                onClose()
                            },
                        )
                    }
                    /* ------------ END OF PRIMARY SECTION ------------ */


                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))


                    /* -------------- SECONDARY SECTION -------------- */

                    /* Settings screen item */
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(Screen.Settings.title),
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        selected = when (currentScreen) {
                            Screen.Settings -> true
                            else -> false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            if (currentScreen != Screen.Settings) {
                                onNavigateDetails(Screen.Settings)
                            }
                            onClose()
                        },
                    )
                    /* ------------ END OF SECONDARY SECTION ------------ */
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = isGesturesEnabled,
    ) {
        content()
    }
}