package dev.aftly.flags.ui.component

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.ScoreData
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.SystemUiController

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
                Column {
                    /* Title row of details card */
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(
                                top = Dimens.medium16,
                                start = Dimens.medium16,
                                end = Dimens.medium16,
                                bottom = Dimens.small8,
                            ),
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

                /* Expandable/collapsable score details with nested LazyColumns */
                ScoreDetailsItems(
                    isDarkTheme = isDarkTheme,
                    guessedFlags = guessedFlags,
                    skippedFlags = skippedFlags,
                    shownFlags = shownFlags,
                )
            }
        }
    }
}


@Composable
private fun ScoreDetailsItems(
    isDarkTheme: Boolean,
    guessedFlags: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    shownFlags: List<FlagResources>,
) {
    val scoreDetails = ScoreData(
        guessedFlags = guessedFlags,
        skippedFlags = skippedFlags,
        shownFlags = shownFlags,
    )
    val expansionMap = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberLazyListState()

    val lerpSurface = when (isDarkTheme) {
        true -> Color.White
        false -> Color.Black
    }
    val itemFontSize = 14.sp
    val itemLineHeight = 18.sp


    /* Nested LazyColumns for score details contents */
    LazyColumn(state = scrollState) {
        items(scoreDetails.allScores) { scores ->
            val isExpanded = expansionMap[scores.titleResId] ?: false

            val textButtonIsEnabled = when (scores.list.size) {
                0 -> false
                else -> true
            }

            val scoreTitleColor = when (scores.list.size) {
                0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.primary
            }

            val dropDownIcon = when (isExpanded) {
                true -> Icons.Default.ArrowDropUp
                false -> Icons.Default.ArrowDropDown
            }


            /* Card for encapsulating each score details group */
            Card(
                modifier = Modifier
                    .padding(
                        start = Dimens.small8,
                        end = Dimens.small8,
                        bottom = Dimens.small8,
                    )
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = lerp(
                        start = MaterialTheme.colorScheme.surface,
                        stop = lerpSurface,
                        fraction = 0.125f
                    ),
                )
            ) {
                /* Interactable score group title */
                TextButton(
                    onClick = { expansionMap[scores.titleResId] = !isExpanded },
                    modifier = Modifier.padding(horizontal = Dimens.extraSmall4),
                    enabled = textButtonIsEnabled,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(
                                scores.titleResId,
                                scores.list.size
                            ),
                            color = scoreTitleColor,
                        )
                        if (scores.list.isNotEmpty()) {
                            Icon(
                                imageVector = dropDownIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                /* Drop-down content of score details */
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(
                                max = (with(LocalDensity.current) {
                                    itemLineHeight.toDp()
                                } + Dimens.medium16) * (scores.list.size + 1))
                            .padding(
                                start = Dimens.medium16,
                                end = Dimens.medium16,
                                bottom = Dimens.medium16,
                            )
                    ) {
                        items(scores.list) { item ->
                            ScoreItem(
                                flag = item,
                                fontSize = itemFontSize,
                                lineHeight = itemLineHeight,
                            )
                        }
                    }
                }
            }
        }
    }
}


/* Item composable for the score details lists */
@Composable
private fun ScoreItem(
    modifier: Modifier = Modifier,
    flag: FlagResources,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(flag.flagOf),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    }
}