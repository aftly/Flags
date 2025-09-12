package dev.aftly.flags.ui.screen.fullscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.ui.util.getFlagFromId
import dev.aftly.flags.ui.util.getFlagIdsFromString
import dev.aftly.flags.ui.util.getFlagsFromIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FullScreenViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(value = FullScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val flagIdArg = savedStateHandle.get<Int>("flagId")
        val flagIdsArg = savedStateHandle.get<String>("flagIds")

        if (flagIdArg != null && flagIdsArg != null) {
            val flag = getFlagFromId(id = flagIdArg)
            val flagIds = getFlagIdsFromString(string = flagIdsArg)
            val flags = getFlagsFromIds(flagIds)

            _uiState.value = FullScreenUiState(flag, flags)
        }
    }

    fun updateCurrentFlag(flag: FlagView) {
        _uiState.update { it.copy(flag = flag) }
    }
}