package dev.aftly.flags.ui.screen.fullscreen

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dev.aftly.flags.data.DataSource.flagsMapId
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
        val flagArg = savedStateHandle.get<Int>("flag")
        val flagsArg = savedStateHandle.get<String>("flags")

        if (flagArg != null && flagsArg != null) {
            val flag = flagsMapId.getValue(flagArg)
            val flags = flagsArg.split(",").mapNotNull { it.toIntOrNull() }

            _uiState.value = FullscreenUiState(
                initialFlag = flag,
                currentFlagId = flag.id,
                currentFlagTitle = flag.flagOf,
                flags = flagsMapId.filterKeys { it in flags }.values.toList(),
            )
        }
    }

    fun updateCurrentFlagId(
        flagId: Int,
        @StringRes flagTitle: Int,
    ) {
        _uiState.update { it.copy(currentFlagId = flagId, currentFlagTitle = flagTitle) }
    }
}