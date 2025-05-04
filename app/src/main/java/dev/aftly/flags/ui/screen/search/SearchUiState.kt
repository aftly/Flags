package dev.aftly.flags.ui.screen.search

import androidx.annotation.StringRes
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class SearchUiState(
    val allFlags: List<FlagResources> = allFlagsList,
    val allFlagsMap: Map<String, FlagResources> = flagsMap,
    val allReverseFlagsMap: Map<FlagResources, String> = reverseFlagsMap,
    val currentFlags: List<FlagResources> = allFlags,
    val currentFlagsMap: Map<String, FlagResources> = allFlagsMap,
    val currentReverseFlagsMap: Map<FlagResources, String> = allReverseFlagsMap,
    val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All,
    @StringRes val currentCategoryTitle: Int = currentSuperCategory.title,
    val theRes: Int = R.string.string_the,
    val the: String = "",
)
