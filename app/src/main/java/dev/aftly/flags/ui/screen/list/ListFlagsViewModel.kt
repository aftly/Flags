package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.SearchFlow
import dev.aftly.flags.ui.util.getFlagView
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getFlagsFromKeys
import dev.aftly.flags.ui.util.getSuperCategories
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeLower
import dev.aftly.flags.ui.util.normalizeString
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ListFlagsViewModel(application: Application) : AndroidViewModel(application) {
    private val savedFlagsRepository =
        (application as FlagsApplication).container.savedFlagsRepository
    private val _uiState = MutableStateFlow(ListFlagsUiState())
    val uiState = _uiState.asStateFlow()

    var searchQuery by mutableStateOf(value = "")
        private set

    private val _firstItem = MutableStateFlow<FlagView?>(value = null)
    private val firstItem = _firstItem.asStateFlow()
    private val _relatedFlags = MutableStateFlow<List<FlagView>>(value = emptyList())
    private val relatedFlags = _relatedFlags.asStateFlow()

    private val currentFlagsFlow = uiState.map { SearchFlow.CurrentFlags(it.currentFlags) }
    private val savedFlagsFlow = uiState.map { SearchFlow.SavedFlags(it.savedFlags) }
    private var isSavedFlagsFlow = uiState.map { SearchFlow.IsSavedFlags(it.isSavedFlags) }
    private val searchQueryFlow = snapshotFlow { searchQuery }.map { SearchFlow.SearchQuery(it) }
    private val appResourcesFlow = snapshotFlow {
        getApplication<Application>().applicationContext.resources
    }.map { SearchFlow.AppResources(it) }
    private val theStringFlow = snapshotFlow {
        getApplication<Application>().applicationContext.resources
            .getString(R.string.string_the_whitespace)
    }.map { SearchFlow.TheString(it) }
    private val firstItemFlow = firstItem.map { SearchFlow.FirstItem(it) }
    private val relatedFlagsFlow = relatedFlags.map { SearchFlow.RelatedFlags(it) }

    /* Use sealed interface SearchFlow for safe casting in combine() transform lambda */
    val flows: List<Flow<SearchFlow>> = listOf(
        searchQueryFlow,
        currentFlagsFlow,
        savedFlagsFlow,
        isSavedFlagsFlow,
        appResourcesFlow,
        theStringFlow,
        firstItemFlow,
        relatedFlagsFlow
    )

    val searchResults = combine(flows) { flowArray ->
        val query = (flowArray[0] as SearchFlow.SearchQuery).value
        val current = (flowArray[1] as SearchFlow.CurrentFlags).value
        val saved = (flowArray[2] as SearchFlow.SavedFlags).value
        val isSaved = (flowArray[3] as SearchFlow.IsSavedFlags).value
        val res = (flowArray[4] as SearchFlow.AppResources).value
        val the = (flowArray[5] as SearchFlow.TheString).value
        val first = (flowArray[6] as SearchFlow.FirstItem).value
        val related = (flowArray[7] as SearchFlow.RelatedFlags).value

        val flags = if (isSaved) saved else current
        val search = query.lowercase().removePrefix(the).let {
            normalizeString(it)
        }

        when {
            query.isNotEmpty() -> flags.filter { flag ->
                /* Handle searchQuery matching any flag string */
                flag.flagStringResIds.map { resId ->
                    normalizeLower(res.getString(resId))
                }.let { flagStrings ->
                    /* If exact match with searchQuery, set firstItem */
                    for (name in flagStrings) {
                        if (name == search) {
                            _firstItem.value = flag
                            break
                        }
                    }
                    /* Return true if search partial match for any flag name  */
                    flagStrings.any { it.contains(search) }
                }
            }.let { results ->
                /* When there is an exact match (firstItem) append related flags to results */
                _relatedFlags.value = first?.let { flag ->
                    sortFlagsAlphabetically(
                        application = application,
                        flags = getFlagsFromKeys(flag.externalRelatedFlagKeys)
                    )
                    //getExternalRelatedFlagsSorted(flag, application)
                } ?: emptyList()

                return@let (related + results).distinct()
            }.sortedWith { p1, p2 ->
                /* Sort list starting with firstItem, then elements in relatedFlags, then else */
                when {
                    p1 == first -> -1
                    p2 == first -> 1
                    p1 in related && p2 !in related -> -1
                    p1 !in related && p2 in related -> 1
                    else -> 0
                }
            }
            else -> flags /* Show unfiltered list when searchQuery is clear */
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = uiState.value.currentFlags,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )


    /* Initialise ListFlagsScreen() with a category not FlagSuperCategory.All
     * Also sort lists by readable name (alphabetically) */
    init {
        sortFlagsAndUpdate()
        updateCurrentCategory(
            newSuperCategory = FlagSuperCategory.SovereignCountry,
            newSubCategory = null,
        )

        viewModelScope.launch {
            savedFlagsRepository.getAllFlagsStream().collect { savedFlags ->
                _uiState.update { state ->
                    state.copy(
                        savedFlags = savedFlags.map { it.getFlagView() }
                    )
                }
            }
        }
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
            _uiState.update {
                it.copy(
                    currentFlags = it.allFlags,
                    isSavedFlags = false,
                    currentSuperCategories = listOf(All),
                    currentSubCategories = emptyList(),
                )
            }
        } else {
            val newFlags = getFlagsByCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
                allFlags = uiState.value.allFlags,
            )

            _uiState.update {
                it.copy(
                    currentFlags = newFlags,
                    isSavedFlags = false,
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
        //if (uiState.value.currentSuperCategories.isEmpty())

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

    fun toggleSavedFlags(on: Boolean = true) {
        if (on) {
            _uiState.update {
                it.copy(
                    isSavedFlags = true,
                    currentSuperCategories = emptyList(),
                    currentSubCategories = emptyList(),
                )
            }
        } else _uiState.update { it.copy(isSavedFlags = false) }
    }

    fun onSearchQueryChange(newQuery: String) {
        _firstItem.value = null /* Reset exact match state with each change to searchQuery */
        //_relatedFlags.value = emptyList()
        searchQuery = newQuery

        _uiState.update {
            it.copy(isSearchQuery = newQuery != "")
        }
    }

    fun toggleIsSearchBarInit(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInit = isSearchBarInit) }
    }

    fun toggleIsSearchBarInitTopBar(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInitTopBar = isSearchBarInit) }
    }
}