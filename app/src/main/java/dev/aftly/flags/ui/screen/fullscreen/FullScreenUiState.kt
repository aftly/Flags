package dev.aftly.flags.ui.screen.fullscreen

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagResources

data class FullScreenUiState(
    val flag: FlagResources = nullFlag,
    val flags: List<FlagResources> = emptyList(),
)
