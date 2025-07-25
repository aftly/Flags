package dev.aftly.flags.ui.component

import android.app.Activity
import android.icu.text.NumberFormat
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aftly.flags.R
import dev.aftly.flags.model.CategoriesOverview
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.GuessedFlags
import dev.aftly.flags.model.RemainderFlags
import dev.aftly.flags.model.ScoreData
import dev.aftly.flags.model.ShownFlags
import dev.aftly.flags.model.SkippedFlags
import dev.aftly.flags.model.SkippedGuessedFlags
import dev.aftly.flags.model.TimeMode
import dev.aftly.flags.model.TimeOverview
import dev.aftly.flags.model.TitledList
import dev.aftly.flags.model.TotalsOverview
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.successDark
import dev.aftly.flags.ui.theme.successLight
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.SystemUiController


@Composable
fun ScoreDetails(
    visible: Boolean,
    scoreDetails: ScoreData,
    onClose: () -> Unit,
) {
    /* For Ui details */
    val insetsPadding = WindowInsets.systemBars.asPaddingValues()
    var isDetailsSorted by rememberSaveable { mutableStateOf(value = false) }
    val sortButtonIcon = when (isDetailsSorted) {
        false -> Icons.Default.SortByAlpha
        true -> Icons.Default.AccessTime
    }

    /* For controlling system bars */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(view, window) }
    val isDarkTheme = LocalDarkTheme.current

    LaunchedEffect(visible) {
        if (visible) {
            isDetailsSorted = false /* Reset to chronological order on re-open */
            systemUiController.setLightStatusBar(light = false)
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        /* Scrim to mimic Compose BasicAlertDialog()'s */
        AnimatedVisibility(
            visible = visible,
            enter = EnterTransition.None,
            exit = ExitTransition.None,
        ) {
            Scrim(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.575f)),
                onAction = {
                    /* Dismiss ScoreDetails on tap off */
                    if (!isDarkTheme) systemUiController.setLightStatusBar(light = true)
                    onClose()
                },
            )
        }


        /* Score details card */
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 4 }),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = insetsPadding.calculateTopPadding(),
                        bottom = insetsPadding.calculateBottomPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16
                    ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                /* Card title bar */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                                imageVector = sortButtonIcon,
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

                /* Score details content */
                ScoreDetailsItems(
                    isDarkTheme = isDarkTheme,
                    isDetailsSorted = isDetailsSorted,
                    scoreDetails = scoreDetails,
                )
            }
        }
    }
}


/* Score details content */
@Composable
private fun ScoreDetailsItems(
    isDarkTheme: Boolean,
    isDetailsSorted: Boolean,
    scoreDetails: ScoreData,
) {
    val lerpSurface = when (isDarkTheme) {
        true -> Color.White
        false -> Color.Black
    }
    val itemFontSize = 14.sp
    val itemLineHeight = 18.sp


    LazyColumn {
        /* Overview item containing overview items */
        item {
            ScoreOverViewItem(
                isDarkTheme = isDarkTheme,
                scoreDetails = scoreDetails,
                lerpSurface = lerpSurface,
                itemFontSize = itemFontSize,
                itemLineHeight = itemLineHeight,
            )
        }

        items(scoreDetails.allScores) { scoreDetails ->
            ScoresItem(
                isDetailsSorted = isDetailsSorted,
                scoreDetails = scoreDetails,
                lerpSurface = lerpSurface,
                itemFontSize = itemFontSize,
                itemLineHeight = itemLineHeight,
            )
        }
    }
}


/* Top level overview item containing Totals and Time overview items */
@Composable
private fun ScoreOverViewItem(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    scoreDetails: ScoreData,
    lerpSurface: Color,
    itemFontSize: TextUnit,
    itemLineHeight: TextUnit,
) {
    var isExpanded by remember { mutableStateOf(value = true) }

    val dropDownIcon = when (isExpanded) {
        true -> Icons.Default.ArrowDropUp
        false -> Icons.Default.ArrowDropDown
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.small8,
                end = Dimens.small8,
                bottom = Dimens.small8,
            ),
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
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.padding(horizontal = Dimens.extraSmall4),
            shape = MaterialTheme.shapes.large,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.game_score_details_overview),
                )
                Icon(
                    imageVector = dropDownIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        /* Drop-down content of score overview items */
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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

                CategoriesOverviewItem(
                    categoriesOverview = scoreDetails.scoreOverview.categoriesOverview,
                    fontSize = itemFontSize,
                    lineHeight = itemLineHeight,
                )

                Spacer(modifier = Modifier.height(Dimens.extraSmall4))

                TimeOverviewItem(
                    timeOverview = scoreDetails.scoreOverview.timeOverview,
                    fontSize = itemFontSize,
                    lineHeight = itemLineHeight,
                )
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

    /* Manage locale aware percentage that excludes redundant 0 decimals */
    val locale = configuration.locales[0]
    val percentFormat = NumberFormat.getPercentInstance(locale)
    percentFormat.maximumFractionDigits = 2
    val scorePercent = percentFormat.format(totalsOverview.scorePercent.toDouble() / 100)

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
            modifier = Modifier
                .padding(horizontal = spacePadding)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )

        /* Relative score */
        Text(
            text = scorePercent,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(successColor)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = MaterialTheme.colorScheme.surface,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    }
}


/* Totals overview row strings */
@Composable
private fun CategoriesOverviewItem(
    modifier: Modifier = Modifier,
    categoriesOverview: CategoriesOverview,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val spacePadding = 2.dp * fontScale

    Column(
        modifier = modifier
    ) {
        /* Title */
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.game_score_details_categories_title),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = lineHeight,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        /* If game list is saved flags */
        if (categoriesOverview.superCategories.isEmpty() &&
            categoriesOverview.subCategories.isEmpty()) {
            CategoryItem(
                topPadding = spacePadding,
                stringResId = R.string.saved_flags_score_detailed,
                fontSize = fontSize,
                lineHeight = lineHeight,
                isPrimary = true,
            )
        } else {
            /* Super-categories */
            categoriesOverview.superCategories.forEach { superCategory ->
                superCategory.gameScoreCategoryDetailed?.let {
                    CategoryItem(
                        topPadding = spacePadding,
                        stringResId = it,
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        isPrimary = true,
                    )
                }
            }

            /* Sub-categories */
            categoriesOverview.subCategories.forEach { subCategory ->
                CategoryItem(
                    topPadding = spacePadding,
                    stringResId = subCategory.title,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    isPrimary = false,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacePadding))
    }
}

@Composable
private fun CategoryItem(
    topPadding: Dp,
    @StringRes stringResId: Int,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    isPrimary: Boolean,
) {
    Row(
        modifier = Modifier.padding(top = topPadding)
    ) {
        Text(
            text = stringResource(stringResId),
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color =
                    if (isPrimary) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                )
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color =
                if (isPrimary) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.surface,
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
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val spacePadding = Dimens.extraSmall4 * fontScale

    val endTimeBackgroundColor = when (timeOverview.timeMode) {
        TimeMode.STANDARD -> MaterialTheme.colorScheme.primary
        TimeMode.TIME_TRIAL -> MaterialTheme.colorScheme.error
    }

    val endTimeTextColor = when (timeOverview.timeMode) {
        TimeMode.STANDARD -> MaterialTheme.colorScheme.onPrimary
        TimeMode.TIME_TRIAL -> MaterialTheme.colorScheme.onError
    }


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /* Title */
        Text(
            text = stringResource(R.string.game_score_details_time_mode),
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = lineHeight,
            style = MaterialTheme.typography.titleSmall,
        )

        /* Time mode type */
        Text(
            text = stringResource(timeOverview.timeMode.title),
            modifier = Modifier.padding(horizontal = spacePadding),
            fontSize = fontSize,
            lineHeight = lineHeight,
        )

        /* Time trial start time */
        timeOverview.timeTrialStart?.let { time ->
            Text(
                text = stringResource(
                    R.string.game_score_details_time,
                    time / 60, time % 60
                ),
                modifier = Modifier
                    .padding(end = spacePadding)
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
                R.string.game_score_details_time,
                timeOverview.time / 60, timeOverview.time % 60
            ),
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(endTimeBackgroundColor)
                .padding(vertical = 2.dp, horizontal = Dimens.small8),
            color = endTimeTextColor,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    }
}


/* Top level score details item containing lazy list of ScoreItem() */
@Composable
private fun ScoresItem(
    modifier: Modifier = Modifier,
    isDetailsSorted: Boolean,
    scoreDetails: TitledList,
    lerpSurface: Color,
    itemFontSize: TextUnit,
    itemLineHeight: TextUnit,
) {
    var isExpanded by remember { mutableStateOf(value = false) }

    val lazyColumnHeight = (with(LocalDensity.current) {
        itemLineHeight.toDp()
    } + Dimens.medium16) * (scoreDetails.list.size + 1)

    val scoreTitle = when (scoreDetails) {
        is GuessedFlags ->
            stringResource(R.string.game_score_details_guessed, scoreDetails.list.size)
        is SkippedGuessedFlags ->
            stringResource(R.string.game_score_details_skipped_guessed, scoreDetails.list.size)
        is SkippedFlags ->
            stringResource(R.string.game_score_details_skipped, scoreDetails.list.size)
        is ShownFlags ->
            stringResource(R.string.game_score_details_shown, scoreDetails.list.size)
        is RemainderFlags ->
            stringResource(R.string.game_score_details_remainder, scoreDetails.list.size)
        else ->
            ""
    }

    val scoreTitleColor = when (scoreDetails.list.size) {
        0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    val scoresList = when (isDetailsSorted) {
        false -> scoreDetails.list
        true -> scoreDetails.sortedList
    }

    val textButtonIsEnabled = when (scoreDetails.list.size) {
        0 -> false
        else -> true
    }

    val dropDownIcon = when (isExpanded) {
        true -> Icons.Default.ArrowDropUp
        false -> Icons.Default.ArrowDropDown
    }


    /* Card for encapsulating each score details group */
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.small8,
                end = Dimens.small8,
                bottom = Dimens.small8,
            ),
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
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.padding(horizontal = Dimens.extraSmall4),
            enabled = textButtonIsEnabled,
            shape = MaterialTheme.shapes.large,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = scoreTitle,
                    color = scoreTitleColor,
                )
                if (scoreDetails.list.isNotEmpty()) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.medium16,
                        end = Dimens.medium16,
                        bottom = Dimens.medium16,
                    )
                    .heightIn(max = lazyColumnHeight)
            ) {
                items(
                    count = scoreDetails.list.size,
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