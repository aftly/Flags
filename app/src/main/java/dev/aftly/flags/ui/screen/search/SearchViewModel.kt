package dev.aftly.flags.ui.screen.search

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
                        normalizeString(string = appResources.getString(flag.flagOf))
                            .contains(normalizeString(searchQuery), ignoreCase = true)

                        normalizeString(string = appResources.getString(flag.flagOfOfficial))
                            .contains(normalizeString(searchQuery), ignoreCase = true)

                        flag.flagOfAlternate?.any { alt ->
                            normalizeString(string = appResources.getString(alt))
                                .contains(normalizeString(searchQuery), ignoreCase = true)
                        } ?: false
                    }
                    else -> flags
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
            )


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