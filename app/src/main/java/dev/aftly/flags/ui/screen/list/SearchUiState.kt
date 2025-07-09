package dev.aftly.flags.ui.screen.list

//import androidx.annotation.StringRes TODO
import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class SearchUiState(
    val allFlags: List<FlagResources> = allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    //val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All, TODO
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),
    //@StringRes val currentCategoryTitle: Int = currentSuperCategory.title, TODO
)
