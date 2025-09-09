package dev.aftly.flags.ui.component

import android.app.Activity
import android.icu.text.NumberFormat
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.model.game.CategoriesOverview
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode
import dev.aftly.flags.model.game.FailedFlags
import dev.aftly.flags.model.game.GuessedFlags
import dev.aftly.flags.model.game.RemainderFlags
import dev.aftly.flags.model.game.ScoreData
import dev.aftly.flags.model.game.ShownFlags
import dev.aftly.flags.model.game.SkippedFlags
import dev.aftly.flags.model.game.SkippedGuessedFlags
import dev.aftly.flags.model.game.TimeMode
import dev.aftly.flags.model.game.TimeOverview
import dev.aftly.flags.model.game.TitledList
import dev.aftly.flags.model.game.TotalsOverview
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
    val sortButtonIcon =
        if (!isDetailsSorted) Icons.Default.SortByAlpha else Icons.Default.AccessTime

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

    /* Content */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        /* -------- Scrim to mimic BasicAlertDialog() -------- */
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


        /* -------- Score details card -------- */
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
                /* -------- Card title bar -------- */
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

                /* -------- Score details content -------- */
                ScoreDetails(
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
private fun ScoreDetails(
    isDarkTheme: Boolean,
    isDetailsSorted: Boolean,
    scoreDetails: ScoreData,
) {
    val lerpSurface = if (isDarkTheme) Color.White else Color.Black

    LazyColumn {
        item {
            ScoreOverViewContent(
                isDarkTheme = isDarkTheme,
                scoreDetails = scoreDetails,
                lerpSurface = lerpSurface,
            )
        }

        items(scoreDetails.allScores) { scoreDetails ->
            FlagDetailsContent(
                isDetailsSorted = isDetailsSorted,
                scoreDetails = scoreDetails,
                lerpSurface = lerpSurface,
            )
        }
    }
}


/* Overview item containing score totals and game mode info */
@Composable
private fun ScoreOverViewContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    scoreDetails: ScoreData,
    lerpSurface: Color,
) {
    var isExpanded by remember { mutableStateOf(value = true) }

    val dropDownIcon = if (!isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp
    val clippedPadding = 2.dp

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
                TotalsOverview(
                    modifier = Modifier.padding(bottom = Dimens.extraSmall4),
                    totalsOverview = scoreDetails.scoreOverview.totalsOverview,
                    isDarkTheme = isDarkTheme,
                    clippedPadding = clippedPadding,
                )

                CategoriesOverview(
                    modifier = Modifier.padding(bottom = Dimens.extraSmall4),
                    categoriesOverview = scoreDetails.scoreOverview.categoriesOverview,
                    clippedPadding = clippedPadding,
                )

                AnswerOverview(
                    modifier = Modifier.padding(bottom = Dimens.extraSmall4),
                    answerMode = scoreDetails.scoreOverview.answerMode,
                )

                DifficultyOverview(
                    modifier = Modifier.padding(bottom = Dimens.extraSmall4),
                    difficultyMode = scoreDetails.scoreOverview.difficultyMode,
                )

                TimeOverview(
                    timeOverview = scoreDetails.scoreOverview.timeOverview,
                    clippedPadding = clippedPadding,
                )
            }
        }
    }
}


/* Overview component */
@Composable
private fun OverviewItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String?,
    clippedContent: @Composable (RowScope.() -> Unit)?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /* Type */
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
        )

        /* String */
        description?.let {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        clippedContent?.let {
            clippedContent()
        }
    }
}


/* Clipped text component for use in Overview composables */
@Composable
private fun ClippedItem(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    textColor: Color,
) {
    Text(
        text = text,
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .background(color = backgroundColor)
            .padding(vertical = 2.dp, horizontal = Dimens.small8),
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
    )
}


@Composable
private fun TotalsOverview(
    modifier: Modifier = Modifier,
    totalsOverview: TotalsOverview,
    isDarkTheme: Boolean,
    clippedPadding: Dp,
) {
    /* Manage locale aware percentage that excludes redundant 0 decimals */
    val locale = LocalConfiguration.current.locales[0]
    val percentFormat = NumberFormat.getPercentInstance(locale)
    percentFormat.maximumFractionDigits = 2
    val scorePercent = percentFormat.format(totalsOverview.scorePercent.toDouble() / 100)

    val title = stringResource(R.string.game_score_details_correct_title)
    val absoluteScore = stringResource(
        R.string.game_score_details_correct_absolute,
        totalsOverview.correctAnswers,
        totalsOverview.outOfCount
    )
    val successColor = if (isDarkTheme) successDark else successLight

    OverviewItem(
        modifier = modifier,
        title = title,
        description = null,
    ) {
        /* Absolute score */
        ClippedItem(
            text = absoluteScore,
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary,
        )

        /* Relative score */
        ClippedItem(
            modifier = Modifier.padding(start = clippedPadding),
            text = scorePercent,
            backgroundColor = successColor,
            textColor = MaterialTheme.colorScheme.surface,
        )
    }
}


@Composable
private fun CategoriesOverview(
    modifier: Modifier = Modifier,
    categoriesOverview: CategoriesOverview,
    clippedPadding: Dp,
) {
    val title = stringResource(R.string.game_score_details_categories_title)

    Column(modifier = modifier.padding(bottom = clippedPadding)) {
        /* Title */
        OverviewItem(
            title = title,
            description = null,
            clippedContent = null,
        )

        /* If game list is saved flags */
        if (categoriesOverview.superCategories.isEmpty() &&
            categoriesOverview.subCategories.isEmpty()) {
            val savedFlagsText = stringResource(R.string.saved_flags_score_detailed)

            ClippedItem(
                modifier = Modifier.padding(top = clippedPadding),
                text = savedFlagsText,
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary,
            )

        } else {
            /* Super-categories */
            categoriesOverview.superCategories.forEach { superCategory ->
                superCategory.gameScoreCategoryDetailed?.let {
                    val categoryText = stringResource(it)

                    ClippedItem(
                        modifier = Modifier.padding(top = clippedPadding),
                        text = categoryText,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            /* Sub-categories */
            categoriesOverview.subCategories.forEach { subCategory ->
                val categoryText = stringResource(subCategory.title)

                ClippedItem(
                    modifier = Modifier.padding(top = clippedPadding),
                    text = categoryText,
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}


@Composable
private fun AnswerOverview(
    modifier: Modifier = Modifier,
    answerMode: AnswerMode,
) {
    val title = stringResource(R.string.game_score_details_answer_mode)
    val description = stringResource(answerMode.title)

    OverviewItem(
        modifier = modifier,
        title = title,
        description = description,
        clippedContent = null,
    )
}


@Composable
private fun DifficultyOverview(
    modifier: Modifier = Modifier,
    difficultyMode: DifficultyMode,
) {
    val title = stringResource(R.string.game_score_details_difficulty_mode)
    val description = difficultyMode.name

    OverviewItem(
        modifier = modifier,
        title = title,
        description = description,
        clippedContent = null,
    )
}


@Composable
private fun TimeOverview(
    modifier: Modifier = Modifier,
    timeOverview: TimeOverview,
    clippedPadding: Dp,
) {
    val endTimeBackgroundColor = when (timeOverview.timeMode) {
        TimeMode.STANDARD -> MaterialTheme.colorScheme.primary
        TimeMode.TIME_TRIAL -> MaterialTheme.colorScheme.error
    }
    val endTimeTextColor = when (timeOverview.timeMode) {
        TimeMode.STANDARD -> MaterialTheme.colorScheme.onPrimary
        TimeMode.TIME_TRIAL -> MaterialTheme.colorScheme.onError
    }

    val title = stringResource(R.string.game_score_details_time_mode)
    val description = stringResource(timeOverview.timeMode.title)


    OverviewItem(
        modifier = modifier,
        title = title,
        description = description,
    ) {
        Spacer(modifier = Modifier.width(clippedPadding))

        /* Time trial start time */
        timeOverview.timeTrialStart?.let { time ->
            val startTimeText = stringResource(
                R.string.game_score_details_time,
                time / 60, time % 60
            )
            ClippedItem(
                modifier = Modifier.padding(start = clippedPadding),
                text = startTimeText,
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        /* Time elapsed */
        val elapsedTimeText = stringResource(
            R.string.game_score_details_time,
            timeOverview.time / 60, timeOverview.time % 60
        )
        ClippedItem(
            modifier = Modifier.padding(start = clippedPadding),
            text = elapsedTimeText,
            backgroundColor = endTimeBackgroundColor,
            textColor = endTimeTextColor,
        )
    }
}


/* Top level score details item containing lazy list of ScoreItem() */
@Composable
private fun FlagDetailsContent(
    modifier: Modifier = Modifier,
    isDetailsSorted: Boolean,
    scoreDetails: TitledList,
    lerpSurface: Color,
) {
    var isExpanded by remember { mutableStateOf(value = false) }
    val textStyle = MaterialTheme.typography.bodyMedium

    val lazyColumnPadding = Dimens.medium16
    val itemHeight = with(LocalDensity.current) { textStyle.lineHeight.toDp() }
    val lazyColumnHeight = (itemHeight + lazyColumnPadding) * (scoreDetails.list.size + 1)

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
        is FailedFlags ->
            stringResource(R.string.game_score_details_failed, scoreDetails.list.size)
        else ->
            ""
    }

    val itemTitleColor =
        if (scoreDetails.list.isNotEmpty()) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.error

    val itemList = if (isDetailsSorted) scoreDetails.sortedList else scoreDetails.list

    val textButtonIsEnabled = scoreDetails.list.isNotEmpty()

    val dropDownIcon = if (!isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp


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
                    color = itemTitleColor,
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
                        start = lazyColumnPadding,
                        end = lazyColumnPadding,
                        bottom = lazyColumnPadding,
                    )
                    .heightIn(max = lazyColumnHeight)
            ) {
                items(
                    count = scoreDetails.list.size,
                    key = { index -> itemList[index].id }
                ) { index ->
                    FlagItem(
                        flag = itemList[index],
                        textStyle = textStyle,
                    )
                }
            }
        }
    }
}

/* Item composable for the score details lists */
@Composable
private fun FlagItem(
    modifier: Modifier = Modifier,
    flag: FlagView,
    textStyle: TextStyle,
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(flag.flagOf),
            color = MaterialTheme.colorScheme.onSurface,
            style = textStyle,
        )
    }
}