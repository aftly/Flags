package dev.aftly.flags.ui.screen.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.SearchFlow
import dev.aftly.flags.ui.util.getFlagKey
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getFlagsFromCategory
import dev.aftly.flags.ui.util.getFlagsFromKeys
import dev.aftly.flags.ui.util.getSavedFlagViewSorted
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeLower
import dev.aftly.flags.ui.util.normalizeString
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.toSavedFlag
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


class ListFlagsViewModel(app: Application) : AndroidViewModel(application = app) {
    private val savedFlagsRepository =
        (application as FlagsApplication).container.savedFlagsRepository
    private val _uiState = MutableStateFlow(value = ListFlagsUiState())
    val uiState = _uiState.asStateFlow()

    var searchQueryValue by mutableStateOf(value = TextFieldValue())
        private set

    private val allFlagsFlow = uiState.map { SearchFlow.FlagsList(it.allFlags) }
    private val currentFlagsFlow = uiState.map { SearchFlow.FlagsList(it.currentFlags) }
    private val savedFlagsFlow = uiState.map {
        SearchFlow.FlagsList(
            getSavedFlagViewSorted(application, it.savedFlags)
        )
    }
    private var isSavedFlagsFlow = uiState.map { SearchFlow.Bool(it.isViewSavedFlags) }
    private val searchQueryFlow = snapshotFlow { searchQueryValue }
        .map { SearchFlow.Str(it.text) }

    /* Use sealed interface SearchFlow for safe casting in combine() transform lambda */
    val flows: List<Flow<SearchFlow>> = listOf(
        searchQueryFlow,
        allFlagsFlow,
        currentFlagsFlow,
        savedFlagsFlow,
        isSavedFlagsFlow,
    )

    val searchResults = combine(flows) { flowArray ->
        val query = (flowArray[0] as SearchFlow.Str).value
        val all = (flowArray[1] as SearchFlow.FlagsList).value
        val current = (flowArray[2] as SearchFlow.FlagsList).value
        val saved = (flowArray[3] as SearchFlow.FlagsList).value
        val isSaved = (flowArray[4] as SearchFlow.Bool).value

        val res = application.resources
        val the = res.getString(R.string.string_the_whitespace)

        val flags = if (isSaved) saved else current
        val search = normalizeString(
            string = query.lowercase().removePrefix(the)
        )
        val exactFlags = mutableListOf<FlagView>()
        val exactOther = mutableListOf<FlagView>()
        var sovereign = listOf<FlagView>()
        var polInternal = listOf<FlagView>()
        var polExternal = listOf<FlagView>()
        var chronDirect = listOf<FlagView>()
        var chronIndirect = listOf<FlagView>()

        when {
            query.isNotEmpty() -> all.filter { flag ->
                /* Get flag strings to match query against */
                val isFlagInFlags = flag in flags
                val descriptorString = flag.flagOfDescriptor?.let {
                    normalizeLower(res.getString(it))
                }
                val fullFlagOfString = buildString {
                    append(normalizeLower(res.getString(flag.flagOf)))
                    descriptorString?.let { append(it) }
                }
                val flagStrings = flag.flagStringResIds
                    .map { normalizeLower(res.getString(it)) } + fullFlagOfString
                val flagStringsExact = if (descriptorString == null) flagStrings else
                    flagStrings.filterNot { it == descriptorString }

                /* If exact match with searchQuery and in flags add flag to exact, else to first */
                if (flagStringsExact.any { it == search && flag.previousFlagOfKey == null }) {
                    if (isFlagInFlags) exactFlags.add(flag)
                    else exactOther.add(flag)
                }

                /* Filter expression: True if partial match to any flag name in flags  */
                isFlagInFlags && flagStrings.any { it.contains(search) }

            }.let { results ->
                /* Add related flags of search exact matches to results:
                 * If exact match is in currentFlags, add any/every related flag
                 * If exact match not in current flags, add only directly related flags */
                if (exactFlags.isNotEmpty() || exactOther.isNotEmpty()) {
                    if (exactFlags.isNotEmpty()) {
                        sovereign = exactFlags.map { flag ->
                            flag.sovereignStateKey?.let { flagViewMap.getValue(it) } ?: flag
                        }

                        polInternal = exactFlags.flatMap { flag ->
                            getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
                        }

                        polExternal = exactFlags.flatMap { flag ->
                            getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)
                        }

                        chronDirect = exactFlags.flatMap { flag ->
                            getFlagsFromKeys(flag.chronologicalDirectRelatedFlagKeys)
                        }

                        chronIndirect = exactFlags.flatMap { flag ->
                            getFlagsFromKeys(flag.chronologicalIndirectRelatedFlagKeys)
                        }
                    }

                    if (exactOther.isNotEmpty()) {
                        exactOther.forEach { flag ->
                            val flagKey = inverseFlagViewMap.getValue(flag)

                            if (flag.sovereignStateKey == null) {
                                // If sovereign: All related
                                sovereign = sovereign + flag

                                polInternal = polInternal +
                                        getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)

                                polExternal = polExternal +
                                        getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)

                                chronDirect = chronDirect +
                                        getFlagsFromKeys(flag.chronologicalDirectRelatedFlagKeys)

                                chronIndirect = chronIndirect +
                                        getFlagsFromKeys(flag.chronologicalIndirectRelatedFlagKeys)

                            } else {
                                // Else: Sovereign + parent and/or children internal units only
                                val related = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
                                val children = related.filter { it.parentUnitKey == flagKey }

                                sovereign = sovereign + flagViewMap.getValue(flag.sovereignStateKey)
                                polInternal = polInternal + children

                                flag.parentUnitKey?.let { parentKey ->
                                    val parents = related.filter {
                                        inverseFlagViewMap.getValue(it) == parentKey
                                    }
                                    polInternal = polInternal + parents
                                }
                            }
                        }
                    }

                    buildList {
                        val relatedFlags = (polInternal + polExternal + chronDirect + chronIndirect)
                        val relatedSorted = sortFlagsAlphabetically(application, relatedFlags)
                        addAll(elements = relatedSorted.filter { it in flags })
                        addAll(elements = results)
                    }.distinct()

                } else {
                    results
                }
            }.sortedWith { p1, p2 ->
                /* Only sort list when exact matches */
                val isMatch = exactFlags.isNotEmpty() || exactOther.isNotEmpty()

                /* Sort list starting with firstItem, then elements in relatedFlags, then else */
                when {
                    isMatch && p1 in exactFlags && p2 !in exactFlags -> -1
                    isMatch && p1 !in exactFlags && p2 in exactFlags -> 1
                    isMatch && p1 in sovereign && p2 !in sovereign -> -1
                    isMatch && p1 !in sovereign && p2 in sovereign -> 1
                    isMatch && p1 in polInternal && p2 !in polInternal -> -1
                    isMatch && p1 !in polInternal && p2 in polInternal -> 1
                    isMatch && p1 in polExternal && p2 !in polExternal -> -1
                    isMatch && p1 !in polExternal && p2 in polExternal -> 1
                    isMatch && p1 in chronDirect && p2 !in chronDirect -> -1
                    isMatch && p1 !in chronDirect && p2 in chronDirect -> 1
                    isMatch && p1 in chronIndirect && p2 !in chronIndirect -> -1
                    isMatch && p1 !in chronIndirect && p2 in chronIndirect -> 1
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
        _uiState.update {
            it.copy(
                allFlags = sortFlagsAlphabetically(application, it.allFlags),
                currentFlags = sortFlagsAlphabetically(application, it.currentFlags),
            )
        }
        resetScreen()

        viewModelScope.launch {
            savedFlagsRepository.getAllFlagsStream().collect { savedFlags ->
                _uiState.update { it.copy(savedFlags = savedFlags.toSet()) }
            }
        }
    }

    fun resetScreen() {
        updateCurrentCategory(
            superCategory = FlagSuperCategory.SovereignCountry,
            subCategory = null,
        )
    }

    fun sortFlagsAlphabetically(flags: List<FlagView>): List<FlagView> =
        sortFlagsAlphabetically(application, flags)

    /* Updates state with new currentFlags list derived from new super- or sub- category
     * Also updates currentSuperCategory and currentCategory title details for FilterFlagsButton UI
     * Is intended to be called with either a newSuperCategory OR newSubCategory, and a null value */
    fun updateCurrentCategory(
        superCategory: FlagSuperCategory?,
        subCategory: FlagCategory?,
    ) {
        /* If new category is All superCategory update flags with static allFlags source,
         * else dynamically generate flags list from category info */
        if (superCategory == All) {
            _uiState.update {
                it.copy(
                    currentFlags = it.allFlags,
                    isViewSavedFlags = false,
                    currentSuperCategories = listOf(All),
                    currentSubCategories = emptyList(),
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    currentFlags = getFlagsFromCategory(
                        superCategory = superCategory,
                        subCategory = subCategory,
                        allFlags = state.allFlags,
                    ),
                    isViewSavedFlags = false,
                    currentSuperCategories = buildList {
                        superCategory?.let { add(it) }
                    },
                    currentSubCategories = buildList {
                        subCategory?.let { add(it) }
                    },
                )
            }
        }
    }


    fun updateCurrentCategories(
        superCategory: FlagSuperCategory?,
        subCategory: FlagCategory?,
    ) {
        /* Controls whether flags are updated from current or all flags */
        var isDeselectSwitch = false to false

        val newSuperCategories = uiState.value.currentSuperCategories.toMutableList()
        val newSubCategories = uiState.value.currentSubCategories.toMutableList()

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        superCategory?.let { superCategory ->
            if (isSuperCategoryExit(superCategory, newSuperCategories, newSubCategories)) return

            isDeselectSwitch = updateCategoriesFromSuper(
                superCategory = superCategory,
                superCategories = newSuperCategories,
                subCategories = newSubCategories,
            ) to false
        }
        subCategory?.let { subCategory ->
            if (isSubCategoryExit(subCategory, newSubCategories, newSuperCategories)) return

            isDeselectSwitch = updateCategoriesFromSub(
                subCategory = subCategory,
                subCategories = newSubCategories,
            )
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselectSwitch.first) {
            when (newSuperCategories.size to newSubCategories.size) {
                0 to 0 -> return updateCurrentCategory(
                    superCategory = All, subCategory = null
                )
                1 to 0 -> return updateCurrentCategory(
                    superCategory = newSuperCategories.first(), subCategory = null
                )
                1 to 1 -> {
                    val onlySuperCategory = newSuperCategories.first()
                    val onlySubCategory = newSubCategories.first()

                    if (onlySuperCategory.subCategories.size == 1 &&
                        onlySuperCategory.firstCategoryEnumOrNull() == onlySubCategory) {
                        return updateCurrentCategory(
                            superCategory = onlySuperCategory, subCategory = null
                        )
                    }
                }
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
                    isDeselectSwitch = isDeselectSwitch,
                    superCategory = superCategory,
                    superCategories = newSuperCategories,
                    subCategories = newSubCategories,
                ),
                currentSuperCategories = newSuperCategories,
                currentSubCategories = newSubCategories,
            )
        }
    }

    fun selectSavedFlags(on: Boolean = true) {
        if (on) {
            _uiState.update {
                it.copy(
                    isViewSavedFlags = true,
                    currentSuperCategories = emptyList(),
                    currentSubCategories = emptyList(),
                )
            }
        } else _uiState.update { it.copy(isViewSavedFlags = false) }
    }

    fun onSaveFlag(flag: FlagView) {
        val savedFlags = uiState.value.savedFlags
        val savedFlag = savedFlags.find { savedFlag ->
            savedFlag.flagKey == getFlagKey(flag)
        }

        viewModelScope.launch {
            if (savedFlag != null) {
                savedFlagsRepository.deleteFlag(savedFlag)
            } else {
                savedFlagsRepository.insertFlag(flag.toSavedFlag())
            }
        }
    }

    fun onSearchQueryValueChange(newValue: TextFieldValue) {
        searchQueryValue = newValue
        _uiState.update { it.copy(isSearchQuery = newValue.text != "") }
    }

    fun toggleIsSearchBarInit(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInit = isSearchBarInit) }
    }

    fun toggleIsSearchBarInitTopBar(isSearchBarInit: Boolean) {
        _uiState.update { it.copy(isSearchBarInitTopBar = isSearchBarInit) }
    }
}