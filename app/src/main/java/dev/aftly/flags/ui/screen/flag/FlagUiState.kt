package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagView

data class FlagUiState(
    val flag: FlagView = nullFlag,
    val flagKey: String? = null,
    val externalRelatedFlags: List<FlagView> = emptyList(),
    val flagIdsFromList: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
    val initExtRelatedFlag: FlagView = nullFlag,
    val isExtRelatedFlagNavigation: Boolean = false,
    val savedFlags: List<SavedFlag> = emptyList(),
    val savedFlag: SavedFlag? = null,
)