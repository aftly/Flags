package dev.aftly.flags.ui.screen.gamehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.model.ScoreData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreItemsRepository =
        (application as FlagsApplication).container.scoreItemsRepository
    private val _uiState = MutableStateFlow(GameHistoryUiState())
    val uiState = _uiState.asStateFlow()

    fun updateScoreDetails(scoreData: ScoreData?) {
        _uiState.update { it.copy(scoreDetails = scoreData) }
    }

    fun toggleScoreDetails() {
        _uiState.update { it.copy(isScoreDetails = !it.isScoreDetails) }
    }
}