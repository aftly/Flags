package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag

data class FlagUiState(
    val flag: FlagResources = nullFlag,
    val flagKey: String? = null,
    val relatedFlags: List<FlagResources> = emptyList(),
    val flagIdsFromList: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
    val initRelatedFlag: FlagResources = nullFlag,
    val isRelatedFlagNavigation: Boolean = false,
    val savedFlags: List<SavedFlag> = emptyList(),
    val savedFlag: SavedFlag? = null,
)