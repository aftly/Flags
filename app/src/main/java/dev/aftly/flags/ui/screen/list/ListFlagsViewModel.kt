package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.ui.util.getCategoryTitle
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getParentSuperCategory
import dev.aftly.flags.ui.util.getSubCategories
import dev.aftly.flags.ui.util.getSuperCategories
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeString
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
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


    fun updateCurrentCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        var isDeselect = false /* Controls whether flags are updated from current or all flags */

        val superCategories = getSuperCategories(
            superCategories = uiState.value.currentSuperCategories,
            currentSuperCategory = uiState.value.currentSuperCategory,
        )
        val subCategories = getSubCategories(
            subCategories = uiState.value.currentSubCategories,
            currentSuperCategory = uiState.value.currentSuperCategory,
        )

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        newSuperCategory?.let { superCategory ->
            if (isSuperCategoryExit(superCategory, superCategories, subCategories)) return

            isDeselect = updateCategoriesFromSuper(
                superCategory = superCategory,
                superCategories = superCategories,
                subCategories = subCategories,
            )
            /* Exit after All is deselected since (how) deselection is state inconsequential */
            if (!isDeselect && superCategory == All) return
        }
        newSubCategory?.let { subCategory ->
            if (isSubCategoryExit(subCategory, subCategories, superCategories)) return

            isDeselect = updateCategoriesFromSub(
                subCategory = subCategory,
                subCategories = subCategories,
            )
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselect) {
            if (subCategories.isEmpty()) {
                if (superCategories.isEmpty()) {
                    return updateCurrentCategory(newSuperCategory = All, newSubCategory = null)

                } else if (superCategories.size == 1) {
                    return updateCurrentCategory(
                        newSuperCategory = superCategories.first(), newSubCategory = null
                    )
                }
            } else if (subCategories.size == 1 && superCategories.size == 1 &&
                superCategories.first().subCategories.size == 1 &&
                subCategories.first() == superCategories.first().subCategories.first()) {
                return updateCurrentCategory(
                    newSuperCategory = superCategories.first(), newSubCategory = null
                )
            }
        }


        /* Get new flags list from categories list and currentFlags or allFlags (depending on
         * processing needs) */
        val newFlags = getFlagsFromCategories(
            allFlags = uiState.value.allFlags,
            currentFlags = uiState.value.currentFlags,
            isDeselect = isDeselect,
            newSuperCategory = newSuperCategory,
            superCategories = superCategories,
            subCategories = subCategories,
        )


        /* Update state with new categories lists and currentFlags list */
        _uiState.update { currentState ->
            currentState.copy(
                currentFlags = newFlags,
                currentSuperCategories = superCategories,
                currentSubCategories = subCategories,
            )
        }
    }
}