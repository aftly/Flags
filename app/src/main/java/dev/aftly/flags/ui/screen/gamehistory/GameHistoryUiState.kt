package dev.aftly.flags.ui.screen.gamehistory

import dev.aftly.flags.data.room.ScoreItem
import dev.aftly.flags.model.ScoreData

data class GameHistoryUiState(
    val scores: List<ScoreItem> = emptyList(),
    val isScoreDetails: Boolean = false,
    val scoreDetails: ScoreData? = null, /* Opens score item with ScoreDetails() */
)