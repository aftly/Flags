package dev.aftly.flags.ui.screen.flag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.ANNEXED_TERRITORY
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.ETHNIC
import dev.aftly.flags.model.FlagCategory.FEDERAL
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INDIGENOUS_TERRITORY
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.MICRONATION
import dev.aftly.flags.model.FlagCategory.MICROSTATE
import dev.aftly.flags.model.FlagCategory.MILITANT_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.MILITARY
import dev.aftly.flags.model.FlagCategory.MONARCHY
import dev.aftly.flags.model.FlagCategory.OBLAST
import dev.aftly.flags.model.FlagCategory.ONE_PARTY
import dev.aftly.flags.model.FlagCategory.POLITICAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.POLITICAL_MOVEMENT
import dev.aftly.flags.model.FlagCategory.QUASI_STATE
import dev.aftly.flags.model.FlagCategory.REGIONAL
import dev.aftly.flags.model.FlagCategory.RELIGIOUS
import dev.aftly.flags.model.FlagCategory.SOCIAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_ENTITY
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.SUPRANATIONAL_UNION
import dev.aftly.flags.model.FlagCategory.TERRITORY
import dev.aftly.flags.model.FlagCategory.TERRORIST_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.TRIBE
import dev.aftly.flags.model.FlagCategory.UNICAMERAL
import dev.aftly.flags.model.FlagCategory.UNRECOGNIZED_STATE
import dev.aftly.flags.model.FlagScreenContent
import dev.aftly.flags.model.FlagSuperCategory.AutonomousRegion
import dev.aftly.flags.model.FlagSuperCategory.Civilian
import dev.aftly.flags.model.FlagSuperCategory.ExecutiveStructure
import dev.aftly.flags.model.FlagSuperCategory.IdeologicalOrientation
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Legislature
import dev.aftly.flags.model.FlagSuperCategory.LegislatureDivision
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.PowerDerivation
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.TerritorialDistributionOfAuthority
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.ui.util.getChronologicalRelatedFlagsContentOrNull
import dev.aftly.flags.ui.util.getFlagFromId
import dev.aftly.flags.ui.util.getFlagIdsFromString
import dev.aftly.flags.ui.util.getFlagKey
import dev.aftly.flags.ui.util.getPoliticalRelatedFlagsContentOrNull
import dev.aftly.flags.ui.util.toSavedFlag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlagViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application = app) {
    private val savedFlagsRepository =
        (application as FlagsApplication).container.savedFlagsRepository
    private val _uiState = MutableStateFlow(value = FlagUiState())
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
                val flag = uiState.value.flag
                val savedFlag = savedFlags.find { it.flagKey == getFlagKey(flag) }
                _uiState.update {
                    it.copy(savedFlags = savedFlags.toSet(), savedFlag = savedFlag)
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
            _uiState.update {
                val listFlagIds = flagIdsFromList ?: it.flagIdsFromList

                it.copy(
                    flag = flag,
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
                        savedFlag.flagKey == getFlagKey(flag)
                    }
                )
            }
            updateFlagDescriptionContent(flagView = flag, isAnimated = isAnimated)
        }
    }

    fun updateFlagRelated(
        flag: FlagView,
        relatedMenu: FlagsMenu?,
        isLink: Boolean,
    ) {
        _uiState.update {
            it.copy(latestMenuInteraction = relatedMenu)
        }
        updateFlag(flagId = flag.id, isLink = isLink)
    }

    fun updateSavedFlag() {
        val savedFlag = uiState.value.savedFlag
        val flag = uiState.value.flag

        viewModelScope.launch {
            if (savedFlag != null) {
                savedFlagsRepository.deleteFlag(savedFlag)
            } else {
                savedFlagsRepository.insertFlag(flag.toSavedFlag())
            }
        }
    }

    fun getFlagIds(): List<Int> {
        val latest = uiState.value.latestMenuInteraction
        val flag = uiState.value.flag
        val flagIds = uiState.value.flagIdsFromList
        val polRelated = uiState.value.politicalRelatedFlagsContent
        val chronRelated = uiState.value.chronologicalRelatedFlagsContent

        return if (latest == FlagsMenu.POLITICAL && flag.id !in flagIds) {
            polRelated?.getIds() ?: emptyList()
        } else if (latest == FlagsMenu.CHRONOLOGICAL && flag.id !in flagIds) {
            chronRelated?.getIds() ?: emptyList()
        } else {
            flagIds
        }
    }


    /* Update state with relevant description string resources for resolution in UI layer */
    private fun updateFlagDescriptionContent(
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
        val latestEntities = buildList {
            flag.latestEntityKeys.forEach { key ->
                flagViewMap[key]?.let { add(it) }
            }
        }
        val politicalSuperEnums = Political.allEnums()

        val clickableFlags = buildList {
            previousFlagOf?.let { add(it) }
            associatedState?.let { add(it) }
            sovereignState?.let { add(it) }
            parentUnit?.let { add(it) }
            addAll(elements = latestEntities)
        }
        val clickableResIds = clickableFlags.flatMap {
            listOf(it.flagOf, it.flagOfOfficial)
        }
        val flagNameResIds = listOf(flag.flagOf, flag.flagOfOfficial)
        val descriptorResIds = listOf(
            R.string.category_historical_in_description,
            R.string.category_historical_descriptor,
            R.string.category_annexed_territory_descriptor,
            R.string.categories_non_self_governing_territory_descriptor,
            R.string.category_nominal_extra_constitutional_in_description,
            R.string.string_de_facto_descriptor
        )

        val categoriesPolity = categories.filterNot {
            it in listOf(ETHNIC, SOCIAL)
        }

        val isAnnexed = ANNEXED_TERRITORY in categories
        val isHistorical = HISTORICAL in categories
        val isInternational = categories.any { it in International.enums() }
        val isLegislature = categories.any { it in Legislature.enums() }
        val isNSGT = flag.sovereignStateKey == null &&
                categories.none { it in listOf(SOVEREIGN_STATE, QUASI_STATE) }
        val isDependent = sovereignState != null
        val isChild = parentUnit != null
        val isPowerEnums = categories.any { it in PowerDerivation.enums() }
        val isIrregularPower = (isNSGT && isPowerEnums) || (isDependent && isPowerEnums)
        val isLimitedRecognition = UNRECOGNIZED_STATE in categories

        /* ----------------------- Description START ----------------------- */

        /* Add flag name */
        if (HISTORICAL in categories && flag.flagOfDescriptor != null) {
            if (flag.isFlagOfOfficialThe) resIds.add(R.string.string_the_capitalized)
            resIds.add(flag.flagOfOfficial) /* FLAG NAME */
        } else {
            if (flag.isFlagOfThe) resIds.add(R.string.string_the_capitalized)
            resIds.add(flag.flagOf) /* FLAG NAME */
        }

        /* Add annexed descriptor */
        if (isAnnexed) {
            resIds.add(R.string.category_annexed_territory_descriptor) /* DESCRIPTOR */
        }

        /* Add connector */
        if (isLegislature) {
            resIds.add(R.string.string_is_the)
        } else {
            resIds.add(R.string.string_is_a)
        }

        /* Add historical descriptor */
        if (isHistorical) {
            resIds.add(R.string.category_historical_in_description) /* DESCRIPTOR */
        }


        if (categories.isNotEmpty()) { /* Add categories description */
            addCatDescriptionStrings(
                categories = categoriesPolity,
                resIds = resIds,
                whitespaceExceptionIndexes = whitespaceExceptionIndexes,
                politicalSuperEnums = politicalSuperEnums,
                isHistorical = isHistorical,
                isInternational = isInternational,
                isConstitutional = CONSTITUTIONAL in categories,
                isNSGT = isNSGT,
                isIrregularPower = isIrregularPower,
                isLimitedRecognition = isLimitedRecognition,
                isDependent = isDependent,
                isChild = isChild,
            )

            /* If relevant add strings about the associated, sovereign state or latest entity */
            if (associatedState != null) {
                if (SOVEREIGN_STATE in categories) {
                    resIds.add(R.string.string_comma)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }
                resIds.add(R.string.category_free_association_in_description)

                if (HISTORICAL in associatedState.categories) {
                    if (associatedState.isFlagOfOfficialThe) resIds.add(R.string.string_the)
                    resIds.add(associatedState.flagOfOfficial) /* FLAG NAME */
                } else {
                    if (associatedState.isFlagOfThe) resIds.add(R.string.string_the)
                    resIds.add(associatedState.flagOf) /* FLAG NAME */
                }

            } else if (isChild || isDependent) {
                addParentSovereignDescription(
                    parentUnit = parentUnit,
                    sovereignState = sovereignState,
                    resIds = resIds,
                    whitespaceExceptionIndexes = whitespaceExceptionIndexes,
                    categories = categories,
                    isIrregularPower = isIrregularPower,
                    isLimitedRecognition = isLimitedRecognition,
                    isAnnexed = isAnnexed,
                )
            }

            /* Append devolved government information */
            if (DEVOLVED_GOVERNMENT in categories) {
                resIds.add(R.string.string_comma)
                addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                resIds.add(R.string.category_devolved_government_in_description)
            }

            /* Append non-state irregular power information */
            if (isIrregularPower && !isInternational) {
                val categoriesPolitical = categories.filter { it in politicalSuperEnums }

                resIds.add(R.string.string_comma)
                addLastIndex(from = resIds, to = whitespaceExceptionIndexes)

                resIds.add(R.string.categories_under_a)

                categoriesPolitical.forEach { category ->
                    if (category in ExecutiveStructure.enums() &&
                        CONSTITUTIONAL !in categoriesPolitical) {
                        resIds.add(R.string.category_nominal_extra_constitutional_in_description)
                        resIds.add(category.string)
                    } else if (category == ONE_PARTY) {
                        resIds.add(R.string.category_one_party_string_short)
                    } else {
                        resIds.add(category.string)
                    }
                }
            }
        }

        resIds.add(R.string.string_period)
        addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
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
    private fun addCatDescriptionStrings(
        categories: List<FlagCategory>,
        resIds: MutableList<Int>,
        whitespaceExceptionIndexes: MutableList<Int>,
        politicalSuperEnums: List<FlagCategory>,
        isHistorical: Boolean,
        isInternational: Boolean,
        isConstitutional: Boolean,
        isNSGT: Boolean,
        isIrregularPower: Boolean,
        isLimitedRecognition: Boolean,
        isDependent: Boolean,
        isChild: Boolean,
    ) {
        val firstCategory = categories.first()
        val lastCategory = categories.last()

        val skipCategories = listOf(
            HISTORICAL, SOVEREIGN_STATE, FREE_ASSOCIATION, DEVOLVED_GOVERNMENT, ANNEXED_TERRITORY
        ).toMutableList()

        val legislatureDivisionsOfThe = LegislatureDivision.enums().filterNot { it == UNICAMERAL }

        val entities = buildList {
            addAll(elements = Regional.enums())
            addAll(elements = PowerDerivation.enums())
            addAll(elements = Institution.allEnums())
            addAll(elements =
                listOf(
                    SOVEREIGN_ENTITY, UNRECOGNIZED_STATE, MICROSTATE, QUASI_STATE,
                    MILITANT_ORGANIZATION, TERRORIST_ORGANIZATION, POLITICAL_MOVEMENT,
                    CONFEDERATION, INDIGENOUS_TERRITORY, TRIBE
                )
            )
        }
        val entityCategories = categories.filter { it in entities }.toMutableList()
        val firstEntityCategory = entityCategories.first()

        val isPolity = categories.any {
            it == SOVEREIGN_STATE || it in AutonomousRegion.enums() || it in Regional.enums() ||
                    (it in politicalSuperEnums && it != FEDERAL)
        }
        val isQuasi = QUASI_STATE in categories
        val isExecStruct = categories.any { it in ExecutiveStructure.enums() }
        val isTerriDistr = categories.any { it in TerritorialDistributionOfAuthority.enums() }
        val isSupraUnion = SUPRANATIONAL_UNION in categories

        fun addCatString(category: FlagCategory) = resIds.add(category.string)

        fun getFirstNonSkipCategoryOrNull(
            category: FlagCategory = firstCategory,
            index: Int = categories.indexOf(firstCategory),
        ): FlagCategory? = when {
            category !in skipCategories -> category
            category != lastCategory -> {
                val indexNew = index.inc()
                getFirstNonSkipCategoryOrNull(category = categories[indexNew], index = indexNew)
            }
            else -> null
        }

        fun isCategoryFirst(category: FlagCategory): Boolean =
            getFirstNonSkipCategoryOrNull() == category && !isHistorical

        /* ----- ITERATIONS ----- */
        for (category in categories) {
            val isLast = category == lastCategory

            /* category in conditions */
            when {
                category in skipCategories -> continue

                category in politicalSuperEnums && isIrregularPower && !isInternational -> {
                    skipCategories.add(category)
                    continue
                }

                category in IdeologicalOrientation.enums() && AUTONOMOUS_REGION in categories -> {
                    skipCategories.add(category)
                    continue
                }

                category in ExecutiveStructure.enums() && !isInternational && !isConstitutional ->
                    resIds.add(R.string.category_nominal_extra_constitutional_in_description)

                category in legislatureDivisionsOfThe -> {
                    addCatString(category)
                    resIds.add(R.string.string_of_the)
                    skipCategories.add(category)
                    continue
                }

                category in entityCategories -> {
                    if (category == UNRECOGNIZED_STATE && isQuasi) {
                        entityCategories.remove(element = QUASI_STATE)
                    }
                    entityCategories.remove(element = category)

                    when {
                        category == firstEntityCategory -> false
                        entityCategories.isEmpty() -> resIds.add(R.string.string_and)
                        else -> {
                            resIds.add(R.string.string_comma)
                            addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                        }
                    }
                }
            }

            /* category literal conditions */
            when (category to true) {
                AUTONOMOUS_REGION to isLimitedRecognition -> {
                    skipCategories.add(category)
                    continue
                }
                AUTONOMOUS_REGION to isCategoryFirst(category) -> {
                    resIds.add(R.string.category_autonomous_region_in_description_an)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }
                AUTONOMOUS_REGION to !isLast ->
                    resIds.add(R.string.category_autonomous_region_in_description)

                UNRECOGNIZED_STATE to (isQuasi && isCategoryFirst(category)) -> {
                    resIds.add(R.string.category_unrecognized_state_short_string_an)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                    entityCategories.remove(element = category)
                }
                UNRECOGNIZED_STATE to isQuasi -> {
                    resIds.add(R.string.category_unrecognized_state_short_string)
                    entityCategories.remove(element = category)
                }
                UNRECOGNIZED_STATE to (isCategoryFirst(category)) -> {
                    resIds.add(R.string.category_unrecognized_state_string_an)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }

                OBLAST to isCategoryFirst(category) -> {
                    resIds.add(R.string.category_oblast_string_in_description_an)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }

                TERRITORY to isNSGT -> {
                    resIds.add(R.string.categories_non_self_governing_territory_string)
                    resIds.add(R.string.categories_non_self_governing_territory_descriptor)
                }

                CONFEDERATION to (!isInternational && !isDependent) ->
                    resIds.add(R.string.category_confederal_string)

                RELIGIOUS to isLast ->
                    resIds.add(R.string.category_religious_in_description)

                POLITICAL_ORGANIZATION to (MILITARY in entityCategories) -> {
                    entityCategories.remove(element = MILITARY)
                    skipCategories.add(element = MILITARY)
                    resIds.add(R.string.category_political_military_organization_string)
                }

                MILITARY to (POLITICAL_ORGANIZATION in entityCategories) -> {
                    entityCategories.remove(element = POLITICAL_ORGANIZATION)
                    skipCategories.add(element = POLITICAL_ORGANIZATION)
                    resIds.add(R.string.category_political_military_organization_string)
                }
                MILITARY to isChild -> {
                    resIds.add(R.string.category_military_child_string)
                }
                MILITARY to isInternational -> {
                    resIds.add(R.string.category_military_alliance_string)
                }

                INTERNATIONAL_ORGANIZATION to isPolity -> when {
                    isExecStruct && !isTerriDistr && isSupraUnion -> {
                        resIds.add(R.string.string_and)
                        addCatString(category)
                    }
                }

                INTERNATIONAL_ORGANIZATION to (isPolity && isExecStruct &&
                        !isTerriDistr && isSupraUnion) -> {
                    resIds.add(R.string.string_and)
                    addCatString(category)
                }
                INTERNATIONAL_ORGANIZATION to (isPolity && !isSupraUnion) -> {
                    resIds.add(R.string.category_international_organization_in_description)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                    resIds.add(R.string.string_and)
                }
                INTERNATIONAL_ORGANIZATION to (isPolity && isTerriDistr) -> {
                    resIds.add(R.string.string_comma)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                    addCatString(category)
                    resIds.add(R.string.string_and)
                }
                INTERNATIONAL_ORGANIZATION to isPolity -> {
                    resIds.add(R.string.string_and)
                    addCatString(category)
                }
                INTERNATIONAL_ORGANIZATION to (isCategoryFirst(category) && !isLast) -> {
                    resIds.add(R.string.category_international_organization_in_description_short)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }
                INTERNATIONAL_ORGANIZATION to isCategoryFirst(category) -> {
                    resIds.add(R.string.category_international_organization_in_description)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }
                INTERNATIONAL_ORGANIZATION to !isLast ->
                    resIds.add(R.string.category_international_organization_string_short)

                CONSTITUTIONAL to (MONARCHY !in categories) -> {
                    skipCategories.add(category)
                    continue
                }

                DEVOLVED_GOVERNMENT to !isLast ->
                    resIds.add(R.string.categories_devolved_string)

                ETHNIC to isPolity -> {
                    skipCategories.add(category)
                    continue
                }
                ETHNIC to isCategoryFirst(category) -> {
                    resIds.add(R.string.category_ethnic_string_an)
                    addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
                }

                FEDERAL to !isPolity ->
                    resIds.add(R.string.category_federal_federation_string)

                else -> addCatString(category)
            }
        }
    }

    private fun addParentSovereignDescription(
        parentUnit: FlagView?,
        sovereignState: FlagView?,
        resIds: MutableList<Int>,
        whitespaceExceptionIndexes: MutableList<Int>,
        categories: List<FlagCategory>,
        isIrregularPower: Boolean = false,
        isLimitedRecognition: Boolean = false,
        isAnnexed: Boolean = false,
    ) {
        val isChild = parentUnit != null
        val isDependent = sovereignState != null
        val inCategories = Civilian.enums() + POLITICAL_MOVEMENT + CONFEDERATION + MICROSTATE

        if (isChild) {
            val isParentHistorical = HISTORICAL in parentUnit.categories

            if (isAnnexed || categories.lastOrNull() in inCategories) {
                resIds.add(R.string.string_in)
            } else {
                resIds.add(R.string.string_of)
            }

            /* Historical name and placement of descriptor varies */
            if (isParentHistorical && parentUnit.flagOfDescriptor != null) {
                if (parentUnit.isFlagOfOfficialThe) resIds.add(R.string.string_the)
                resIds.add(parentUnit.flagOfOfficial) /* FLAG NAME */

            } else {
                if (parentUnit.isFlagOfThe) resIds.add(R.string.string_the)
                resIds.add(parentUnit.flagOf) /* FLAG NAME */
            }

            if (isParentHistorical) resIds.add(R.string.category_historical_descriptor) /* DESCRIPTOR */

            if (isDependent) {
                resIds.add(R.string.string_comma)
                addLastIndex(from = resIds, to = whitespaceExceptionIndexes)
            }
        }

        if (isDependent) {
            val isSovHistorical = HISTORICAL in sovereignState.categories
            val isInCategories = listOf(MICRONATION, REGIONAL)

            if (!isChild) {
                if (isIrregularPower ||
                    isLimitedRecognition ||
                    categories.any { it in isInCategories } ||
                    categories.lastOrNull() in inCategories) {
                    resIds.add(R.string.string_in)
                } else {
                    resIds.add(R.string.string_of)
                }
            }

            if (isSovHistorical && sovereignState.flagOfDescriptor != null) {
                if (sovereignState.isFlagOfOfficialThe && !isChild) resIds.add(R.string.string_the)
                resIds.add(sovereignState.flagOfOfficial) /* FLAG NAME */

            } else {
                if (sovereignState.isFlagOfThe && !isChild) resIds.add(R.string.string_the)
                resIds.add(sovereignState.flagOf) /* FLAG NAME */
            }

            if (isChild && isAnnexed) resIds.add(R.string.string_de_facto_descriptor) /* DESCRIPTOR */
            if (isSovHistorical) resIds.add(R.string.category_historical_descriptor) /* DESCRIPTOR */
        }
    }

    private fun addLastIndex(
        from: MutableList<Int>,
        to: MutableList<Int>,
    ) {
        to.add(from.lastIndex)
    }
}