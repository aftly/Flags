package dev.aftly.flags.ui.screen.list

import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class ListFlagsUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    val savedFlags: List<FlagResources> = emptyList(),
    val isSavedFlags: Boolean = false,
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),
    val isSearchQuery: Boolean = false,
    val isSearchBarInit: Boolean = false, /* Hold initialised state for effects */
    val isSearchBarInitTopBar: Boolean = false, /* Hold initialised state for effects */
)