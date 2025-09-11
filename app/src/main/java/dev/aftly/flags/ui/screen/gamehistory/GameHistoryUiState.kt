package dev.aftly.flags.ui.screen.gamehistory

import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.game.ScoreData

data class GameHistoryUiState(
    val scoreItems: List<ScoreItem> = emptyList(),
    val checkedScoreItems: Map<Int, ScoreItem> = emptyMap(), /* For delete selection */
    val isScoreDetails: Boolean = false, /* For scoreDetails() component animation purposes */
    val scoreDetails: ScoreData? = null, /* Opens score item with ScoreDetails() */
    val isFromGameOver: Boolean = false,
)