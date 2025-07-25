package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag

data class FlagUiState(
    val currentFlag: FlagResources = nullFlag,
    val relatedFlags: List<FlagResources> = emptyList(),
    val flagsFromList: List<Int> = emptyList(),
    val descriptionIdsWhitespaceExceptions: List<Int> = emptyList(),
    val description: List<String> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
    val isNavigatingRelated: Boolean = false,
    val savedFlags: List<SavedFlag> = emptyList(),
    val savedFlag: SavedFlag? = null,
    val isFlagSaved: Boolean = false,
)