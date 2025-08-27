package dev.aftly.flags.ui.screen.flag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
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
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagCategory.SOCIALIST
import dev.aftly.flags.model.FlagCategory.FASCIST
import dev.aftly.flags.model.FlagScreenContent
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.Cultural
import dev.aftly.flags.model.FlagSuperCategory.ExecutiveStructure
import dev.aftly.flags.model.FlagSuperCategory.TerritorialDistributionOfAuthority
import dev.aftly.flags.model.FlagSuperCategory.IdeologicalOrientation
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

            updateFlag(
                flagId = flagIdArg,
                flagIdsFromList = flagIdsFromList,
                isAnimated = false,
            )
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
        isAnimated: Boolean = true,
        isLink: Boolean = false,
    ) {
        val flag = getFlagFromId(flagId)

        if (flag != uiState.value.flag) {
            val flagKey = getFlagKey(flag)

            _uiState.update {
                val listFlagIds = flagIdsFromList ?: it.flagIdsFromList

                it.copy(
                    flag = flag,
                    flagKey = flagKey,
                    politicalRelatedFlagsContent =
                        getPoliticalRelatedFlagsContentOrNull(flag, application),
                    chronologicalRelatedFlagsContent =
                        getChronologicalRelatedFlagsContentOrNull(flag, application),
                    flagIdsFromList = listFlagIds,
                    navBackScrollToId = if (flagId in listFlagIds) flagId else it.navBackScrollToId,
                    annotatedLinkFrom = when (isLink) {
                        true -> it.annotatedLinkFrom + it.flag
                        false -> it.annotatedLinkFrom.filterIndexed { index, flag ->
                            index != it.annotatedLinkFrom.lastIndex
                        }
                    },
                    savedFlag = it.savedFlags.find { savedFlag ->
                        savedFlag.flagKey == flagKey
                    }
                )
            }
            updateFlagScreenContent(flagView = flag, isAnimated = isAnimated)
        }
    }


    fun updateFlagRelated(
        flag: FlagView,
        relatedMenu: RelatedFlagsMenu?,
        isLink: Boolean,
    ) {
        _uiState.update {
            it.copy(latestMenuInteraction = relatedMenu)
        }
        updateFlag(flagId = flag.id, isLink = isLink)
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

    fun getFlagIds(): List<Int> {
        val latest = uiState.value.latestMenuInteraction
        val flag = uiState.value.flag
        val flagIds = uiState.value.flagIdsFromList
        val polRelated = uiState.value.politicalRelatedFlagsContent
        val chronRelated = uiState.value.chronologicalRelatedFlagsContent

        return if (latest == RelatedFlagsMenu.POLITICAL && flag.id !in flagIds) {
            polRelated?.getIds() ?: emptyList()
        } else if (latest == RelatedFlagsMenu.CHRONOLOGICAL && flag.id !in flagIds) {
            chronRelated?.getIds() ?: emptyList()
        } else {
            flagIds
        }
    }


    /* Update state with relevant description string resources for resolution in UI layer */
    private fun updateFlagScreenContent(
        flagView: FlagView,
        isAnimated: Boolean,
    ) {
        /* ---------- Initialise properties for getting description strings ---------- */
        val resIds = mutableListOf<Int>()
        val whitespaceExceptionIndexes = mutableListOf(0)
        val clickableIndexes = mutableListOf<Int>()
        val flagNameIndexes = mutableListOf<Int>()
        val descriptorIndexes = mutableListOf<Int>()

        /* Resources */
        val previousFlagOf = flagViewMap[flagView.previousFlagOfKey]
        val flag = previousFlagOf ?: flagView
        val categories = flag.categories
        val associatedState = flagViewMap[flag.associatedStateKey]
        val sovereignState = flagViewMap[flag.sovereignStateKey]
        val parentUnit = flagViewMap[flag.parentUnitKey]

        val clickableFlags = buildList {
            previousFlagOf?.let { add(it) }
            associatedState?.let { add(it) }
            sovereignState?.let { add(it) }
            parentUnit?.let { add(it) }
        }
        val clickableResIds = clickableFlags.flatMap {
            listOf(it.flagOf, it.flagOfLiteral, it.flagOfOfficial)
        }
        val flagNameResIds = listOf(flag.flagOf, flag.flagOfLiteral, flag.flagOfOfficial)
        val descriptorResIds = listOf(
            R.string.category_nominal_extra_constitutional_in_description,
            R.string.string_defunct
        )

        val categoriesHistorical = categories.filter { it == HISTORICAL }
        val categoriesInstitutional = categories.filter { it in Institution.enums() }
        val categoriesCultural = categories.filter { it in Cultural.enums() }
        val categoriesPolitical = categories.filterNot {
            it in categoriesHistorical || it in categoriesInstitutional || it in categoriesCultural
        }

        val isPolitical = categoriesPolitical.isNotEmpty()
        val isInstitutional = categoriesInstitutional.isNotEmpty()
        val isCultural = categoriesCultural.isNotEmpty()

        /* ----------------------- Description START ----------------------- */
        if (HISTORICAL in categories) {
            if (flag.isFlagOfOfficialThe) resIds.add(R.string.string_the_capitalized)
            resIds.add(flag.flagOfOfficial) /* FLAG NAME */
            resIds.add(R.string.string_is_a)
            resIds.add(R.string.string_defunct) /* DESCRIPTOR */
        } else {
            if (flag.isFlagOfThe) resIds.add(R.string.string_the_capitalized)
            resIds.add(flag.flagOfLiteral) /* FLAG NAME */

            if (isInstitutional) resIds.add(R.string.string_is_the)
            else resIds.add(R.string.string_is_a)
        }

        if (isPolitical) {
            iterateOverPoliticalCategories(
                categories = categoriesHistorical + categoriesPolitical,
                stringIds = resIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
                isConstitutional = CONSTITUTIONAL in categories,
            )

            /* If relevant add strings about the associated or sovereign state */
            if (associatedState != null) {
                if (SOVEREIGN_STATE in categories) {
                    resIds.add(R.string.string_comma)
                    whitespaceExceptionIndexes.add(resIds.lastIndex)
                }
                resIds.add(R.string.category_free_association_in_description)

                if (HISTORICAL in associatedState.categories) {
                    if (associatedState.isFlagOfOfficialThe) resIds.add(R.string.string_the)
                    resIds.add(associatedState.flagOfOfficial) /* FLAG NAME */
                } else {
                    if (associatedState.isFlagOfThe) resIds.add(R.string.string_the)
                    resIds.add(associatedState.flagOfLiteral) /* FLAG NAME */
                }

            } else if (sovereignState != null) {
                when (REGIONAL) {
                    in categories -> resIds.add(R.string.string_in)
                    else -> resIds.add(R.string.string_of)
                }

                if (HISTORICAL in sovereignState.categories) {
                    if (sovereignState.isFlagOfOfficialThe) resIds.add(R.string.string_the)
                    resIds.add(R.string.string_defunct) /* DESCRIPTOR */
                    resIds.add(sovereignState.flagOfOfficial) /* FLAG NAME */
                } else {
                    if (sovereignState.isFlagOfThe) resIds.add(R.string.string_the)
                    resIds.add(sovereignState.flagOfLiteral) /* FLAG NAME */
                }
            }

            /* Append devolved government information */
            if (DEVOLVED_GOVERNMENT in categories) {
                resIds.add(R.string.string_comma)
                whitespaceExceptionIndexes.add(resIds.lastIndex)

                if (THEOCRATIC in categories) {
                    resIds.add(R.string.category_devolved_government_in_description_theocratic)
                } else if (SOCIALIST in categories) {
                    resIds.add(R.string.category_devolved_government_in_description_socialist)
                } else if (FASCIST in categories) {
                    resIds.add(R.string.category_devolved_government_in_description_fascist)
                } else {
                    resIds.add(R.string.category_devolved_government_in_description)
                }
            }

            /* Append info about the parent unit */
            if (parentUnit != null) {
                val regionalCat =
                    parentUnit.categories.firstOrNull { it in FlagSuperCategory.Regional.enums() }

                regionalCat?.let {
                    resIds.add(R.string.string_comma)
                    whitespaceExceptionIndexes.add(resIds.lastIndex)

                    resIds.add(R.string.string_in_the)

                    if (AUTONOMOUS_REGION in parentUnit.categories)
                        resIds.add(R.string.categories_autonomous_string)

                    resIds.add(regionalCat.string)
                    resIds.add(R.string.string_of)
                    resIds.add(parentUnit.flagOfLiteral)
                }
            }

            /* Append non-state irregular power information */
            val irregularPowers = listOf(ONE_PARTY, MILITARY_JUNTA, PROVISIONAL_GOVERNMENT)

            if (AUTONOMOUS_REGION in categories && categories.any { it in irregularPowers }) {
                val power = categories.first { it in irregularPowers }

                resIds.add(R.string.string_comma)
                whitespaceExceptionIndexes.add(resIds.lastIndex)

                when (power) {
                    ONE_PARTY ->
                        if (THEOCRATIC in categories) {
                            resIds.add(R.string.category_one_party_in_description_non_state_theocratic)
                        } else if (SOCIALIST in categories) {
                            resIds.add(R.string.category_one_party_in_description_non_state_socialist)
                        } else if (FASCIST in categories) {
                            resIds.add(R.string.category_one_party_in_description_non_state_fascist)
                        } else {
                            resIds.add(R.string.category_one_party_in_description_non_state)
                        }
                    MILITARY_JUNTA ->
                        if (THEOCRATIC in categories) {
                            resIds.add(R.string.category_military_junta_in_description_non_state_theocratic)
                        } else if (SOCIALIST in categories) {
                            resIds.add(R.string.category_military_junta_in_description_non_state_socialist)
                        } else if (FASCIST in categories) {
                            resIds.add(R.string.category_military_junta_in_description_non_state_fascist)
                        } else {
                            resIds.add(R.string.category_military_junta_in_description_non_state)
                        }
                    PROVISIONAL_GOVERNMENT ->
                        if (THEOCRATIC in categories) {
                            resIds.add(R.string.category_provisional_government_in_description_non_state_theocratic)
                        } else if (SOCIALIST in categories) {
                            resIds.add(R.string.category_provisional_government_in_description_non_state_socialist)
                        } else if (FASCIST in categories) {
                            resIds.add(R.string.category_provisional_government_in_description_non_state_fascist)
                        } else {
                            resIds.add(R.string.category_provisional_government_in_description_non_state)
                        }
                    else -> {}
                }
            }

        } else if (isInstitutional) {
            categoriesInstitutional.forEachIndexed { index, category ->
                if (index == 1) resIds.add(R.string.string_of_the)
                resIds.add(category.string)
            }

            if (sovereignState != null) {
                resIds.add(R.string.string_of)

                if (HISTORICAL in sovereignState.categories) {
                    if (sovereignState.isFlagOfOfficialThe) resIds.add(R.string.string_the)
                    resIds.add(R.string.string_defunct) /* DESCRIPTOR */
                    resIds.add(sovereignState.flagOfOfficial) /* FLAG NAME */
                } else {
                    if (sovereignState.isFlagOfThe) resIds.add(R.string.string_the)
                    resIds.add(sovereignState.flagOfLiteral) /* FLAG NAME */
                }
            }

        } else if (isCultural) {
            iterateOverCulturalCategories(
                categories = categoriesCultural,
                stringIds = resIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
            )
        }

        resIds.add(R.string.string_period)
        whitespaceExceptionIndexes.add(resIds.lastIndex)
        /* ------------------------------- Description END ------------------------------- */


        /* ---------- Update state with whitespace resIds inserted ---------- */
        val resIdsComplete = resIds.flatMapIndexed { index, resId ->
            when (index) {
                in whitespaceExceptionIndexes -> listOf(resId)
                else -> listOf(R.string.string_whitespace, resId)
            }
        }
        resIdsComplete.forEachIndexed { index, resId ->
            /* Add index of resId to index lists when also in respective lists */
            when (resId) {
                in clickableResIds -> clickableIndexes.add(index)
                in flagNameResIds -> flagNameIndexes.add(index)
                in descriptorResIds -> descriptorIndexes.add(index)
            }
        }
        _uiState.update {
            it.copy(
                flagScreenContent = FlagScreenContent(
                    flag = flagView,
                    descriptionResIds = resIdsComplete,
                    descriptionClickableFlags = clickableFlags,
                    descriptionClickableWordIndexes = clickableIndexes,
                    descriptionBoldWordIndexes = flagNameIndexes,
                    descriptionLightWordIndexes = descriptorIndexes,
                    isAnimated = isAnimated,
                )
            )
        }
    }


    /* Loop over non-cultural categories and add it's string or alternate strings depending on
     * legibility to resIds list */
    private fun iterateOverPoliticalCategories(
        categories: List<FlagCategory>,
        stringIds: MutableList<Int>,
        whitespaceExceptions: MutableList<Int>,
        isConstitutional: Boolean,
    ) {
        val skipCategories =
            listOf(SOVEREIGN_STATE, FREE_ASSOCIATION, HISTORICAL, DEVOLVED_GOVERNMENT)

        val regionCategories = categories.filter { it in Regional.enums() }
            .toMutableList()
        regionCategories.removeFirstOrNull()

        /* Exception properties */
        val isIrregularPower =
            categories.any { it in listOf(ONE_PARTY, MILITARY_JUNTA, PROVISIONAL_GOVERNMENT) }
        val ideologies = IdeologicalOrientation.enums()
        val ideology = categories.firstOrNull { it in ideologies }


        /* ----- ITERATIONS ----- */
        for (category in categories) {
            if (category in skipCategories) {
                continue

            } else if (category == AUTONOMOUS_REGION) {
                val ideologyRegularPower = if (isIrregularPower) null else ideology

                if (HISTORICAL !in categories && ideologyRegularPower == null) {
                    stringIds.add(R.string.category_autonomous_region_in_description_an)
                    whitespaceExceptions.add(stringIds.lastIndex)
                }
                else {
                    ideologyRegularPower?.let { stringIds.add(it.string) }
                    stringIds.add(R.string.category_autonomous_region_in_description)
                }

            } else if (category == OBLAST && AUTONOMOUS_REGION !in categories) {
                stringIds.add(R.string.category_oblast_string_in_description_an)
                whitespaceExceptions.add(stringIds.lastIndex)

            } else if (category in regionCategories) {
                stringIds.add(R.string.string_and)
                stringIds.add(category.string)
                regionCategories.remove(category)

            } else if (category == INTERNATIONAL_ORGANIZATION) {
                if (categories.any { it in ExecutiveStructure.enums() } &&
                    !categories.any {
                        it in TerritorialDistributionOfAuthority.enums()
                    }) {
                    if (SUPRANATIONAL_UNION in categories) {
                        stringIds.add(R.string.string_and)
                    }
                    stringIds.add(category.string)

                } else if (SUPRANATIONAL_UNION !in categories) {
                    stringIds.add(R.string.category_international_organization_in_description)
                    whitespaceExceptions.add(stringIds.lastIndex)
                    stringIds.add(R.string.string_and)

                } else if (categories.any { it in TerritorialDistributionOfAuthority.enums() }) {
                    stringIds.add(R.string.string_comma)
                    whitespaceExceptions.add(stringIds.lastIndex)
                    stringIds.add(category.string)
                    stringIds.add(R.string.string_and)

                } else {
                    stringIds.add(R.string.string_and)
                    stringIds.add(category.string)
                }

            } else if (category == CONFEDERATION && INTERNATIONAL_ORGANIZATION !in categories) {
                stringIds.add(R.string.category_confederal_string)

            } else if (category in ideologies && AUTONOMOUS_REGION in categories) {
                continue

            } else if (category in ExecutiveStructure.enums() &&
                !categories.any { it in International.enums() } &&
                !isConstitutional) {
                stringIds.add(R.string.category_nominal_extra_constitutional_in_description)
                stringIds.add(category.string)

            } else if (category == CONSTITUTIONAL && MONARCHY !in categories) {
                continue

            } else if (category == THEOCRACY && MONARCHY in categories) {
                stringIds.add(R.string.string_and)
                stringIds.add(category.string)

            } else if (category == ONE_PARTY) {
                if (AUTONOMOUS_REGION !in categories)
                    stringIds.add(R.string.category_one_party_in_description)

            } else if (category == MILITARY_JUNTA) {
                if (AUTONOMOUS_REGION !in categories)
                    stringIds.add(R.string.category_military_junta_in_description)

            } else if (category == PROVISIONAL_GOVERNMENT) {
                if (AUTONOMOUS_REGION !in categories)
                    stringIds.add(R.string.category_provisional_government_in_description)

            } else {
                stringIds.add(category.string)
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
            when (category) {
                REGIONAL -> stringIds.add(R.string.category_regional_string)
                SOCIAL -> stringIds.add(R.string.category_social_in_description)
                POLITICAL -> stringIds.add(R.string.category_political_in_description)
                RELIGIOUS -> stringIds.add(R.string.category_religious_in_description)
                ETHNIC -> when (firstCategory) {
                    ETHNIC -> {
                        stringIds.add(R.string.category_ethnic_in_description_2)
                        whitespaceExceptions.add(stringIds.lastIndex)
                    }
                    else -> stringIds.add(R.string.category_ethnic_in_description_1)
                }
                else -> continue
            }

            if (size > 2 && category !in listOf(penultimateCategory, lastCategory)) {
                stringIds.add(R.string.string_comma)
                whitespaceExceptions.add(stringIds.lastIndex)
                stringIds.add(R.string.string_a)

            } else if (category != lastCategory) {
                stringIds.add(R.string.string_and)
            }
        }
    }
}