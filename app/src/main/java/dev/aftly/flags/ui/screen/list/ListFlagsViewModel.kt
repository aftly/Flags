package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.data.DataSource.mutuallyExclusiveMultiSelectSuperCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveMultiSelectSuperCategories2
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.Political
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
                    currentSuperCategories = null,
                    currentSubCategories = null,
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
                    currentSuperCategories = null,
                    currentSubCategories = null,
                )
            }
        }
    }


    fun updateMultiSelectCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        val currentSuperCategory = uiState.value.currentSuperCategory
        var isDeselect = false /* Controls whether flags are updated from current or all flags */

        val superCategories = when (val superCategories = uiState.value.currentSuperCategories) {
            null ->
                when (currentSuperCategory) {
                    Political -> mutableListOf()
                    else -> mutableListOf(currentSuperCategory)
                }
            else -> superCategories.toMutableList()
        }
        val subCategories = when (val subCategories = uiState.value.currentSubCategories) {
            null -> mutableListOf()
            else -> subCategories.toMutableList()
        }


        /* Exit function if newSuperCategory is not selectable or mutually exclusive from current */
        newSuperCategory?.let {
            if (mutuallyExclusiveMultiSelectSuperCategories
                .containsAll(elements = listOf(newSuperCategory, currentSuperCategory))) {
                return
            } else if (mutuallyExclusiveMultiSelectSuperCategories2
                .containsAll(elements = listOf(newSuperCategory, currentSuperCategory))) {
                return
            }
        }
        /* Exit function if newSuperCategory is mutually exclusive from current */
        newSubCategory?.let {
            // TODO
        }


        /* Add/remove argument category to/from categories lists */
        newSuperCategory?.let { superCategory ->
            if (superCategories.isNotEmpty() && superCategory in superCategories) {
                superCategories.remove(superCategory)
                isDeselect = true
            } else if (superCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
                superCategory.subCategories.filterIsInstance<FlagCategory>().first()
                in subCategories) {
                subCategories.remove(
                    superCategory.subCategories.filterIsInstance<FlagCategory>().first()
                )
                isDeselect = true
            } else if (superCategory == All) {
                return
            } else {
                when (newSuperCategory.subCategories.size) {
                    1 -> subCategories.add(superCategory.subCategories
                        .filterIsInstance<FlagCategory>().first())
                    else -> superCategories.add(superCategory)
                }
            }
        }
        newSubCategory?.let { subCategory ->
            if (subCategories.isNotEmpty() && subCategory in subCategories) {
                subCategories.remove(subCategory)
                isDeselect = true
            } else {
                subCategories.add(subCategory)
            }
        }


        /* Get new flags list from categories list and currentFlags or allFlags (depending on
         * processing needs) */
        val newFlags = uiState.value.let { state ->
            if (isDeselect || newSuperCategory == Historical) state.allFlags
            else state.currentFlags
        }.let { flags ->
            when (subCategories.isNotEmpty()) {
                true -> flags.filter { it.categories.containsAll(subCategories) }
                false -> flags
            }
        }.let { flags ->
            when (superCategories.isNotEmpty()) {
                true ->
                    flags.filter { flag ->
                        superCategories.all { superCategory ->
                            flag.categories.any { it in superCategory.subCategories }
                        }
                    }
                false -> flags
            }
        }


        /* Update currentSuperCategory if it aids in ignoring multi-selection of mutually exclusive
         * supers, as per top operation on mutuallyExclusiveMultiSelectSuperCategories(2) */
        val newCurrentSuperCategory = if (currentSuperCategory == All && newSuperCategory != null) {
            newSuperCategory
        } else currentSuperCategory


        /* Update state with new multiSelectCategories list and currentFlags list */
        _uiState.update { currentState ->
            currentState.copy(
                currentSuperCategory = newCurrentSuperCategory,
                currentSuperCategories = superCategories,
                currentSubCategories = subCategories,
                currentFlags = newFlags,
            )
        }
    }
}