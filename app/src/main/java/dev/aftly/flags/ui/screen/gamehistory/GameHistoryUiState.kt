package dev.aftly.flags.ui.screen.gamehistory

import dev.aftly.flags.model.ScoreData

data class GameHistoryUiState(
    val scores: List<ScoreData> = emptyList(),
)
