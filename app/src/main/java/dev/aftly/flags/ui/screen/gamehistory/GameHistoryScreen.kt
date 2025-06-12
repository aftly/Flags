package dev.aftly.flags.ui.screen.gamehistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.model.ScoreData
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.ScoreDetails
import dev.aftly.flags.ui.theme.Dimens

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
        onNavigateUp = onNavigateUp,
    )
}


@Composable
private fun GameHistoryScreen(
    modifier: Modifier = Modifier,
    uiState: GameHistoryUiState,
    screen: Screen,
    onCloseScoreDetails: () -> Unit,
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
            /*
            GameHistoryContent(
                modifier = Modifier.padding(scaffoldPadding),
                scoreHistory = uiState.scores
            )
             */
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
    scoreHistory: List<ScoreData>,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal16)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                count = scoreHistory.size,
                key = { index -> scoreHistory[index].timeStamp }
            ) { index ->
                HistoryItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = scoreHistory[index],
                )
            }
        }
    }
}


@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    item: ScoreData,
) {
    Row(modifier = modifier) {
        Text(text = "hello")
    }
}


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