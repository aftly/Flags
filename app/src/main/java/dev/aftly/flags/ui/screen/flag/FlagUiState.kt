package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.data.DataSource.nullFlag

data class FlagUiState(
    val flag: FlagResources = nullFlag,
    val descriptionStringResIds: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
)
