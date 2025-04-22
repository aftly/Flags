package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.ui.util.getCategoryTitle
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getParentSuperCategory
import dev.aftly.flags.ui.util.normalizeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListFlagsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ListFlagsUiState())
    val uiState: StateFlow<ListFlagsUiState> = _uiState.asStateFlow()

    /* Initialise ListFlagsScreen() with a category not FlagSuperCategory.All
     * Also sort lists by readable name (alphabetically) */
    init {
        sortFlagsAlphabetically()
        updateCurrentCategory(
            newSuperCategory = FlagSuperCategory.SovereignCountry,
            newSubCategory = null,
        )
    }

    /* Default allFlagsList derives from flagsMap key order, not readable name order
     * Function updates state by readable order from associated strings */
    fun sortFlagsAlphabetically() {
        val appResources = getApplication<Application>().applicationContext.resources

        _uiState.update { currentState ->
            currentState.copy(
                allFlags = currentState.allFlags.sortedBy { flag ->
                    normalizeString(string = appResources.getString(flag.flagOf))
                },
                currentFlags = currentState.currentFlags.sortedBy { flag ->
                    normalizeString(string = appResources.getString(flag.flagOf))
                },
            )
        }
    }

    /* Updates state with a new flag list derived from the new super- or sub- category
     * Also updates currentSuperCategory and currentCategoryTitle details for FilterFlagsButton UI
     * Is intended to be called with either a newSuperCategory OR newSubCategory, and a null value */
    fun updateCurrentCategory(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* If the new category is the All super category update flags with static allFlags source,
         * else dynamically generate flags list from category info */
        if (newSuperCategory == FlagSuperCategory.All) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentCategoryTitle = newSuperCategory.title,
                    currentSuperCategory = newSuperCategory,
                    currentFlags = uiState.value.allFlags,
                )
            }
        } else {
            /* Determining category title Res from nullable values */
            @StringRes val categoryTitle = getCategoryTitle(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Determine the relevant parent superCategory */
            val parentSuperCategory = getParentSuperCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Get new flags list from function arguments and parent superCategory */
            val newFlags = getFlagsByCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
                allFlags = uiState.value.allFlags,
                parentCategory = parentSuperCategory,
            )

            _uiState.update { currentState ->
                currentState.copy(
                    currentFlags = newFlags,
                    currentSuperCategory = parentSuperCategory,
                    currentCategoryTitle = categoryTitle,
                )
            }
        }
    }
}


/* Updates state with a new FlagSuperCategory and flag list derived from the new category */
/*
fun updateCurrentSuperCategory(newSuperCategory: FlagSuperCategory) {
    if (newSuperCategory == FlagSuperCategory.All) {
        _uiState.update { currentState ->
            currentState.copy(
                currentCategoryTitle = FlagSuperCategory.All.title,
                currentSuperCategory = FlagSuperCategory.All,
                currentFlagsList = allFlagsList,
            )
        }
    } else {
        /* Using nested loops: For each subcategory in newSuperCategory -> For each flagResource
         * in allFlagsList -> If subcategory in flagResource -> If flagResource not already in
         * MutableList -> add flagResource to MutableList. Then: Update state with .toList() */
        val mutableFlagsList: MutableList<FlagResources> = mutableListOf()

        for (flagResource in uiState.value.allFlagsList) {
            if (newSuperCategory in DataSource.historicalSuperCategoryExceptions &&
                HISTORICAL in flagResource.categories) {
                continue
            }
            for (category in newSuperCategory.subCategories) {
                if (category in flagResource.categories) {
                    if (flagResource !in mutableFlagsList) {
                        mutableFlagsList.add(flagResource)
                    }
                    break
                }
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                currentCategoryTitle = newSuperCategory.title,
                currentSuperCategory = newSuperCategory,
                currentFlagsList = mutableFlagsList.toList(),
            )
        }
    }
}

fun updateCurrentSubCategory(newSubCategory: FlagCategory) {
    val mutableFlagsList: MutableList<FlagResources> = mutableListOf()

    val newSuperCategory = superCategoryList.filterNot {
        it in listOf(FlagSuperCategory.All, FlagSuperCategory.Political)
    }.find { superCategory ->
        newSubCategory in superCategory.subCategories
    } ?: FlagSuperCategory.Political

    for (flagResource in uiState.value.allFlagsList) {
        if (newSubCategory in DataSource.historicalSubCategoryExceptions &&
            HISTORICAL in flagResource.categories) {
            continue
        }
        if (newSubCategory in flagResource.categories) {
            if (flagResource !in mutableFlagsList) {
                mutableFlagsList.add(flagResource)
            }
        }
    }
    _uiState.update { currentState ->
        currentState.copy(
            currentCategoryTitle = newSubCategory.title,
            currentSuperCategory = newSuperCategory,
            currentFlagsList = mutableFlagsList.toList()
        )
    }
}
 */