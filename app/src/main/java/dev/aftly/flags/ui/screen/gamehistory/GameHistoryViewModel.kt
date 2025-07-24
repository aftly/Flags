package dev.aftly.flags.ui.screen.gamehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.toScoreData
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameHistoryViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val scoreItemsRepository =
        (application as FlagsApplication).container.scoreItemsRepository
    private val _uiState = MutableStateFlow(GameHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Boolean>("isGameOver")?.let { isGameOver ->
            _uiState.update { it.copy(isGameOver = isGameOver) }
        }

        viewModelScope.launch {
            scoreItemsRepository.getAllItemsStream().collect { items ->
                _uiState.update { it.copy(scores = items) }
            }
        }
    }


    fun updateScoreDetails(scoreItem: ScoreItem?) {
        _uiState.update {
            it.copy(
                scoreDetails = scoreItem?.toScoreData(
                    flagsGuessedSorted = sortFlags(scoreItem.flagsGuessed),
                    flagsSkippedGuessedSorted = sortFlags(scoreItem.flagsSkippedGuessed),
                    flagsSkippedSorted = sortFlags(scoreItem.flagsSkipped),
                    flagsShownSorted = sortFlags(scoreItem.flagsShown),
                ),
            )
        }
    }


    fun toggleScoreDetails() {
        _uiState.update { it.copy(isScoreDetails = !it.isScoreDetails) }
    }


    private fun sortFlags(flags: List<FlagResources>): List<FlagResources> {
        val application = getApplication<Application>()
        return sortFlagsAlphabetically(application, flags)
    }
}