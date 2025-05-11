package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSubCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Historical
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
        val currentSuperCategory = uiState.value.currentSuperCategory
        var isDeselect = false /* Controls whether flags are updated from current or all flags */

        val superCategories = when (val superCategories = uiState.value.currentSuperCategories) {
            null -> mutableListOf(currentSuperCategory)
            else -> superCategories.toMutableList()
        }

        val subCategories = when (val subCategories = uiState.value.currentSubCategories) {
            null ->
                when (currentSuperCategory.subCategories.size) {
                    1 -> mutableListOf(currentSuperCategory.subCategories
                        .filterIsInstance<FlagCategory>().first())
                    else -> mutableListOf<FlagCategory>()
                }
            else -> subCategories.toMutableList()
        }


        /* Exit function if newSuperCategory is not selectable or mutually exclusive from current */
        newSuperCategory?.let { newCategory ->
            mutuallyExclusiveSuperCategories1.let { exclusive1 ->
                if (newCategory !in superCategories && exclusive1.contains(newCategory) &&
                    exclusive1.any { it in superCategories }) {
                    return
                }
            }
            mutuallyExclusiveSuperCategories2.let { exclusive2 ->
                if (newCategory !in superCategories && exclusive2.contains(newCategory) &&
                    exclusive2.any { it in superCategories }) {
                    return
                }
            }
        }
        /* Exit function if newSubCategory is mutually exclusive from current */
        newSubCategory?.let { newCategory ->
            mutuallyExclusiveSubCategories.forEach { superCategory ->
                if (newCategory !in subCategories &&
                    superCategory.subCategories.any { it in subCategories } &&
                    newCategory in superCategory.subCategories) {
                    return
                }
            }
            mutuallyExclusiveSuperCategories1.let { exclusive1 ->
                val subCategorySuper = exclusive1.find { newCategory in it.subCategories }
                if (subCategorySuper != null) {
                    val selectedExclusiveSupers = exclusive1.filter { it in superCategories }
                    val unselectedExclusiveSupers = exclusive1.filterNot { it in superCategories }

                    if (selectedExclusiveSupers.isNotEmpty() &&
                        subCategorySuper in unselectedExclusiveSupers) {
                        return
                    }
                }
            }
            mutuallyExclusiveSuperCategories2.let { exclusive2 ->
                val subCategorySuper = exclusive2.find { newCategory in it.subCategories }
                if (subCategorySuper != null) {
                    val selectedExclusiveSupers = exclusive2.filter { it in superCategories }
                    val unselectedExclusiveSupers = exclusive2.filterNot { it in superCategories }

                    if (selectedExclusiveSupers.isNotEmpty() &&
                        subCategorySuper in unselectedExclusiveSupers) {
                        return
                    }
                }
            }
        }


        /* Add/remove super-category argument to/from categories lists */
        newSuperCategory?.let { superCategory ->
            /* If super in either superCategories or subCategories remove it/them and exit let */
            if (superCategories.isNotEmpty() || subCategories.isNotEmpty()) {
                if (superCategories.isNotEmpty() && superCategory in superCategories) {
                    superCategories.remove(superCategory)
                    isDeselect = true
                }
                if (subCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
                    superCategory.subCategories.first() in subCategories) {
                    subCategories.remove(superCategory.subCategories.first())
                    isDeselect = true
                }
                if (isDeselect) {
                    return if (superCategories.isEmpty() && subCategories.isEmpty()) {
                        updateCurrentCategory(newSuperCategory = All, newSubCategory = null)
                    } else return@let
                }
            }

            if (superCategory == All) {
                return
            } else {
                when (newSuperCategory.subCategories.size) {
                    1 -> {
                        superCategories.add(superCategory)
                        subCategories.add(superCategory.subCategories
                            .filterIsInstance<FlagCategory>().first())
                    }
                    else -> superCategories.add(superCategory)
                }
            }
        }
        /* Add/remove sub-category argument to/from subcategories list */
        newSubCategory?.let { subCategory ->
            if (subCategories.isNotEmpty() && subCategory in subCategories) {
                subCategories.remove(subCategory)
                isDeselect = true

                return if (superCategories.isEmpty() && subCategories.isEmpty()) {
                    updateCurrentCategory(newSuperCategory = All, newSubCategory = null)
                } else return@let

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


        /* Update state with new multiSelectCategories list and currentFlags list */
        _uiState.update { currentState ->
            currentState.copy(
                //currentSuperCategory = newCurrentSuperCategory,
                currentSuperCategories = superCategories,
                currentSubCategories = subCategories,
                currentFlags = newFlags,
            )
        }
    }
}