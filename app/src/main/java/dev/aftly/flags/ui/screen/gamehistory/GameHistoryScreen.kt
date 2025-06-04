package dev.aftly.flags.ui.screen.gamehistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.model.ScoreData
import dev.aftly.flags.navigation.Screen

@Composable
fun GameHistoryScreen(
    viewModel: GameHistoryViewModel = viewModel(),
    screen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    /* When language configuration changes, update strings in uiState */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }

    GameHistoryScaffold(
        screen = screen,
        scoreHistory = uiState.scores,
    )
}


@Composable
private fun GameHistoryScaffold(
    modifier: Modifier = Modifier,
    screen: Screen,
    scoreHistory: List<ScoreData>,
) {
    Box(modifier = modifier.fillMaxSize()) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {},
        ) { scaffoldPadding ->
            GameHistoryContent(
                modifier = Modifier.padding(scaffoldPadding),
                scoreHistory = scoreHistory
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */

        // TODO: FilterHistoryButton()
    }
}


@Composable
private fun GameHistoryContent(
    modifier: Modifier = Modifier,
    scoreHistory: List<ScoreData>,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "History",
            style = MaterialTheme.typography.headlineMedium,
        )

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