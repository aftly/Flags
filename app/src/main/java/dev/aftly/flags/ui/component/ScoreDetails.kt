package dev.aftly.flags.ui.component

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.SystemUiController
import kotlin.math.exp

@Composable
fun ScoreDetails(
    visible: Boolean,
    insetsPadding: PaddingValues,
    guessedFlags: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    shownFlags: List<FlagResources>,
    onClose: () -> Unit,
) {
    /* Properties for controlling system bars */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(window, view) }
    val isDarkTheme by rememberUpdatedState(newValue = isSystemInDarkTheme())
    LaunchedEffect(visible) {
        if (visible) systemUiController.setLightStatusBar(light = false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        /* Scrim with fade animation */
        AnimatedVisibility(
            visible = visible,
            enter = EnterTransition.None,
            exit = ExitTransition.None,
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.575f))
            )
        }

        /* Score details content */
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(),
        ) {
            Card(
                modifier = Modifier
                    .padding(
                        top = insetsPadding.calculateTopPadding(),
                        bottom = insetsPadding.calculateBottomPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16
                    )
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                /* 8.dp short of full padding due to title row IconButton padding */
                Column(
                    modifier = Modifier.padding(Dimens.medium16)
                        .fillMaxWidth()
                ) {
                    /* Title row of details card */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.game_score_details_title),
                            modifier = Modifier.padding(start = Dimens.small8),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        IconButton(
                            onClick = {
                                if (!isDarkTheme) systemUiController.setLightStatusBar(light = true)
                                onClose()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DetailsExpand(
    @StringRes title: Int,
    list: List<FlagResources>,
) {
    var expanded by rememberSaveable { mutableStateOf(value = false) }

    Column {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = Dimens.small8,
                    vertical = Dimens.medium16,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(
                    text = stringResource(title, list.size)
                )
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    count = list.size,
                    key = { index -> list[index].id }
                ) { index ->

                }
            }
        }
    }
}