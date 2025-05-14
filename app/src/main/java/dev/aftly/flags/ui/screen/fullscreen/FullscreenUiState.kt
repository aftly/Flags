package dev.aftly.flags.ui.screen.fullscreen

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagResources

data class FullscreenUiState(
    val initialFlag: FlagResources = nullFlag,
    val currentFlagId: Int = 0,
    val flags: List<FlagResources> = emptyList(),
)
