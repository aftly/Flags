package dev.aftly.flags.ui.screen.gamehistory

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.data.room.scorehistory.ScoreItem
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
        onCheckedItem = { viewModel.onCheckedItem(item = it) },
        onCheckAllItems = { viewModel.onCheckAllItems(checkAll = it) },
        onDeleteCheckedItems = { viewModel.onDeleteCheckedItems() },
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
    onCheckedItem: (ScoreItem) -> Unit,
    onCheckAllItems: (Boolean) -> Unit,
    onDeleteCheckedItems: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    var isInit by rememberSaveable { mutableStateOf(value = false) }
    var isDeleteMode by rememberSaveable { mutableStateOf(value = false) }
    var isAllCheckbox by rememberSaveable { mutableStateOf(value = false) }
    val allItemIds = uiState.scoreItems.map { it.id }.toSet()
    val isCheckedAll = uiState.checkedScoreItems.keys == allItemIds
    val isCheckedAny = uiState.checkedScoreItems.isNotEmpty()

    /* Manage top bar (check all) checkbox state */
    LaunchedEffect(isCheckedAll) {
        if (isCheckedAll) isAllCheckbox = true
    }
    LaunchedEffect(isCheckedAny) {
        if (!isCheckedAny) isAllCheckbox = false
    }

    /* Reset state when delete mode is disabled (after init) */
    LaunchedEffect(isDeleteMode) {
        if (!isDeleteMode && isInit) {
            onCheckAllItems(false)
            isAllCheckbox = false
        } else {
            isInit = true
        }
    }

    BackHandler {
        if (isDeleteMode) isDeleteMode = false
        else onNavigateUp()
    }

    Box(modifier = modifier.fillMaxSize()) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GameHistoryTopBar(
                    screen = screen,
                    isDeleteButton = uiState.scoreItems.isNotEmpty(),
                    isDeleteMode = isDeleteMode,
                    isCheckedAny = isCheckedAny,
                    isCheckedAll = isCheckedAll,
                    isCheckedInit = isAllCheckbox,
                    onDeleteAction = {
                        if (isDeleteMode && isCheckedAny) {
                            onDeleteCheckedItems()
                        } else {
                            isDeleteMode = !isDeleteMode
                        }
                    },
                    onCheckboxAction = {
                        onCheckAllItems(!isAllCheckbox)
                        isAllCheckbox = !isAllCheckbox
                    },
                    onNavigateUp = onNavigateUp,
                )
            },
        ) { scaffoldPadding ->
            GameHistoryContent(
                modifier = Modifier.padding(scaffoldPadding),
                isDeleteMode = isDeleteMode,
                checkedIds = uiState.checkedScoreItems.keys,
                scoreItems = uiState.scoreItems,
                onScoreDetails = onScoreDetails,
                onCheckedItem = onCheckedItem,
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */

        // TODO: FilterHistoryButton()

        if (uiState.scoreDetails != null) {
            ScoreDetails(
                visible = uiState.isScoreDetails,
                scoreData = uiState.scoreDetails,
                onClose = onCloseScoreDetails,
            )
        }
    }
}


@Composable
private fun GameHistoryContent(
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean,
    checkedIds: Set<Int>,
    scoreItems: List<ScoreItem>,
    onScoreDetails: (ScoreItem) -> Unit,
    onCheckedItem: (ScoreItem) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal16)
    ) {
        if (scoreItems.isEmpty()) {
            NoResultsFound(
                modifier = Modifier.fillMaxSize(),
                resultsType = ResultsType.GAME_HISTORY,
                bottomSpacer = true,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    count = scoreItems.size,
                    key = { index -> scoreItems[index].timestamp }
                ) { index ->
                    val item = scoreItems[index]

                    HistoryItem(
                        modifier = Modifier.fillMaxWidth(),
                        isDeleteMode = isDeleteMode,
                        isChecked = checkedIds.contains(item.id),
                        item = item,
                        onScoreDetails = onScoreDetails,
                        onCheckedItem = onCheckedItem,
                    )
                }
            }
        }
    }
}


@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean,
    isChecked: Boolean,
    item: ScoreItem,
    onScoreDetails: (ScoreItem) -> Unit,
    onCheckedItem: (ScoreItem) -> Unit,
) {
    /* General properties */
    val isDarkTheme = LocalDarkTheme.current
    val padding = Dimens.extraSmall4
    val itemElementModifier = Modifier
        .padding(vertical = padding)
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
        )
    val categoryResId =
        if (item.isCategoriesEmpty()) R.string.saved_flags_score_preview
        else getSingleCategoryPreviewTitleOrNull(
            superCategories = item.superCategories(),
            subCategories = item.gameSubCategories,
        ) ?: R.string.game_history_category_multiple

    /* Icon properties */
    val iconSize = Dimens.standardIconSize24 * 0.85f
    val categoryIcon = Icons.Default.Category
    val scoreIcon = Icons.Default.SportsScore
    val modeIcon = Icons.Default.Settings
    val timestampIcon = Icons.Default.CalendarMonth

    /* Card properties */
    val scoreCardColors = CardDefaults.cardColors(
        containerColor = if (isDarkTheme) successDark else successLight,
        contentColor = MaterialTheme.colorScheme.surface
    )
    val detailCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    val detailCardColors2 = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
    val modeCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
    )

    /* Element properties */
    val scoreText = stringResource(R.string.game_history_score, item.score, item.outOf)
    val categoryText = stringResource(categoryResId)
    val timestampText = formatTimestamp(item.timestamp).replace(
        oldValue = stringResource(R.string.game_score_details_time_date_connector),
        newValue = stringResource(R.string.string_new_line),
    )

    Row(
        modifier = modifier.padding(bottom = padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(modifier = Modifier
            .weight(1f)
            .clickable { onScoreDetails(item) }) {
            Row(
                modifier = Modifier
                    .padding(horizontal = padding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                /* ----- Score item ----- */
                HistoryItemElement(
                    modifier = itemElementModifier,
                    isPaddingEnd = true,
                    icon = scoreIcon,
                    iconSize = iconSize,
                    padding = padding,
                    cardColors = scoreCardColors,
                ) {
                    HistoryItemElementText(text = scoreText, padding = padding)
                }

                /* ----- Category item ----- */
                HistoryItemElement(
                    modifier = itemElementModifier.weight(1f),
                    isPaddingEnd = true,
                    icon = categoryIcon,
                    iconSize = iconSize,
                    padding = padding,
                    cardColors = detailCardColors,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        HistoryItemElementText(text = categoryText, padding = padding)
                    }
                }

                /* ----- Mode item ----- */
                HistoryItemElement(
                    modifier = itemElementModifier,
                    isPaddingEnd = true,
                    icon = modeIcon,
                    iconSize = iconSize,
                    padding = padding,
                    cardColors = modeCardColors,
                ) {
                    Row(modifier = Modifier.padding(all = padding / 2)) {
                        /* Answer mode */
                        HistoryItemElementIcon(icon = item.answerMode.icon, iconSize = iconSize)

                        /* Difficulty mode */
                        item.difficultyMode.icon?.let { icon ->
                            HistoryItemElementIcon(Modifier.padding(horizontal = 2.dp), icon, iconSize)
                        } ?: item.difficultyMode.guessLimit?.let { guessLimit ->
                            HistoryItemElementText(text = guessLimit.toString(), padding = padding)
                        }

                        /* Time mode */
                        HistoryItemElementIcon(icon = item.timeMode.icon, iconSize = iconSize)
                    }
                }

                /* ----- Timestamp item ----- */
                HistoryItemElement(
                    modifier = itemElementModifier,
                    icon = timestampIcon,
                    iconSize = iconSize,
                    padding = padding,
                    cardColors = detailCardColors2,
                ) {
                    HistoryItemElementText(text = timestampText, padding = padding)
                }
            }
        }

        AnimatedVisibility(
            visible = isDeleteMode,
            enter = scaleIn(animationSpec = tween(durationMillis = Timing.SCALE_IN)),
            exit = ExitTransition.None,
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onCheckedItem(item) },
                modifier = Modifier
                    .padding(start = Dimens.small8)
                    .size(Dimens.standardIconSize24)
            )
        }
    }
}


@Composable
private fun HistoryItemElement(
    modifier: Modifier = Modifier,
    isPaddingEnd: Boolean = false,
    icon: ImageVector,
    iconSize: Dp,
    padding: Dp,
    cardColors: CardColors,
    elementContent: @Composable (() -> Unit),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(start = padding / 2)
                .size(iconSize),
        )

        Card(colors = cardColors) {
            elementContent()
        }
    }

    if (isPaddingEnd) {
        Spacer(modifier = Modifier.width(Dimens.small8))
    }
}

@Composable
private fun HistoryItemElementText(
    modifier: Modifier = Modifier,
    text: String,
    padding: Dp,
) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = padding, vertical = padding / 2),
        textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.labelMedium.lineHeight * 0.75f,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun HistoryItemElementIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp,
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(iconSize),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameHistoryTopBar(
    modifier: Modifier = Modifier,
    screen: Screen,
    isDeleteButton: Boolean,
    isDeleteMode: Boolean,
    isCheckedAny: Boolean,
    isCheckedAll: Boolean,
    isCheckedInit: Boolean,
    onDeleteAction: () -> Unit,
    onCheckboxAction: (Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val deleteIconColor =
        if (!isDeleteButton) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        else if (isDeleteMode && isCheckedAny) MaterialTheme.colorScheme.error
        else LocalContentColor.current

    val deleteIconEndPadding = if (isDeleteMode) 0.dp else Dimens.small8

    val checkedColor =
        if (isCheckedAll == isCheckedInit) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary

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
        actions = {
            IconButton(
                onClick = onDeleteAction,
                modifier = Modifier.padding(end = deleteIconEndPadding),
                enabled = isDeleteButton,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = deleteIconColor,
                )
            }

            AnimatedVisibility(
                visible = isDeleteMode && isDeleteButton,
                enter = scaleIn(animationSpec = tween(durationMillis = Timing.SCALE_IN)),
                exit = ExitTransition.None,
            ) {
                Checkbox(
                    checked = isCheckedInit,
                    onCheckedChange = onCheckboxAction,
                    colors = CheckboxDefaults.colors(checkedColor = checkedColor)
                )
            }
            /*
            if (isDeleteMode && isDeleteButton) {

            }
             */
        }
    )
}