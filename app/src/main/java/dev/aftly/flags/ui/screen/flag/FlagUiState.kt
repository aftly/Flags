package dev.aftly.flags.ui.screen.flag

import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagScreenContent
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagsContent
import dev.aftly.flags.model.menu.FlagsMenu

data class FlagUiState(
    val flag: FlagView = nullFlag,
    val politicalRelatedFlagsContent: RelatedFlagsContent.Political? = null,
    val chronologicalRelatedFlagsContent: RelatedFlagsContent.Chronological? = null,
    val flagIdsFromList: List<Int> = emptyList(),
    val flagScreenContent: FlagScreenContent? = null,
    val navBackScrollToId: Int = 0,
    val annotatedLinkFrom: List<FlagView> = emptyList(),
    val latestMenuInteraction: FlagsMenu? = null,
    val savedFlags: Set<SavedFlag> = emptySet(),
    val savedFlag: SavedFlag? = null,
)