package dev.aftly.flags.ui.screen.list

import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView

data class ListFlagsUiState(
    val allFlags: List<FlagView> = allFlagsList,
    val currentFlags: List<FlagView> = allFlagsList,
    val savedFlags: List<FlagView> = emptyList(),
    val isSavedFlags: Boolean = false,
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),
    val isSearchQuery: Boolean = false,
    val isSearchBarInit: Boolean = false, /* Hold initialised state for effects */
    val isSearchBarInitTopBar: Boolean = false, /* Hold initialised state for effects */
)