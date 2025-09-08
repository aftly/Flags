package dev.aftly.flags.ui.screen.gamehistory

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.game.TimeMode
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.ResultsType
import dev.aftly.flags.ui.component.ScoreDetails
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.successDark
import dev.aftly.flags.ui.theme.successLight
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.formatTimestamp
import dev.aftly.flags.ui.util.getSingleCategoryPreviewTitleOrNull
import dev.aftly.flags.ui.util.isCategoriesEmpty
import dev.aftly.flags.ui.util.superCategories
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameHistoryScreen(
    viewModel: GameHistoryViewModel = viewModel(),
    screen: Screen,
    onNavigateUp: (Boolean) -> Unit, // isGameOver Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    /* When language configuration changes, update strings in uiState */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }

    GameHistoryScreen(
        screen = screen,
        uiState = uiState,
        onCloseScoreDetails = {
            scope.launch {
                viewModel.toggleScoreDetails()
                delay(timeMillis = Timing.COMPOSE_DEFAULT.toLong())
                viewModel.updateScoreDetails(null)
            }
        },
        onScoreDetails = {
            scope.launch {
                viewModel.updateScoreDetails(it)
                delay(timeMillis = 50)
                viewModel.toggleScoreDetails()
            }
        },
        onNavigateUp = { onNavigateUp(uiState.isFromGameOver) },
    )
}


@Composable
private fun GameHistoryScreen(
    modifier: Modifier = Modifier,
    uiState: GameHistoryUiState,
    screen: Screen,
    onCloseScoreDetails: () -> Unit,
    onScoreDetails: (ScoreItem) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GameHistoryTopBar(
                    screen = screen,
                    onNavigateUp = onNavigateUp,
                )
            },
        ) { scaffoldPadding ->
            GameHistoryContent(
                modifier = Modifier.padding(scaffoldPadding),
                scoreHistory = uiState.scores,
                onScoreDetails = onScoreDetails,
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */

        // TODO: FilterHistoryButton()

        if (uiState.scoreDetails != null) {
            ScoreDetails(
                visible = uiState.isScoreDetails,
                scoreDetails = uiState.scoreDetails,
                onClose = onCloseScoreDetails,
            )
        }
    }
}


@Composable
private fun GameHistoryContent(
    modifier: Modifier = Modifier,
    scoreHistory: List<ScoreItem>,
    onScoreDetails: (ScoreItem) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal16)
    ) {
        if (scoreHistory.isEmpty()) {
            NoResultsFound(
                modifier = Modifier.fillMaxSize(),
                resultsType = ResultsType.GAME_HISTORY,
                bottomSpacer = true,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    count = scoreHistory.size,
                    key = { index -> scoreHistory[index].timestamp }
                ) { index ->
                    HistoryItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = scoreHistory[index],
                        onScoreDetails = onScoreDetails,
                    )
                }
            }
        }
    }
}


@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    item: ScoreItem,
    onScoreDetails: (ScoreItem) -> Unit,

) {
    /* General properties */
    val isDarkTheme = LocalDarkTheme.current
    val isTimeTrial = item.timeMode == TimeMode.TIME_TRIAL
    val padding = Dimens.extraSmall4
    val iconBackgroundColor = MaterialTheme.colorScheme.surface
    val detailsRowModifier = Modifier
        .padding(vertical = padding)
        .background(
            color = iconBackgroundColor,
            shape = MaterialTheme.shapes.medium
        )
    val timerRowModifier = Modifier.background(
        color = iconBackgroundColor,
        shape = MaterialTheme.shapes.large
    )
    @StringRes val categoryTitle =
        if (item.isCategoriesEmpty()) R.string.saved_flags_score_preview
        else getSingleCategoryPreviewTitleOrNull(
            superCategories = item.superCategories(),
            subCategories = item.gameSubCategories,
        ) ?: R.string.game_history_category_multiple


    /* Icon properties */
    val iconSize = Dimens.standardIconSize24 * 0.85f
    val categoryIcon = Icons.Default.Category
    val scoreIcon = Icons.Default.SportsScore
    val timeModeIcon = Icons.Default.Timer
    val timeModeTypeIcon = when (isTimeTrial) {
        true -> Icons.Default.ArrowDownward
        false -> Icons.Default.ArrowUpward
    }
    val dateTimeIcon = Icons.Default.DateRange


    /* Card properties */
    val scoreCardColors = CardDefaults.cardColors(
        containerColor = when (isDarkTheme) {
            true -> successDark
            false -> successLight
        },
        contentColor = MaterialTheme.colorScheme.surface
    )
    val timerCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    val timerRemainingCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
    )
    val dateTimeCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )


    Card(modifier = modifier.clickable { onScoreDetails(item) }) {
        Row(
            modifier = Modifier
                .padding(horizontal = padding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            /* Score */
            Row(
                modifier = detailsRowModifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = scoreIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = padding / 2)
                        .size(iconSize),
                )

                Card(colors = scoreCardColors) {
                    Text(
                        text = stringResource(R.string.game_history_score, item.score, item.outOf),
                        modifier = Modifier.padding(horizontal = padding, vertical = padding / 2),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.small8))


            /* Category */
            Row(
                modifier = detailsRowModifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = padding / 2)
                        .size(iconSize),
                )

                Card(colors = scoreCardColors) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(categoryTitle),
                            modifier = Modifier.padding(horizontal = padding, vertical = padding / 2),
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.labelLarge.lineHeight * 0.75f,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Dimens.small8))


            /* Time details */
            Row(
                modifier = timerRowModifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = timeModeIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                )
                Icon(
                    imageVector = timeModeTypeIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = padding / 2)
                        .size(iconSize),
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    item.timerStart?.let { startTime ->
                        if (isTimeTrial) {
                            Card(colors = timerCardColors) {
                                Text(
                                    text = stringResource(
                                        R.string.game_score_details_time,
                                        startTime / 60, startTime % 60
                                    ),
                                    modifier = Modifier
                                        .padding(
                                            horizontal = padding,
                                            vertical = padding / 4,
                                        ),
                                    textAlign = TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.labelLarge.lineHeight * 0.75f,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                    Card(
                        colors = if (isTimeTrial) timerRemainingCardColors else timerCardColors
                    ) {
                        val verticalPadding = if (isTimeTrial) padding / 4 else padding / 1.5f

                        Text(
                            text = stringResource(
                                R.string.game_score_details_time,
                                item.timerEnd / 60, item.timerEnd % 60
                            ),
                            modifier = Modifier
                                .padding(
                                    horizontal = padding,
                                    vertical = verticalPadding,
                                ),
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.labelLarge.lineHeight * 0.75f,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Dimens.small8))


            /* Date & Time of game end */
            Row(
                modifier = detailsRowModifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = dateTimeIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = padding / 2)
                        .size(iconSize),
                )

                Card(colors = dateTimeCardColors) {
                    Text(
                        text = formatTimestamp(item.timestamp).replace(
                            oldValue = stringResource(R.string.game_score_details_time_date_connector),
                            newValue = stringResource(R.string.string_new_line),
                        ),
                        modifier = Modifier.padding(horizontal = padding, vertical = padding / 2),
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.labelMedium.lineHeight * 0.75f,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(padding))
}


/*
@Composable
private fun ItemElement(
    icon: ImageVector,
    text: String,
    style: FontStyle,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Row {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = text)
        }
    }
}
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameHistoryTopBar(
    modifier: Modifier = Modifier,
    screen: Screen,
    onNavigateUp: () -> Unit,
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
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
    )
}