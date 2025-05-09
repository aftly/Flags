package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.data.DataSource.allMultiSelectSuperCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveMultiSelectSuperCategories
import dev.aftly.flags.model.FlagCategory
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
     * This function updates state by readable order (of common name) from associated strings */
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

    /* Updates state with new currentFlags list derived from new super- or sub- category
     * Also updates currentSuperCategory and currentCategory title details for FilterFlagsButton UI
     * Is intended to be called with either a newSuperCategory OR newSubCategory, and a null value */
    fun updateCurrentCategory(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* If new category is All superCategory update flags with static allFlags source,
         * else dynamically generate flags list from category info */
        if (newSuperCategory == FlagSuperCategory.All) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentFlags = currentState.allFlags,
                    currentSuperCategory = newSuperCategory,
                    currentCategoryTitle = newSuperCategory.title,
                    multiSelectCategories = null,
                )
            }
        } else {
            /* Determine category title Res from nullable values */
            @StringRes val categoryTitle = getCategoryTitle(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Determine the relevant parent superCategory */
            val parentSuperCategory = getParentSuperCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Get new currentFlags list from function arguments and parent superCategory */
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
                    multiSelectCategories = null,
                )
            }
        }
    }

    fun updateMultiSelectCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* Exit function if newSuperCategory is not selectable or mutually exclusive from current */
        if (newSuperCategory != null && (newSuperCategory !in allMultiSelectSuperCategories ||
            mutuallyExclusiveMultiSelectSuperCategories.containsAll(
                elements = listOf(newSuperCategory, uiState.value.currentSuperCategory)))) {
            return
        }

        /* Init newCategories list from currentCategories or from current selected category */
        val newCategories = when (val currentCategories = uiState.value.multiSelectCategories) {
            null -> uiState.value.currentCategoryTitle.let {
                for (superCategory in allMultiSelectSuperCategories) {
                    if (superCategory.title == it) {
                        return@let superCategory.subCategories.filterIsInstance<FlagCategory>()
                            .toMutableList()
                    }
                }
                for (subCategory in FlagCategory.entries) {
                    if (subCategory.title == it) return@let mutableListOf(subCategory)
                }
                return@let mutableListOf<FlagCategory>()
            }
            else -> currentCategories.toMutableList()
        }

        /* Add argument category(s) to newCategories */
        newSuperCategory?.subCategories?.forEach {
            it as FlagCategory
            newCategories.add(it)
        } ?: newSubCategory?.let { newCategories.add(it) }


        /* Get new flags list from newCategories and currentFlags */
        val newFlags = uiState.value.currentFlags.filter {
            it.categories.containsAll(newCategories)
        }


        /* Update state with new multiSelectCategories list and currentFlags list */
        _uiState.update { currentState ->
            currentState.copy(
                multiSelectCategories = newCategories,
                currentFlags = newFlags,
            )
        }
    }
}