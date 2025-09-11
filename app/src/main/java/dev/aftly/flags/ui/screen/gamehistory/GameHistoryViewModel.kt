package dev.aftly.flags.ui.screen.gamehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.toScoreData
import dev.aftly.flags.ui.util.getFlagsFromKeys
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
    private val _uiState = MutableStateFlow(value = GameHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Boolean>("isFromGameOver")?.let { isFromGameOver ->
            _uiState.update { it.copy(isFromGameOver = isFromGameOver) }
        }

        viewModelScope.launch {
            scoreItemsRepository.getAllItemsStream().collect { items ->
                _uiState.update { it.copy(scoreItems = items) }
            }
        }
    }

    fun updateScoreDetails(scoreItem: ScoreItem?) {
        _uiState.update {
            it.copy(
                scoreDetails = scoreItem?.toScoreData(
                    flagsGuessedSorted = sortFlags(
                        flags = getFlagsFromKeys(flagKeys = scoreItem.flagsGuessed)
                    ),
                    flagsSkippedGuessedSorted = sortFlags(
                        flags = getFlagsFromKeys(flagKeys = scoreItem.flagsSkippedGuessed)
                    ),
                    flagsSkippedSorted = sortFlags(
                        flags = getFlagsFromKeys(flagKeys = scoreItem.flagsSkipped)
                    ),
                    flagsShownSorted = sortFlags(
                        flags = getFlagsFromKeys(flagKeys = scoreItem.flagsShown)
                    ),
                    flagsFailedSorted = sortFlags(
                        flags = getFlagsFromKeys(flagKeys = scoreItem.flagsFailed)
                    ),
                ),
            )
        }
    }

    fun toggleScoreDetails() {
        _uiState.update { it.copy(isScoreDetails = !it.isScoreDetails) }
    }

    fun onCheckedItem(item: ScoreItem) {
        val checkedItems = uiState.value.checkedScoreItems
        val checkedItemsNew = buildMap {
            when (item) {
                in checkedItems.values ->
                    putAll(from = checkedItems.filterKeys { it != item.id })
                else -> {
                    putAll(from = checkedItems)
                    put(key = item.id, value = item)
                }
            }
        }
        _uiState.update { it.copy(checkedScoreItems = checkedItemsNew) }
    }

    fun onCheckAllItems(checkAll: Boolean) {
        val scoreItems = uiState.value.scoreItems
        val checkedItems = buildMap {
            if (checkAll) scoreItems.forEach { item ->
                put(key = item.id, value = item)
            }
        }
        _uiState.update { it.copy(checkedScoreItems = checkedItems) }
    }

    fun onDeleteCheckedItems() {
        val checkedItems = uiState.value.checkedScoreItems.values

        if (checkedItems.isNotEmpty()) {
            viewModelScope.launch {
                checkedItems.forEach { item ->
                    scoreItemsRepository.deleteItem(item)
                }
                _uiState.update { it.copy(checkedScoreItems = emptyMap()) }
            }
        }
    }

    private fun sortFlags(flags: List<FlagView>): List<FlagView> =
        sortFlagsAlphabetically(getApplication(), flags)
}