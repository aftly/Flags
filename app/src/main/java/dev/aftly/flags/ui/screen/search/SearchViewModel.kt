package dev.aftly.flags.ui.screen.search

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.util.normalizeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf(value = "")
        private set

    var isSearchQuery by mutableStateOf(value = false)
        private set

    /* Holds first item in sorted list if searchQuery is exact match */
    private var firstItem by mutableStateOf<FlagResources?>(value = null)

    private val appResources = getApplication<Application>().applicationContext.resources

    private val flagsFlow = flowOf(uiState.value.allFlags)
    val searchResults: StateFlow<List<FlagResources>> =
        snapshotFlow { searchQuery }
            .combine(flagsFlow) { searchQuery, flags ->
                when {
                    searchQuery.isNotEmpty() -> flags.filter { flag ->
                        /* Handle searchQuery matching flag's common name */
                        flag.flagOf.let {
                            normalizeStringRes(it)
                                .contains(normalizeString(searchQuery), ignoreCase = true)
                        }.let {
                            /* If exact match set flag as firstItem in results list */
                            if (it && normalizeStringRes(flag.flagOf)
                                .equals(normalizeString(searchQuery), ignoreCase = true)) {
                                firstItem = flag
                            }
                            return@let it
                        }.or(
                            /* Handle searchQuery matching flag's official name */
                            other = flag.flagOfOfficial.let {
                                normalizeStringRes(it)
                                    .contains(normalizeString(searchQuery), ignoreCase = true)
                            }
                        ).let {
                            /* If exact match set flag as firstItem in results list */
                            if (it && normalizeStringRes(flag.flagOfOfficial)
                                .equals(normalizeString(searchQuery), ignoreCase = true)) {
                                firstItem = flag
                            }
                            return@let it
                        }.or(
                            /* Handle searchQuery matching any of flag's alt names if applicable */
                            other = flag.flagOfAlternate?.any {
                                normalizeStringRes(it)
                                    .contains(normalizeString(searchQuery), ignoreCase = true)
                            } ?: false
                        ).let {
                            /* If exact match set flag as firstItem in results list */
                            if (it && flag.flagOfAlternate != null) {
                                if (flag.flagOfAlternate.any { name -> normalizeStringRes(name)
                                    .equals(normalizeString(searchQuery), ignoreCase = true) }) {
                                    firstItem = flag
                                }
                            }
                            return@let it
                        }.or(
                            /* Handle search queries matching info of a flag's sovereign state */
                            other = flag.sovereignState?.let { sovereign ->
                                val sov = flagsMap.getValue(sovereign)

                                sov.let {
                                    normalizeStringRes(it.flagOf)
                                        .contains(normalizeString(searchQuery), ignoreCase = true)
                                }.or(
                                    other = normalizeStringRes(sov.flagOfOfficial)
                                        .contains(normalizeString(searchQuery), ignoreCase = true)
                                ).or(
                                    other = sov.flagOfAlternate?.any {
                                        normalizeStringRes(it).contains(
                                            normalizeString(searchQuery),
                                            ignoreCase = true
                                        )
                                    } ?: false
                                )
                            } ?: false
                        ).or(
                            /* Handle search queries matching info of a flag's associated state */
                            other = flag.associatedState?.let { associated ->
                                val ass = flagsMap.getValue(associated)

                                ass.let {
                                    normalizeStringRes(it.flagOf)
                                        .contains(normalizeString(searchQuery), ignoreCase = true)
                                }.or(
                                    other = normalizeStringRes(ass.flagOfOfficial)
                                        .contains(normalizeString(searchQuery), ignoreCase = true)
                                ).or(
                                    other = ass.flagOfAlternate?.any {
                                        normalizeStringRes(it).contains(
                                            normalizeString(searchQuery),
                                            ignoreCase = true
                                        )
                                    } ?: false
                                )
                            } ?: false
                        ).or(
                            /* Handle search queries matching info of flags that have the flag as
                             * their sovereignState value */
                            other = reverseFlagsMap.getValue(flag).let { sovereign ->
                                for (item in flags) {
                                    if (item.sovereignState == sovereign &&
                                        normalizeStringRes(item.flagOf).contains(
                                            normalizeString(searchQuery), ignoreCase = true)) {
                                        return@let true
                                    }
                                }
                                return@let false
                            }
                        ).or(
                            /* Handle search queries matching info of flags that have the flag as
                             * their associatedState value */
                            other = reverseFlagsMap.getValue(flag).let { associated ->
                                for (item in flags) {
                                    if (item.associatedState == associated &&
                                        normalizeStringRes(item.flagOf).contains(
                                            normalizeString(searchQuery), ignoreCase = true)) {
                                        return@let true
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
                    else -> searchResultsProxy.value /* Maintains list after clearing searchQuery */
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
            )
    private val searchResultsProxy = searchResults


    init {
        sortFlagsAlphabetically()
    }


    fun sortFlagsAlphabetically() {
        _uiState.update { currentState ->
            currentState.copy(
                allFlags = currentState.allFlags.sortedBy { flag ->
                    normalizeString(string = appResources.getString(flag.flagOf))
                }
            )
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery

        isSearchQuery = when (newQuery) {
            "" -> false
            else -> true
        }
    }

    private fun normalizeStringRes(@StringRes res: Int): String {
        return normalizeString(string = appResources.getString(res))
    }
}