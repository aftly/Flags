package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeLower
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    var searchQuery by mutableStateOf(value = "")
        private set

    var isSearchQuery by mutableStateOf(value = false)
        private set

    /* Holds first item in sorted list if searchQuery is exact match */
    private var firstItem by mutableStateOf<FlagResources?>(value = null)

    /* Flows for combining in searchResults */
    private val flagsFlow = uiState.map { it.currentFlags }
    private val searchQueryFlow = snapshotFlow { searchQuery }
    private var appResources = MutableStateFlow(
        value = getApplication<Application>().applicationContext.resources
    )
    private var the = MutableStateFlow(value = appResources.value.getString(R.string.string_the))

    val searchResults = combine(
        flow = searchQueryFlow,
        flow2 = flagsFlow,
        flow3 = appResources,
        flow4 = the
    ) { query, flags, res, the ->
            when {
                query.isNotEmpty() -> flags.filter { flag ->
                    /* Handle searchQuery matching flag's common name */
                    normalizeLower(res.getString(flag.flagOf)).let { flagOf ->
                        if (flag.isFlagOfThe && normalizeLower(query).startsWith(the)) {
                            (the + flagOf).contains(normalizeLower(query))
                        } else {
                            flagOf.contains(normalizeLower(query))
                        }
                    }.let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch) {
                            val search = normalizeLower(query)
                            val flagOf = if (flag.isFlagOfThe && search.startsWith(the)) {
                                the + normalizeLower(res.getString(flag.flagOf))
                            } else {
                                normalizeLower(res.getString(flag.flagOf))
                            }

                            if (flagOf == search) firstItem = flag
                        }
                        return@let isMatch
                    }.or(
                        /* Handle searchQuery matching flag's official name */
                        other = normalizeLower(res.getString(flag.flagOfOfficial)).let { official ->
                            if (flag.isFlagOfOfficialThe && normalizeLower(query).startsWith(the)) {
                                (the + official).contains(normalizeLower(query))
                            } else {
                                official.contains(normalizeLower(query))
                            }
                        }
                    ).let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch) {
                            val search = normalizeLower(query)
                            val official = if (flag.isFlagOfOfficialThe && search.startsWith(the)) {
                                the + normalizeLower(res.getString(flag.flagOfOfficial))
                            } else {
                                normalizeLower(res.getString(flag.flagOfOfficial))
                            }

                            if (official == search) firstItem = flag
                        }
                        return@let isMatch
                    }.or(
                        /* Handle searchQuery matching any of flag's alt names if applicable */
                        other = flag.flagOfAlternate?.any { alt ->
                            normalizeLower(res.getString(alt)).contains(normalizeLower(query))
                        } ?: false
                    ).let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch && flag.flagOfAlternate != null) {
                            if (flag.flagOfAlternate.any { alt ->
                                normalizeLower(res.getString(alt)) == normalizeLower(query) }) {
                                firstItem = flag
                            }
                        }
                        return@let isMatch
                    }.or(
                        /* Handle search queries matching info of a flag's sovereign state */
                        other = flag.sovereignState?.let { sovereignState ->
                            val sov = flagsMap.getValue(sovereignState)

                            normalizeLower(res.getString(sov.flagOf)).let { flagOf ->
                                if (sov.isFlagOfThe && normalizeLower(query).startsWith(the)) {
                                    (the + flagOf).contains(normalizeLower(query))
                                } else {
                                    flagOf.contains(normalizeLower(query))
                                }
                            }.or(
                                other = normalizeLower(res.getString(sov.flagOfOfficial)).let {
                                    official ->
                                    if (sov.isFlagOfOfficialThe &&
                                        normalizeLower(query).startsWith(the)) {
                                        (the + official).contains(normalizeLower(query))
                                    } else {
                                        official.contains(normalizeLower(query))
                                    }
                                }
                            ).or(
                                other = sov.flagOfAlternate?.any { alt ->
                                    normalizeLower(res.getString(alt))
                                        .contains(normalizeLower(query))
                                } ?: false
                            )
                        } ?: false
                    ).or(
                        /* Handle search queries matching info of a flag's associated state */
                        other = flag.associatedState?.let { associatedState ->
                            val ass = flagsMap.getValue(associatedState)

                            normalizeLower(res.getString(ass.flagOf)).let { flagOf ->
                                if (ass.isFlagOfThe && normalizeLower(query).startsWith(the)) {
                                    (the + flagOf).contains(normalizeLower(query))
                                } else {
                                    flagOf.contains(normalizeLower(query))
                                }
                            }.or(
                                other = normalizeLower(res.getString(ass.flagOfOfficial)).let {
                                    official ->
                                    if (ass.isFlagOfOfficialThe &&
                                        normalizeLower(query).startsWith(the)) {
                                        (the + official).contains(normalizeLower(query))
                                    } else {
                                        official.contains(normalizeLower(query))
                                    }
                                }
                            ).or(
                                other = ass.flagOfAlternate?.any { alt ->
                                    normalizeLower(res.getString(alt))
                                        .contains(normalizeLower(query))
                                } ?: false
                            )
                        } ?: false
                    ).or(
                        /* Handle search queries matching info of flags that have the flag as
                         * their sovereignState value */
                        other = reverseFlagsMap.getValue(flag).let { sovereign ->
                            val search = normalizeLower(query)
                            val isThe = search.startsWith(the)

                            for (item in flags) {
                                if (item.sovereignState == sovereign) {
                                    val flagOf = normalizeLower(res.getString(item.flagOf))
                                    val official =
                                        normalizeLower(res.getString(item.flagOfOfficial))

                                    if (item.isFlagOfThe && isThe &&
                                        (the + flagOf).contains(search)) {
                                        return@let true
                                    } else if (flagOf.contains(search)) {
                                        return@let true
                                    }

                                    if (item.isFlagOfOfficialThe && isThe &&
                                        (the + official).contains(search)) {
                                        return@let true
                                    } else if (official.contains(search)) {
                                        return@let true
                                    }

                                    if (item.flagOfAlternate?.any { alt ->
                                        normalizeLower(res.getString(alt))
                                            .contains(search) } == true) {
                                        return@let true
                                    }
                                }
                            }
                            return@let false
                        }
                    ).or(
                        /* Handle search queries matching info of flags that have the flag as
                         * their associatedState value */
                        other = reverseFlagsMap.getValue(flag).let { associated ->
                            val search = normalizeLower(query)
                            val isThe = search.startsWith(the)

                            for (item in flags) {
                                if (item.associatedState == associated) {
                                    val flagOf = normalizeLower(res.getString(item.flagOf))
                                    val official =
                                        normalizeLower(res.getString(item.flagOfOfficial))

                                    if (item.isFlagOfThe && isThe &&
                                        (the + flagOf).contains(search)) {
                                        return@let true
                                    } else if (flagOf.contains(search)) {
                                        return@let true
                                    }

                                    if (item.isFlagOfOfficialThe && isThe &&
                                        (the + official).contains(search)) {
                                        return@let true
                                    } else if (official.contains(search)) {
                                        return@let true
                                    }

                                    if (item.flagOfAlternate?.any { alt ->
                                        normalizeLower(res.getString(alt))
                                            .contains(search) } == true) {
                                        return@let true
                                    }
                                }
                            }
                            return@let false
                        }
                    )
                }.sortedWith { p1, p2 ->
                    /* When firstItem is in list, sort it to first position, else no sorting */
                    when {
                        p1 == firstItem && p2 != firstItem -> -1
                        p1 != firstItem && p2 == firstItem -> 1
                        else -> 0
                    }
                }
                else -> searchResultsCopy.value /* Maintains list after clearing searchQuery */
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        )
    private val searchResultsCopy: StateFlow<List<FlagResources>> = searchResults

    init { sortFlagsAndUpdate() }


    fun updateResources() {
        appResources.value = getApplication<Application>().applicationContext.resources
        the.value = appResources.value.getString(R.string.string_the)
    }

    fun sortFlagsAndUpdate() {
        val application = getApplication<Application>()

        _uiState.update {
            it.copy(
                allFlags = sortFlagsAlphabetically(application, it.allFlags),
                currentFlags = sortFlagsAlphabetically(application, it.currentFlags),
            )
        }
    }


    /* Updates state with new currentFlags list derived from a single super- or sub- category only
     * Is intended to be called with either a newSuperCategory OR newSubCategory, and a null value */
    fun updateCurrentCategory(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* If new category is All superCategory update flags with static allFlags source,
         * else dynamically generate flags list from category info */
        if (newSuperCategory == All) {
            _uiState.value = SearchUiState()
        } else {
            _uiState.update {
                it.copy(
                    currentFlags = getFlagsByCategory(
                        superCategory = newSuperCategory,
                        subCategory = newSubCategory,
                        allFlags = it.allFlags,
                    ),
                    currentSuperCategories = when (newSuperCategory) {
                        null -> emptyList()
                        else -> listOf(newSuperCategory)
                    },
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


    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery

        isSearchQuery = when (newQuery) {
            "" -> false
            else -> true
        }
        firstItem = null /* Reset exact match state with each change to searchQuery */
    }
}