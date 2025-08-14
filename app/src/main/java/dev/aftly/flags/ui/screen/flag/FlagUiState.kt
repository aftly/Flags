package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagView

data class FlagUiState(
    val flag: FlagView = nullFlag,
    val flagKey: String? = null,
    val politicalRelatedFlags: List<FlagView> = emptyList(),
    val chronologicalRelatedFlags: List<FlagView> = emptyList(),
    val flagIdsFromList: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
    val initPoliticalRelatedFlag: FlagView = nullFlag,
    val isPoliticalRelatedFlagNavigation: Boolean = false,
    val savedFlags: List<SavedFlag> = emptyList(),
    val savedFlag: SavedFlag? = null,
)