package dev.aftly.flags.ui.screen.fullscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.util.getFlagFromId
import dev.aftly.flags.ui.util.getFlagIdsFromString
import dev.aftly.flags.ui.util.getFlagsFromIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FullscreenViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(FullscreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val flagIdArg = savedStateHandle.get<Int>("flagId")
        val flagIdsArg = savedStateHandle.get<String>("flagIds")

        if (flagIdArg != null && flagIdsArg != null) {
            val flag = getFlagFromId(id = flagIdArg)
            val flagIds = getFlagIdsFromString(string = flagIdsArg)
            val flags = getFlagsFromIds(flagIds)

            _uiState.value = FullscreenUiState(flag, flags)
        }
    }

    fun updateCurrentFlag(flag: FlagResources) {
        _uiState.update { it.copy(flag = flag) }
    }
}