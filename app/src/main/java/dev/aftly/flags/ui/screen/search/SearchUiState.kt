package dev.aftly.flags.ui.screen.search

import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagResources

data class SearchUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList
)
