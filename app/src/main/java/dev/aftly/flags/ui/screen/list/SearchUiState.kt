package dev.aftly.flags.ui.screen.list

import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class SearchUiState(
    val allFlags: List<FlagResources> = allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All,
    val currentSuperCategories: List<FlagSuperCategory>? = null,
    val currentSubCategories: List<FlagCategory>? = null,
    @StringRes val currentCategoryTitle: Int = currentSuperCategory.title,
)
