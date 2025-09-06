package dev.aftly.flags.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerDefaults
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
import dev.aftly.flags.model.GameMode
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentScreen: Screen?,
    isGesturesEnabled: Boolean,
    gameMode: GameMode,
    onGameMode: (GameMode) -> Unit,
    onClose: () -> Unit,
    onNavigateDetails: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val cardColorsSelected = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
    val cardColorsUnselected = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    val cardColorsUnselected2 = CardDefaults.cardColors(
        containerColor = DrawerDefaults.modalContainerColor
    )

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(drawerState = drawerState) {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.medium16)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    /* -------- App drawer title (App icon & name) -------- */
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.flags_icon_circle),
                            contentDescription = null,
                            modifier = Modifier.height(Dimens.defaultListItemHeight48),
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.padding(Dimens.medium16),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    /* -------- List/View screen -------- */
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
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.List,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            if (currentScreen != Screen.List) {
                                onNavigateDetails(Screen.List)
                            }
                            onClose()
                        },
                    )

                    /* -------- Game screen -------- */
                    Box(contentAlignment = Alignment.CenterEnd) {
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
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Games,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                if (currentScreen != Screen.Game) {
                                    onNavigateDetails(Screen.Game)
                                }
                                onClose()
                            },
                        )

                        Row(modifier = Modifier.padding(end = Dimens.medium12)) {
                            /* NAMES game mode button */
                            Card(
                                onClick = {
                                    onGameMode(GameMode.NAMES)

                                    if (gameMode != GameMode.NAMES && currentScreen == Screen.Game)
                                        onClose()
                                },
                                shape =
                                    if (gameMode == GameMode.NAMES) MaterialTheme.shapes.large
                                    else MaterialTheme.shapes.extraSmall,
                                colors =
                                    if (gameMode == GameMode.NAMES) cardColorsSelected
                                    else if (currentScreen == Screen.Game) cardColorsUnselected2
                                    else cardColorsUnselected,
                            ) {
                                Text(
                                    text = stringResource(GameMode.NAMES.title),
                                    modifier = Modifier.padding(Dimens.small8),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }

                            Spacer(Modifier.width(4.dp))

                            /* DATES game mode button */
                            Card(
                                onClick = {
                                    onGameMode(GameMode.DATES)

                                    if (gameMode != GameMode.DATES && currentScreen == Screen.Game)
                                        onClose()
                                },
                                shape =
                                    if (gameMode == GameMode.DATES) MaterialTheme.shapes.large
                                    else MaterialTheme.shapes.extraSmall,
                                colors =
                                    if (gameMode == GameMode.DATES) cardColorsSelected
                                    else if (currentScreen == Screen.Game) cardColorsUnselected2
                                    else cardColorsUnselected,
                            ) {
                                Text(
                                    text = stringResource(GameMode.DATES.title),
                                    modifier = Modifier.padding(Dimens.small8),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.small8))

                    /* -------- Settings screen -------- */
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
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = isGesturesEnabled,
    ) {
        content()
    }
}