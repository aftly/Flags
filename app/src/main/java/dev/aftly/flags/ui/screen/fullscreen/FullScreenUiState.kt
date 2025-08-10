package dev.aftly.flags.ui.screen.fullscreen

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagView

data class FullScreenUiState(
    val flag: FlagView = nullFlag,
    val flags: List<FlagView> = emptyList(),
)
