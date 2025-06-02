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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SortByAlpha
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.ScoreData
import dev.aftly.flags.model.TimeOverview
import dev.aftly.flags.model.TotalsOverview
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.successDark
import dev.aftly.flags.ui.theme.successLight
import dev.aftly.flags.ui.util.SystemUiController

@Composable
fun ScoreDetails(
    visible: Boolean,
    insetsPadding: PaddingValues,
    gameFlags: List<FlagResources>,
    guessedFlags: List<FlagResources>,
    guessedFlagsSorted: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    skippedFlagsSorted: List<FlagResources>,
    shownFlags: List<FlagResources>,
    shownFlagsSorted: List<FlagResources>,
    isTimeTrial: Boolean,
    timeTrialStart: Int?,
    time: Int,
    onClose: () -> Unit,
) {
    /* Properties for controlling system bars */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(window, view) }
    val isDarkTheme by rememberUpdatedState(newValue = isSystemInDarkTheme())
    var isDetailsSorted by rememberSaveable { mutableStateOf(value = false) }
    LaunchedEffect(visible) {
        if (visible) {
            isDetailsSorted = false
            systemUiController.setLightStatusBar(light = false)
        }
    }

    /* Class for processing score details */
    val scoreDetails = ScoreData(
        gameFlags = gameFlags,
        guessedFlags = guessedFlags,
        guessedFlagsSorted = guessedFlagsSorted,
        skippedFlags = skippedFlags,
        skippedFlagsSorted = skippedFlagsSorted,
        shownFlags = shownFlags,
        shownFlagsSorted = shownFlagsSorted,
        guessedFlagsTitle = R.string.game_score_details_guessed,
        skippedFlagsTitle = R.string.game_score_details_skipped,
        shownFlagsTitle = R.string.game_score_details_shown,
        remainderFlagsTitle = R.string.game_score_details_remainder,
        isTimeTrial = isTimeTrial,
        timeTrialStart = timeTrialStart,
        time = time,
    )


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

                    Row {
                        IconButton(
                            onClick = { isDetailsSorted = !isDetailsSorted }
                        ) {
                            Icon(
                                imageVector = when (isDetailsSorted) {
                                    false -> Icons.Default.SortByAlpha
                                    true -> Icons.Default.AccessTime
                                },
                                contentDescription = null,
                            )
                        }

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
                    isDetailsSorted = isDetailsSorted,
                    scoreDetails = scoreDetails,
                )
            }
        }
    }
}


@Composable
private fun ScoreDetailsItems(
    isDarkTheme: Boolean,
    isDetailsSorted: Boolean,
    scoreDetails: ScoreData,
) {
    val scrollState = rememberLazyListState()
    var overviewExpanded by remember { mutableStateOf(value = true) }
    val expansionMap = remember { mutableStateMapOf<Int, Boolean>() }

    val overviewDropDownIcon = when (overviewExpanded) {
        true -> Icons.Default.ArrowDropUp
        false -> Icons.Default.ArrowDropDown
    }

    val lerpSurface = when (isDarkTheme) {
        true -> Color.White
        false -> Color.Black
    }

    val itemFontSize = 14.sp
    val itemLineHeight = 18.sp


    /* Nested LazyColumns for score details contents */
    LazyColumn(state = scrollState) {
        /* Overview item containing overview items */
        item {
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
                /* Interactable overview title */
                TextButton(
                    onClick = { overviewExpanded = !overviewExpanded },
                    modifier = Modifier.padding(horizontal = Dimens.extraSmall4),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.game_score_details_overview),
                        )
                        Icon(
                            imageVector = overviewDropDownIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                /* Drop-down content of score overview items */
                AnimatedVisibility(
                    visible = overviewExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(
                                start = Dimens.medium16,
                                end = Dimens.medium16,
                                bottom = Dimens.medium16,
                            )
                    ) {
                        TotalsOverviewItem(
                            totalsOverview = scoreDetails.scoreOverview.totalsOverview,
                            isDarkTheme = isDarkTheme,
                            fontSize = itemFontSize,
                            lineHeight = itemLineHeight,
                        )

                        Spacer(modifier = Modifier.height(Dimens.extraSmall4))

                        TimeOverviewItem(
                            timeOverview = scoreDetails.scoreOverview.timeOverview,
                            isDarkTheme = isDarkTheme,
                            fontSize = itemFontSize,
                            lineHeight = itemLineHeight,
                        )
                    }
                }
            }
        }

        /* Score details items */
        items(scoreDetails.allScores) { scores ->
            val isExpanded = expansionMap[scores.titleResId] ?: false

            val scoresList = when (isDetailsSorted) {
                false -> scores.list
                true -> scores.sortedList
            }

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
                        items(
                            count = scoresList.size,
                            key = { index -> scoresList[index].id }
                        ) { index ->
                            ScoreItem(
                                flag = scoresList[index],
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


/* Totals overview row strings */
@Composable
private fun TotalsOverviewItem(
    modifier: Modifier = Modifier,
    totalsOverview: TotalsOverview,
    isDarkTheme: Boolean,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val spacePadding = Dimens.extraSmall4 * fontScale

    val successColor = when (isDarkTheme) {
        true -> successDark
        false -> successLight
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /* Title */
        Text(
            text = stringResource(R.string.game_score_details_correct_title),
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = lineHeight,
            style = MaterialTheme.typography.titleSmall,
        )

        /* Absolute score */
        Text(
            text = stringResource(
                R.string.game_score_details_correct_absolute,
                totalsOverview.correctAnswers,
                totalsOverview.outOfCount
            ),
            modifier = Modifier.padding(horizontal = spacePadding)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )

        /* Relative score */
        Text(
            text = stringResource(
                R.string.game_score_details_correct_relative,
                totalsOverview.scorePercent
            ) + stringResource(R.string.string_percent),
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                .background(successColor)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = MaterialTheme.colorScheme.surface,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    }
}


/* Time overview row strings */
@Composable
private fun TimeOverviewItem(
    modifier: Modifier = Modifier,
    timeOverview: TimeOverview,
    isDarkTheme: Boolean,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val spacePadding = Dimens.extraSmall4 * fontScale

    val timeModeResId = when (timeOverview.isTimeTrial) {
        false -> R.string.game_score_details_time_mode_1
        true -> R.string.game_score_details_time_mode_2
    }

    val endTimeBackgroundColor = when (timeOverview.isTimeTrial) {
        false -> MaterialTheme.colorScheme.primary
        true -> MaterialTheme.colorScheme.error
    }

    val endTimeTextColor = when (timeOverview.isTimeTrial) {
        false -> MaterialTheme.colorScheme.onPrimary
        true -> MaterialTheme.colorScheme.onError
    }


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /* Title */
        Text(
            text = stringResource(R.string.game_score_details_time_mode_title),
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = lineHeight,
            style = MaterialTheme.typography.titleSmall,
        )

        /* Time mode type */
        Text(
            text = stringResource(timeModeResId),
            modifier = Modifier.padding(horizontal = spacePadding),
            fontSize = fontSize,
            lineHeight = lineHeight,
        )

        /* Time trial start time */
        timeOverview.timeTrialStart?.let { time ->
            Text(
                text = stringResource(
                    R.string.game_score_details_time_mode_details,
                    time / 60,
                    time % 60
                ),
                modifier = Modifier.padding(end = spacePadding)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 2.dp, horizontal = Dimens.small8),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = fontSize,
                lineHeight = lineHeight,
            )
        }

        /* Time elapsed */
        Text(
            text = stringResource(
                R.string.game_score_details_time_mode_details,
                timeOverview.time / 60,
                timeOverview.time % 60
            ),
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                .background(endTimeBackgroundColor)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = endTimeTextColor,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
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