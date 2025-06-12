package dev.aftly.flags.ui.screen.gamehistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.data.room.ScoreItem
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.ScoreDetails
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.formatTimestamp

@Composable
fun GameHistoryScreen(
    viewModel: GameHistoryViewModel = viewModel(),
    screen: Screen,
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    /* When language configuration changes, update strings in uiState */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }

    GameHistoryScreen(
        screen = screen,
        uiState = uiState,
        onCloseScoreDetails = { viewModel.toggleScoreDetails() },
        onScoreDetails = {
            viewModel.updateScoreDetails(it)
            viewModel.toggleScoreDetails()
        },
        onNavigateUp = onNavigateUp,
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
        modifier = modifier.fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal16)
    ) {
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


@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    item: ScoreItem,
    onScoreDetails: (ScoreItem) -> Unit,
) {
    val padding = Dimens.extraSmall4
    val dateTime = formatTimestamp(item.timestamp).replace(oldValue = " ", newValue = "\n")
    val categoryIcon = Icons.Default.Category
    val scoreIcon = Icons.Default.SportsScore
    val timeModeIcon = Icons.Default.Timer
    val dateTimeIcon = Icons.Default.DateRange
    val container = MaterialTheme.colorScheme.primary
    val content = MaterialTheme.colorScheme.onPrimary

    Card(onClick = { onScoreDetails(item) }) {
        Row(
            modifier = modifier.padding(all = padding / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TODO figure padding
            /* Card {  } */
            Row(
                modifier = Modifier
                    .padding(horizontal = padding, vertical = padding / 2)
                    .clip(MaterialTheme.shapes.medium)
                    .background(container)
            ) {
                Icon(
                    imageVector = dateTimeIcon,
                    contentDescription = null,
                    tint = content,
                )
                Text(
                    text = dateTime,
                    color = content,
                    style = MaterialTheme.typography.labelMedium,
                    lineHeight = MaterialTheme.typography.labelMedium.lineHeight * 0.75f
                )
            }
        }
    }
    //Spacer(modifier = Modifier.height(padding))
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
            screen.title?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
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