package dev.aftly.flags.ui.screen.fullscreen

import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagResources

data class FullscreenUiState(
    val initialFlag: FlagResources = nullFlag,
    val currentFlagId: Int = 0,
    @StringRes val currentFlagTitle: Int = 0,
    val flags: List<FlagResources> = emptyList(),
)
