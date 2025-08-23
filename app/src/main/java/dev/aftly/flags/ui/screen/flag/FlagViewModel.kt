package dev.aftly.flags.ui.screen.flag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.ETHNIC
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.MILITARY_JUNTA
import dev.aftly.flags.model.FlagCategory.MONARCHY
import dev.aftly.flags.model.FlagCategory.OBLAST
import dev.aftly.flags.model.FlagCategory.ONE_PARTY
import dev.aftly.flags.model.FlagCategory.POLITICAL
import dev.aftly.flags.model.FlagCategory.PROVISIONAL_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.REGIONAL
import dev.aftly.flags.model.FlagCategory.RELIGIOUS
import dev.aftly.flags.model.FlagCategory.SOCIAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.SUPRANATIONAL_UNION
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.RelatedFlagsMenu
import dev.aftly.flags.ui.util.getChronologicalRelatedFlagsContentOrNull
import dev.aftly.flags.ui.util.getFlagFromId
import dev.aftly.flags.ui.util.getFlagIdsFromString
import dev.aftly.flags.ui.util.getFlagKey
import dev.aftly.flags.ui.util.getPoliticalRelatedFlagsContentOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlagViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val savedFlagsRepository =
        (application as FlagsApplication).container.savedFlagsRepository
    private val _uiState = MutableStateFlow(FlagUiState())
    val uiState = _uiState.asStateFlow()

    init {
        /* Initialise state from nav arg via SavedStateHandle */
        val flagIdArg = savedStateHandle.get<Int>("flagId")
        val flagIdsArg = savedStateHandle.get<String>("flagIds")

        if (flagIdArg != null && flagIdsArg != null) {
            val flagIdsFromList = getFlagIdsFromString(string = flagIdsArg)

            updateFlag(flagIdArg, flagIdsFromList)
        }

        viewModelScope.launch {
            savedFlagsRepository.getAllFlagsStream().collect { savedFlags ->
                val savedFlag = savedFlags.find { it.flagKey == uiState.value.flagKey }
                _uiState.update {
                    it.copy(savedFlags = savedFlags, savedFlag = savedFlag)
                }
            }
        }
    }


    fun updateFlag(
        flagId: Int,
        flagIdsFromList: List<Int>? = null,
    ) {
        val flag = getFlagFromId(flagId)

        if (flag != uiState.value.flag) {
            val flagKey = getFlagKey(flag)

            _uiState.update {
                it.copy(
                    flag = flag,
                    flagKey = flagKey,
                    politicalRelatedFlagsContent =
                        getPoliticalRelatedFlagsContentOrNull(flag, application),
                    chronologicalRelatedFlagsContent =
                        getChronologicalRelatedFlagsContentOrNull(flag, application),
                    flagIdsFromList = flagIdsFromList ?: it.flagIdsFromList,
                    isRelatedFlagNav = when (flag) {
                        it.initRelatedFlag -> null
                        else -> it.isRelatedFlagNav
                    },
                    savedFlag = it.savedFlags.find { savedFlag ->
                        savedFlag.flagKey == flagKey
                    }
                )
            }
            updateDescriptionString(flag = flag)
        }
    }


    fun updateFlagRelated(
        flag: FlagView,
        relatedMenu: RelatedFlagsMenu,
    ) {
        _uiState.update {
            val initRelatedFlag =
                if (relatedMenu == it.isRelatedFlagNav) it.initRelatedFlag
                else it.flag

            it.copy(
                isRelatedFlagNav = if (flag != initRelatedFlag) relatedMenu else null,
                initRelatedFlag = initRelatedFlag,
            )
        }
        updateFlag(flagId = flag.id)
    }


    fun updateSavedFlag() {
        val savedFlag = uiState.value.savedFlag
        val flagKey = uiState.value.flagKey

        viewModelScope.launch {
            savedFlag?.let { flag ->
                savedFlagsRepository.deleteFlag(flag)
            } ?: flagKey?.let { key ->
                savedFlagsRepository.insertFlag(SavedFlag(flagKey = key))
            }
        }
    }


    fun getFlagIds(): List<Int> = when (uiState.value.isRelatedFlagNav) {
        null -> uiState.value.flagIdsFromList
        RelatedFlagsMenu.POLITICAL ->
            uiState.value.politicalRelatedFlagsContent?.getIds() ?: emptyList()
        RelatedFlagsMenu.CHRONOLOGICAL ->
            uiState.value.chronologicalRelatedFlagsContent?.getIds() ?: emptyList()
    }


    /* Convert the @StringRes list into a legible string
     * Also for execution upon locale/language configuration changes */
    fun updateDescriptionString(flag: FlagView) {
        val appResources = getApplication<Application>().applicationContext.resources
        val stringIds = getDescriptionIds(flag = flag)
        val whitespaceExceptions = uiState.value.descriptionIdsWhitespaceExceptions
        val strings = mutableListOf<String>()
        val flagOfIndexes = mutableListOf<Int>()
        val allFlagOfIds = allFlagsList.map { it.flagOfLiteral }

        /* Loop through stringResIds and add their corresponding strings to stringList
         * (and with whitespace when appropriate) */
        for ((index, stringId) in stringIds.withIndex()) {
            if (index !in whitespaceExceptions) {
                strings.add(appResources.getString(R.string.string_whitespace))
            }
            strings.add(appResources.getString(stringId))

            /* Add index of word in stringList to flagOfPositions if it is a flag name */
            if (stringId in allFlagOfIds) {
                flagOfIndexes.add(element = strings.lastIndex)
            }
        }

        /* Update boldWordIndexes state with flagOf positions */
        _uiState.update { currentState ->
            currentState.copy(descriptionBoldWordIndexes = flagOfIndexes.toList())
        }

        /* Update description state with stringList */
        _uiState.update { currentState ->
            currentState.copy(description = strings.toList())
        }
    }


    /* Return appropriate @StringRes list from flag info to make a legible entity description
     * Also, update state with whitespace exceptions for @StringRes list */
    private fun getDescriptionIds(flag: FlagView): List<Int> {
        val categories = flag.categories
        val stringIds = mutableListOf<Int>()
        val whitespaceExceptionIndexes = mutableListOf(0)

        val allCulturalCategories = FlagSuperCategory.Cultural.enums()
        val allNonCulturalCategories = FlagSuperCategory.All.enums().filterNot {
            it in allCulturalCategories
        }

        /* Description varies based on the following conditions */
        val isConstitutional = CONSTITUTIONAL in categories
        val isNonCulturalCategoriesInFlag = categories.filterNot { it == HISTORICAL }
            .any { it in allNonCulturalCategories }
        val isCulturalCategoriesInFlag = categories.any { it in allCulturalCategories }


        /* Add non-cultural description @StringRes ids to list */
        if (isNonCulturalCategoriesInFlag) {
            /* Start with flag name */
            if (flag.isFlagOfThe) stringIds.add(element = R.string.string_the_capitalized)
            stringIds.add(element = flag.flagOfLiteral)

            /* If flag is historical "was a", else "is a" */
            if (HISTORICAL in categories) {
                stringIds.add(element = R.string.string_was_a)
            } else {
                stringIds.add(element = R.string.string_is_a)
            }

            /* Loop through categories and add it's string or alternate strings depending on
             * legibility */
            iterateOverNonCulturalCategories(
                categories = categories.filterNot { it in allCulturalCategories },
                stringIds = stringIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
                isConstitutional = isConstitutional,
            )

            /* If relevant add strings about the associated state */
            if (flag.associatedStateKey != null) {
                val associatedState = flagViewMap.getValue(flag.associatedStateKey)

                stringIds.add(element = R.string.category_free_association_in_description)
                if (associatedState.isFlagOfThe) stringIds.add(element = R.string.string_the)
                stringIds.add(element = associatedState.flagOfLiteral)
            }

            /* If relevant add strings about the sovereign state */
            if (flag.sovereignStateKey != null) {
                val sovereign = flagViewMap.getValue(flag.sovereignStateKey)
                val isSovereignConstitutional = CONSTITUTIONAL in sovereign.categories

                /* If sovereign state isn't the associated state add it's name info */
                if (flag.associatedStateKey == null) {
                    stringIds.add(element = R.string.string_within)
                    if (sovereign.isFlagOfThe) stringIds.add(element = R.string.string_the)
                    stringIds.add(element = sovereign.flagOfLiteral)
                }
                stringIds.add(element = R.string.string_comma)
                whitespaceExceptionIndexes.add(element = stringIds.lastIndex)

                if (HISTORICAL in sovereign.categories) {
                    stringIds.add(element = R.string.string_which_was_a)
                } else {
                    stringIds.add(element = R.string.string_a)
                }

                iterateOverNonCulturalCategories(
                    categories = sovereign.categories.filterNot { it in allCulturalCategories },
                    stringIds = stringIds,
                    whitespaceExceptions = whitespaceExceptionIndexes,
                    isConstitutional = isSovereignConstitutional,
                )
            }
            /* End of description */
            stringIds.add(element = R.string.string_period)
            whitespaceExceptionIndexes.add(element = stringIds.lastIndex)
        }


        /* Add cultural description @StringRes ids to list */
        if (isCulturalCategoriesInFlag) {
            val categoriesCultural = categories.filter { it in allCulturalCategories }

            /* Make new paragraph if non-culture description precedes */
            if (isNonCulturalCategoriesInFlag) {
                stringIds.add(element = R.string.string_new_paragraph)
                whitespaceExceptionIndexes.add(element = stringIds.lastIndex)
            }

            /* Start with flag name */
            stringIds.add(element = R.string.string_the_capitalized)
            if (isNonCulturalCategoriesInFlag) {
                whitespaceExceptionIndexes.add(element = stringIds.lastIndex)
            }
            stringIds.add(element = flag.flagOfLiteral)

            /* If flag is historical or non-cultural description precedes, strings differ */
            if (isNonCulturalCategoriesInFlag && HISTORICAL in categories) {
                stringIds.add(element = R.string.category_super_cultural_in_description_historical_also)
            } else if (HISTORICAL in categories) {
                stringIds.add(element = R.string.category_super_cultural_in_description_historical)
            } else if (isNonCulturalCategoriesInFlag) {
                stringIds.add(element = R.string.category_super_cultural_in_description_also)
            } else {
                stringIds.add(element = R.string.category_super_cultural_in_description)
            }

            /* Loop through categories and add appropriate @StringRes ids to list */
            iterateOverCulturalCategories(
                categories = categoriesCultural,
                stringIds = stringIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
            )
        }

        /* Update state with whitespaceExceptionIndexes */
        _uiState.update { currentState ->
            currentState.copy(
                descriptionIdsWhitespaceExceptions = whitespaceExceptionIndexes.toList()
            )
        }
        return stringIds.toList()
    }


    /* Loop over non-cultural categories and add it's string or alternate strings depending on
     * legibility to resIds list */
    private fun iterateOverNonCulturalCategories(
        categories: List<FlagCategory>,
        stringIds: MutableList<Int>,
        whitespaceExceptions: MutableList<Int>,
        isConstitutional: Boolean,
    ) {
        val skipCategories = listOf(SOVEREIGN_STATE, FREE_ASSOCIATION, HISTORICAL)

        for (category in categories) {
            if (category in skipCategories) {
                continue

            } else if (category == AUTONOMOUS_REGION) {
                stringIds.add(element = R.string.category_autonomous_region_in_description_an)
                whitespaceExceptions.add(element = stringIds.lastIndex)

            } else if (category == OBLAST) {
                if (AUTONOMOUS_REGION !in categories) {
                    stringIds.add(element = R.string.category_oblast_string_in_description_an)
                    whitespaceExceptions.add(element = stringIds.lastIndex)
                } else {
                    stringIds.add(element = category.string)
                }

            } else if (category == DEVOLVED_GOVERNMENT) {
                stringIds.add(element = R.string.category_devolved_government_in_description)

            } else if (category == INTERNATIONAL_ORGANIZATION) {
                if (categories.any { it in FlagSuperCategory.ExecutiveStructure.enums() } &&
                    !categories.any {
                        it in FlagSuperCategory.TerritorialDistributionOfAuthority.enums()
                    }) {
                    if (SUPRANATIONAL_UNION in categories) {
                        stringIds.add(element = R.string.string_and)
                    }
                    stringIds.add(element = category.string)

                } else if (SUPRANATIONAL_UNION !in categories) {
                    stringIds.add(element = R.string.category_international_organization_in_description)
                    whitespaceExceptions.add(element = stringIds.lastIndex)
                    stringIds.add(element = R.string.string_and)

                } else if (categories.any {
                    it in FlagSuperCategory.TerritorialDistributionOfAuthority.enums() }) {
                    stringIds.add(element = R.string.string_comma)
                    whitespaceExceptions.add(element = stringIds.lastIndex)
                    stringIds.add(element = category.string)
                    stringIds.add(element = R.string.string_and)

                } else {
                    stringIds.add(element = R.string.string_and)
                    stringIds.add(element = category.string)
                }

            } else if (category in FlagSuperCategory.IdeologicalOrientation.enums() &&
                AUTONOMOUS_REGION in categories) {
                if (DEVOLVED_GOVERNMENT in categories) {
                    stringIds.add(element = R.string.string_that_is)
                } else if (ONE_PARTY in categories) {
                    stringIds.add(element = R.string.string_and)
                } else {
                    stringIds.add(element = R.string.string_with_a)
                }
                stringIds.add(element = category.string)

            } else if (category in FlagSuperCategory.ExecutiveStructure.enums() &&
                !categories.any { it in FlagSuperCategory.International.enums() } &&
                !isConstitutional) {
                stringIds.add(element = R.string.category_nominal_extra_constitutional_in_description)
                stringIds.add(element = category.string)

            } else if (category == CONSTITUTIONAL && MONARCHY !in categories) {
                continue

            } else if (category == ONE_PARTY) {
                stringIds.add(element = R.string.category_one_party_in_description)

            } else if (category == THEOCRACY && MONARCHY in categories) {
                stringIds.add(element = R.string.string_and)
                stringIds.add(element = category.string)

            } else if (category == MILITARY_JUNTA) {
                stringIds.add(element = R.string.category_military_junta_in_description)

            } else if (category == PROVISIONAL_GOVERNMENT) {
                stringIds.add(element = R.string.category_provisional_government_in_description)

            } else {
                stringIds.add(element = category.string)
            }
        }
    }


    /* Loop over cultural categories and add it's string or alternate strings depending on
     * legibility to resIds list */
    private fun iterateOverCulturalCategories(
        categories: List<FlagCategory>,
        stringIds: MutableList<Int>,
        whitespaceExceptions: MutableList<Int>,
    ) {
        val size = categories.size
        val firstCategory = categories[0]
        val penultimateCategory = if (size > 2) categories[size - 2] else firstCategory
        val lastCategory = categories[size - 1]

        for (category in categories) {
            if (category == ETHNIC) {
                stringIds.add(element = R.string.category_ethnic_in_description)
                whitespaceExceptions.add(element = stringIds.lastIndex)
            }
            if (category == SOCIAL) stringIds.add(element = R.string.category_social_in_description)
            if (category == POLITICAL) stringIds.add(element = R.string.category_political_in_description)
            if (category == RELIGIOUS) stringIds.add(element = R.string.category_religious_in_description)
            if (category == REGIONAL) stringIds.add(element = R.string.category_regional_string)

            if (size > 2 && category !in listOf(penultimateCategory, lastCategory)) {
                stringIds.add(element = R.string.string_comma)
                stringIds.add(element = R.string.string_a)
                whitespaceExceptions.add(element = stringIds.lastIndex)

            } else if (category != lastCategory) {
                stringIds.add(element = R.string.string_and_a)

            } else { // End of loop condition
                stringIds.add(element = R.string.string_period)
                whitespaceExceptions.add(element = stringIds.lastIndex)
            }
        }
    }
}