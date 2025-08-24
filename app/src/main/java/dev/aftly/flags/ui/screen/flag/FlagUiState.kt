package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.RelatedFlagsContent
import dev.aftly.flags.model.RelatedFlagsMenu

data class FlagUiState(
    val flag: FlagView = nullFlag,
    val flagKey: String? = null,
    val politicalRelatedFlagsContent: RelatedFlagsContent.Political? = null,
    val chronologicalRelatedFlagsContent: RelatedFlagsContent.Chronological? = null,
    val flagIdsFromList: List<Int> = emptyList(),
    val descriptionResIds: List<Int> = emptyList(),
    val descriptionClickableWordIndexes: List<Int> = emptyList(),
    val descriptionBoldWordIndexes: List<Int> = emptyList(),
    val descriptionLightWordIndexes: List<Int> = emptyList(),
    val initRelatedFlag: FlagView? = null,
    val isRelatedFlagNav: RelatedFlagsMenu? = null,
    val savedFlags: List<SavedFlag> = emptyList(),
    val savedFlag: SavedFlag? = null,
)