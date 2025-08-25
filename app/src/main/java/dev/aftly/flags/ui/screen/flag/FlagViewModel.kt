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
            updateDescriptionIds(flagView = flag)
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

    fun getFlagIdsPolitical(): List<Int> =
        uiState.value.politicalRelatedFlagsContent?.getIds() ?: emptyList()


    /* Update state with relevant description string resources for resolution in UI layer */
    private fun updateDescriptionIds(flagView: FlagView) {
        /* ---------- Initialise properties for getting description strings ---------- */
        val resIds = mutableListOf<Int>()
        val whitespaceExceptionIndexes = mutableListOf(0)
        val clickableIndexes = mutableListOf<Int>()
        val flagNameIndexes = mutableListOf<Int>()
        val descriptorIndexes = mutableListOf<Int>()

        /* Resources */
        val flag = flagView.previousFlagOfKey?.let { flagViewMap.getValue(it) } ?: flagView
        val categories = flag.categories
        val associatedState = flagViewMap[flag.associatedStateKey]
        val sovereignState = flagViewMap[flag.sovereignStateKey]

        val clickableFlags = buildList {
            associatedState?.let { add(it) }
            sovereignState?.let { add(it) }
        }
        val clickableResIds = buildList {
            associatedState?.let {
                addAll(elements = listOf(it.flagOf, it.flagOfLiteral, it.flagOfOfficial))
            }
            sovereignState?.let {
                addAll(elements = listOf(it.flagOf, it.flagOfLiteral, it.flagOfOfficial))
            }
        }
        val flagNameResIds = listOf(flag.flagOf, flag.flagOfLiteral, flag.flagOfOfficial)
        val descriptorResIds = listOf(
            R.string.category_nominal_extra_constitutional_in_description,
            R.string.string_defunct
        )

        val allCulturalCategories = FlagSuperCategory.Cultural.enums().filterNot { cultural ->
            sovereignState?.let { cultural == REGIONAL } == true
        }
        val categoriesNonCultural = categories.filterNot { category ->
            category == HISTORICAL || category in allCulturalCategories
        }
        val categoriesCultural = categories.filter { it in allCulturalCategories }

        /* Description varies based on the following conditions */
        val isConstitutional = CONSTITUTIONAL in categories
        val isNonCulturalCategoriesInFlag = categoriesNonCultural.isNotEmpty()
        val isCulturalCategoriesInFlag = categoriesCultural.isNotEmpty()


        /* ---------- Add non-cultural description @StringRes Ids to list ---------- */
        if (isNonCulturalCategoriesInFlag) {
            /* Start with flag name, details vary if historical */
            if (HISTORICAL in categories) {
                if (flag.isFlagOfOfficialThe) resIds.add(R.string.string_the_capitalized)
                resIds.add(flag.flagOfOfficial) /* FLAG NAME */
                resIds.add(R.string.string_is_a)
                resIds.add(R.string.string_defunct) /* DESCRIPTOR */
            } else {
                if (flag.isFlagOfThe) resIds.add(R.string.string_the_capitalized)
                resIds.add(flag.flagOfLiteral) /* FLAG NAME */
                resIds.add(R.string.string_is_a)
            }

            /* Loop through categories and add it's string or alternate strings depending on
             * legibility */
            iterateOverNonCulturalCategories(
                categories = categoriesNonCultural,
                stringIds = resIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
                isConstitutional = isConstitutional,
            )

            /* If relevant add strings about the associated state */
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

            }

            /* If relevant add strings about the sovereign state */
            if (sovereignState != null && associatedState == null) {
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

            if (DEVOLVED_GOVERNMENT in categories) {
                resIds.add(R.string.string_comma)
                whitespaceExceptionIndexes.add(resIds.lastIndex)

                resIds.add(R.string.category_devolved_government_in_description)
            }
        }


        /* ---------- Add cultural description @StringRes Ids to list ---------- */
        if (isCulturalCategoriesInFlag && !isNonCulturalCategoriesInFlag) {
            resIds.add(R.string.string_the_capitalized)

            if (HISTORICAL in categories) {
                resIds.add(flag.flagOfOfficial) /* FLAG NAME */
                resIds.add(R.string.category_super_cultural_in_description_historical)
            } else {
                resIds.add(flag.flagOfLiteral) /* FLAG NAME */
                resIds.add(R.string.category_super_cultural_in_description)
            }

            /* Loop through categories and add appropriate @StringRes ids to list */
            iterateOverCulturalCategories(
                categories = categoriesCultural,
                stringIds = resIds,
                whitespaceExceptions = whitespaceExceptionIndexes,
            )
        }


        /* ---------- Description END ---------- */
        resIds.add(R.string.string_period)
        whitespaceExceptionIndexes.add(resIds.lastIndex)
        /* ---------------------------------------- */


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
                descriptionResIds = resIdsComplete,
                descriptionClickableFlags = clickableFlags,
                descriptionClickableWordIndexes = clickableIndexes,
                descriptionBoldWordIndexes = flagNameIndexes,
                descriptionLightWordIndexes = descriptorIndexes,
            )
        }
    }


    /* Loop over non-cultural categories and add it's string or alternate strings depending on
     * legibility to resIds list */
    private fun iterateOverNonCulturalCategories(
        categories: List<FlagCategory>,
        stringIds: MutableList<Int>,
        whitespaceExceptions: MutableList<Int>,
        isConstitutional: Boolean,
    ) {
        val skipCategories =
            listOf(SOVEREIGN_STATE, FREE_ASSOCIATION, HISTORICAL, DEVOLVED_GOVERNMENT)

        val regionCategories = categories.filter { it in FlagSuperCategory.Regional.enums() }
            .toMutableList()
        regionCategories.removeFirstOrNull()

        for (category in categories) {
            if (category in skipCategories) {
                continue

            } else if (category == AUTONOMOUS_REGION) {
                stringIds.add(R.string.category_autonomous_region_in_description_an)
                whitespaceExceptions.add(stringIds.lastIndex)

            } else if (category == OBLAST && AUTONOMOUS_REGION !in categories) {
                stringIds.add(R.string.category_oblast_string_in_description_an)
                whitespaceExceptions.add(stringIds.lastIndex)

            } else if (category in regionCategories) {
                stringIds.add(R.string.string_and)
                stringIds.add(category.string)
                regionCategories.remove(category)

            } else if (category == INTERNATIONAL_ORGANIZATION) {
                if (categories.any { it in FlagSuperCategory.ExecutiveStructure.enums() } &&
                    !categories.any {
                        it in FlagSuperCategory.TerritorialDistributionOfAuthority.enums()
                    }) {
                    if (SUPRANATIONAL_UNION in categories) {
                        stringIds.add(R.string.string_and)
                    }
                    stringIds.add(category.string)

                } else if (SUPRANATIONAL_UNION !in categories) {
                    stringIds.add(R.string.category_international_organization_in_description)
                    whitespaceExceptions.add(stringIds.lastIndex)
                    stringIds.add(R.string.string_and)

                } else if (categories.any {
                    it in FlagSuperCategory.TerritorialDistributionOfAuthority.enums() }) {
                    stringIds.add(R.string.string_comma)
                    whitespaceExceptions.add(stringIds.lastIndex)
                    stringIds.add(category.string)
                    stringIds.add(R.string.string_and)

                } else {
                    stringIds.add(R.string.string_and)
                    stringIds.add(category.string)
                }

            } else if (category in FlagSuperCategory.IdeologicalOrientation.enums() &&
                AUTONOMOUS_REGION in categories) {
                if (DEVOLVED_GOVERNMENT in categories) {
                    stringIds.add(R.string.string_that_is)
                } else if (ONE_PARTY in categories) {
                    stringIds.add(R.string.string_and)
                } else {
                    stringIds.add(R.string.string_with_a)
                }
                stringIds.add(category.string)

            } else if (category in FlagSuperCategory.ExecutiveStructure.enums() &&
                !categories.any { it in FlagSuperCategory.International.enums() } &&
                !isConstitutional) {
                stringIds.add(R.string.category_nominal_extra_constitutional_in_description)
                stringIds.add(category.string)

            } else if (category == CONSTITUTIONAL && MONARCHY !in categories) {
                continue

            } else if (category == ONE_PARTY) {
                stringIds.add(R.string.category_one_party_in_description)

            } else if (category == THEOCRACY && MONARCHY in categories) {
                stringIds.add(R.string.string_and)
                stringIds.add(category.string)

            } else if (category == MILITARY_JUNTA) {
                stringIds.add(R.string.category_military_junta_in_description)

            } else if (category == PROVISIONAL_GOVERNMENT) {
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