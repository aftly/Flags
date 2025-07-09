package dev.aftly.flags.ui.screen.list

import androidx.annotation.StringRes // TODO
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class ListFlagsUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    //val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All, TODO
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),
    //@StringRes val currentCategoryTitle: Int = currentSuperCategory.title, TODO
)
