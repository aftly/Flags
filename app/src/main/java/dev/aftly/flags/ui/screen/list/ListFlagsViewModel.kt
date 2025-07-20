package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getSuperCategories
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ListFlagsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ListFlagsUiState())
    val uiState = _uiState.asStateFlow()

    /* Initialise ListFlagsScreen() with a category not FlagSuperCategory.All
     * Also sort lists by readable name (alphabetically) */
    init {
        sortFlagsAndUpdate()
        updateCurrentCategory(
            newSuperCategory = FlagSuperCategory.SovereignCountry,
            newSubCategory = null,
        )
    }

    /* Default allFlagsList derives from flagsMap key order, not readable name order
     * This function updates state by readable order (of common name) from associated strings */
    fun sortFlagsAndUpdate() {
        val application = getApplication<Application>()

        _uiState.update {
            it.copy(
                allFlags = sortFlagsAlphabetically(application, it.allFlags),
                currentFlags = sortFlagsAlphabetically(application, it.currentFlags),
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
        if (newSuperCategory == All) {
            _uiState.value = ListFlagsUiState()
        } else {
            val newFlags = getFlagsByCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
                allFlags = uiState.value.allFlags,
            )

            _uiState.update {
                it.copy(
                    currentFlags = newFlags,
                    currentSuperCategories = getSuperCategories(
                        superCategory = newSuperCategory,
                        subCategory = newSubCategory,
                        flags = newFlags,
                    ),
                    currentSubCategories = when (newSubCategory) {
                        null -> emptyList()
                        else -> listOf(newSubCategory)
                    },
                )
            }
        }
    }


    fun updateCurrentCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        var isDeselect = false /* Controls whether flags are updated from current or all flags */

        val newSuperCategories = uiState.value.currentSuperCategories.toMutableList()
        val newSubCategories = uiState.value.currentSubCategories.toMutableList()

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        newSuperCategory?.let { superCategory ->
            if (isSuperCategoryExit(superCategory, newSuperCategories, newSubCategories)) return

            isDeselect = updateCategoriesFromSuper(
                superCategory = superCategory,
                superCategories = newSuperCategories,
                subCategories = newSubCategories,
            )
            /* Exit after All is deselected since (how) deselection is state inconsequential */
            if (!isDeselect && superCategory == All) return
        }
        newSubCategory?.let { subCategory ->
            if (isSubCategoryExit(subCategory, newSubCategories, newSuperCategories)) return

            isDeselect = updateCategoriesFromSub(
                subCategory = subCategory,
                subCategories = newSubCategories,
            )
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselect) {
            if (newSubCategories.isEmpty()) {
                if (newSuperCategories.isEmpty()) {
                    return updateCurrentCategory(newSuperCategory = All, newSubCategory = null)

                } else if (newSuperCategories.size == 1) {
                    return updateCurrentCategory(
                        newSuperCategory = newSuperCategories.first(), newSubCategory = null
                    )
                }
            } else if (newSubCategories.size == 1 && newSuperCategories.size == 1 &&
                newSuperCategories.first().subCategories.size == 1 &&
                newSubCategories.first() == newSuperCategories.first().firstCategoryEnumOrNull()) {
                return updateCurrentCategory(
                    newSuperCategory = newSuperCategories.first(), newSubCategory = null
                )
            }
        }

        /* Update state with new categories lists and currentFlags list */
        _uiState.update {
            it.copy(
                /* Get new flags list from categories lists and either currentFlags or allFlags
                 * (depending on select vs. deselect) */
                currentFlags = getFlagsFromCategories(
                    allFlags = it.allFlags,
                    currentFlags = it.currentFlags,
                    isDeselect = isDeselect,
                    newSuperCategory = newSuperCategory,
                    superCategories = newSuperCategories,
                    subCategories = newSubCategories,
                ),
                currentSuperCategories = newSuperCategories,
                currentSubCategories = newSubCategories,
            )
        }
    }


    fun toggleIsSearchBarInit(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInit = isSearchBarInit) }
    }

    fun toggleIsSearchBarInitTopBar(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInitTopBar = isSearchBarInit) }
    }
}