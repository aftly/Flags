package dev.aftly.flags.ui.screen.search

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.ui.util.getCategoryTitle
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getParentSuperCategory
import dev.aftly.flags.ui.util.normalizeLower
import dev.aftly.flags.ui.util.normalizeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf(value = "")
        private set

    var isSearchQuery by mutableStateOf(value = false)
        private set

    /* Holds first item in sorted list if searchQuery is exact match */
    private var firstItem by mutableStateOf<FlagResources?>(value = null)

    private val flagsFlow = uiState.map { it.currentFlags }
    val searchResults: StateFlow<List<FlagResources>> = snapshotFlow { searchQuery }
        .combine(flagsFlow) { searchQuery, flags ->
            when {
                searchQuery.isNotEmpty() -> flags.filter { flag ->
                    /* Handle searchQuery matching flag's common name */
                    normalizeLowerRes(flag.flagOf).let { flagOf ->
                        if (flag.isFlagOfThe &&
                            normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                            (uiState.value.the + flagOf).contains(normalizeLower(searchQuery))
                        } else {
                            flagOf.contains(normalizeLower(searchQuery))
                        }
                    }.let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch) {
                            val the = uiState.value.the
                            val query = normalizeLower(searchQuery)
                            val flagOf = if (flag.isFlagOfThe && query.startsWith(the)) {
                                the + normalizeLowerRes(flag.flagOf)
                            } else {
                                normalizeLowerRes(flag.flagOf)
                            }

                            if (flagOf == query) firstItem = flag
                        }
                        return@let isMatch
                    }.or(
                        /* Handle searchQuery matching flag's official name */
                        other = normalizeLowerRes(flag.flagOfOfficial).let { official ->
                            if (flag.isFlagOfOfficialThe &&
                                normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                                (uiState.value.the + official).contains(normalizeLower(searchQuery))
                            } else {
                                official.contains(normalizeLower(searchQuery))
                            }
                        }
                    ).let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch) {
                            val the = uiState.value.the
                            val query = normalizeLower(searchQuery)
                            val official = if (flag.isFlagOfOfficialThe && query.startsWith(the)) {
                                the + normalizeLowerRes(flag.flagOfOfficial)
                            } else {
                                normalizeLowerRes(flag.flagOfOfficial)
                            }

                            if (official == query) firstItem = flag
                        }
                        return@let isMatch
                    }.or(
                        /* Handle searchQuery matching any of flag's alt names if applicable */
                        other = flag.flagOfAlternate?.any { alt ->
                            normalizeLowerRes(alt).contains(normalizeLower(searchQuery))
                        } ?: false
                    ).let { isMatch ->
                        /* If exact match set flag as firstItem in results list */
                        if (isMatch && flag.flagOfAlternate != null) {
                            if (flag.flagOfAlternate.any { alt ->
                                normalizeLowerRes(alt) == normalizeLower(searchQuery) }) {
                                firstItem = flag
                            }
                        }
                        return@let isMatch
                    }.or(
                        /* Handle search queries matching info of a flag's sovereign state */
                        other = flag.sovereignState?.let { sovereignState ->
                            val sovereign = flagsMap.getValue(sovereignState)

                            normalizeLowerRes(sovereign.flagOf).let { flagOf ->
                                if (sovereign.isFlagOfThe &&
                                    normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                                    (uiState.value.the + flagOf)
                                        .contains(normalizeLower(searchQuery))
                                } else {
                                    flagOf.contains(normalizeLower(searchQuery))
                                }
                            }.or(
                                other = normalizeLowerRes(sovereign.flagOfOfficial).let {
                                    official ->
                                    if (sovereign.isFlagOfOfficialThe &&
                                        normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                                        (uiState.value.the + official)
                                            .contains(normalizeLower(searchQuery))
                                    } else {
                                        official.contains(normalizeLower(searchQuery))
                                    }
                                }
                            ).or(
                                other = sovereign.flagOfAlternate?.any { alt ->
                                    normalizeLowerRes(alt).contains(normalizeLower(searchQuery))
                                } ?: false
                            )
                        } ?: false
                    ).or(
                        /* Handle search queries matching info of a flag's associated state */
                        other = flag.associatedState?.let { associatedState ->
                            val associated = flagsMap.getValue(associatedState)

                            normalizeLowerRes(associated.flagOf).let { flagOf ->
                                if (associated.isFlagOfThe &&
                                    normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                                    (uiState.value.the + flagOf)
                                        .contains(normalizeLower(searchQuery))
                                } else {
                                    flagOf.contains(normalizeLower(searchQuery))
                                }
                            }.or(
                                other = normalizeLowerRes(associated.flagOfOfficial).let {
                                    official ->
                                    if (associated.isFlagOfOfficialThe &&
                                        normalizeLower(searchQuery).startsWith(uiState.value.the)) {
                                        (uiState.value.the + official)
                                            .contains(normalizeLower(searchQuery))
                                    } else {
                                        official.contains(normalizeLower(searchQuery))
                                    }
                                }
                            ).or(
                                other = associated.flagOfAlternate?.any { alt ->
                                    normalizeLowerRes(alt).contains(normalizeLower(searchQuery))
                                } ?: false
                            )
                        } ?: false
                    ).or(
                        /* Handle search queries matching info of flags that have the flag as
                         * their sovereignState value */
                        other = reverseFlagsMap.getValue(flag).let { sovereign ->
                            val the = uiState.value.the
                            val query = normalizeLower(searchQuery)
                            val isThe = query.startsWith(the)

                            for (item in flags) {
                                if (item.sovereignState == sovereign) {
                                    val flagOf = normalizeLowerRes(item.flagOf)
                                    val official = normalizeLowerRes(item.flagOfOfficial)

                                    if (item.isFlagOfThe && isThe &&
                                        (the + flagOf).contains(query)) {
                                        return@let true
                                    } else if (flagOf.contains(query)) {
                                        return@let true
                                    }

                                    if (item.isFlagOfOfficialThe && isThe &&
                                        (the + official).contains(query)) {
                                        return@let true
                                    } else if (official.contains(query)) {
                                        return@let true
                                    }

                                    if (item.flagOfAlternate?.any { alt ->
                                        normalizeLowerRes(alt).contains(query) } == true) {
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
                            val the = uiState.value.the
                            val query = normalizeLower(searchQuery)
                            val isThe = query.startsWith(the)

                            for (item in flags) {
                                if (item.associatedState == associated) {
                                    val flagOf = normalizeLowerRes(item.flagOf)
                                    val official = normalizeLowerRes(item.flagOfOfficial)

                                    if (item.isFlagOfThe && isThe &&
                                        (the + flagOf).contains(query)) {
                                        return@let true
                                    } else if (flagOf.contains(query)) {
                                        return@let true
                                    }

                                    if (item.isFlagOfOfficialThe && isThe &&
                                        (the + official).contains(query)) {
                                        return@let true
                                    } else if (official.contains(query)) {
                                        return@let true
                                    }

                                    if (item.flagOfAlternate?.any { alt ->
                                            normalizeLowerRes(alt).contains(query) } == true) {
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
                else -> _searchResults.value /* Maintains list after clearing searchQuery */
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        )
    private val _searchResults = searchResults


    init {
        sortFlagsAlphabetically()
        updateTheString()
    }


    fun sortFlagsAlphabetically() {
        _uiState.update { currentState ->
            currentState.copy(
                allFlags = currentState.allFlags.sortedBy { flag ->
                    normalizeString(string = getString(flag.flagOf))
                },
                currentFlags = currentState.currentFlags.sortedBy { flag ->
                    normalizeString(string = getString(flag.flagOf))
                }
            )
        }
    }

    fun updateTheString() {
        _uiState.update { it.copy(the = getString(it.theRes)) }
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
                )
            }
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

    private fun getString(@StringRes res: Int): String {
        val appResources = getApplication<Application>().applicationContext.resources
        return appResources.getString(res)
    }

    private fun normalizeLowerRes(@StringRes res: Int): String {
        return normalizeLower(string = getString(res))
    }
}