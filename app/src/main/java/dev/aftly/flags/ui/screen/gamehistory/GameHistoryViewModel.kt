package dev.aftly.flags.ui.screen.gamehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GameHistoryUiState())
    val uiState = _uiState.asStateFlow()
}