package dev.aftly.flags.ui.screen.fullscreen

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagResources

data class FullscreenUiState(
    val initialFlag: FlagResources = nullFlag,
    val currentFlag: FlagResources = nullFlag,
    val flags: List<FlagResources> = emptyList(),
)
