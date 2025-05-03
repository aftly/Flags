package dev.aftly.flags.ui.screen.search

import android.app.Application
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

    private val appResources = getApplication<Application>().applicationContext.resources

    private val flagsFlow = flowOf(uiState.value.allFlags)
    val searchResults: StateFlow<List<FlagResources>> =
        snapshotFlow { searchQuery }
            .combine(flagsFlow) { searchQuery, flags ->
                when {
                    searchQuery.isNotEmpty() -> flags.filter { flag ->
                        /* Handle search queries matching info of a flag's primary info */
                        flag.flagOf.let {
                            normalizeString(string = appResources.getString(it))
                                .contains(normalizeString(searchQuery), ignoreCase = true)
                        }.or(
                            other = flag.flagOfOfficial.let {
                                normalizeString(string = appResources.getString(it))
                                    .contains(normalizeString(searchQuery), ignoreCase = true)
                            }
                        ).or(
                            other = flag.flagOfAlternate?.any {
                                normalizeString(string = appResources.getString(it))
                                    .contains(normalizeString(searchQuery), ignoreCase = true)
                            } ?: false
                        ).or(
                            /* Handle search queries matching info of a flag's sovereign state */
                            other = flag.sovereignState?.let { sovereign ->
                                val sov = flagsMap.getValue(sovereign)

                                sov.let {
                                    normalizeString(string = appResources.getString(it.flagOf))
                                        .contains(normalizeString(searchQuery), ignoreCase = true)
                                }.or(
                                    other = normalizeString(
                                        string = appResources.getString(sov.flagOfOfficial)
                                    ).contains(normalizeString(searchQuery), ignoreCase = true)
                                ).or(
                                    other = sov.flagOfAlternate?.any {
                                        normalizeString(string = appResources.getString(it))
                                            .contains(
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
                                    normalizeString(string = appResources.getString(it.flagOf))
                                        .contains(
                                            normalizeString(searchQuery),
                                            ignoreCase = true
                                        )
                                }.or(
                                    other = normalizeString(
                                        string = appResources.getString(ass.flagOfOfficial)
                                    ).contains(normalizeString(searchQuery), ignoreCase = true)
                                ).or(
                                    other = ass.flagOfAlternate?.any {
                                        normalizeString(string = appResources.getString(it))
                                            .contains(
                                                normalizeString(searchQuery),
                                                ignoreCase = true,
                                            )
                                    } ?: false
                                )
                            } ?: false
                        ).or(
                            /* Handle search queries matching info of flags that have the flag as
                             * their sovereignState value */
                            other = reverseFlagsMap.getValue(flag).let { sovereign ->
                                for (item in flags) {
                                    if (item.sovereignState == sovereign) {
                                        if (normalizeString(appResources.getString(item.flagOf))
                                                .contains(
                                                    normalizeString(searchQuery),
                                                    ignoreCase = true)
                                        ) {
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
                                for (item in flags) {
                                    if (item.associatedState == associated) {
                                        if (normalizeString(appResources.getString(item.flagOf))
                                                .contains(
                                                    normalizeString(searchQuery),
                                                    ignoreCase = true)
                                        ) {
                                            return@let true
                                        }
                                    }
                                }
                                return@let false
                            }
                        )
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
}