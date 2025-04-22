package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.data.DataSource.nullFlag

data class FlagUiState(
    val flag: FlagResources = nullFlag,
    /* For use in deriving and converting to strings in ViewModel */
    val descriptionStringResIds: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    /* For use in making annotated strings in @Composable function(s) */
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
)
