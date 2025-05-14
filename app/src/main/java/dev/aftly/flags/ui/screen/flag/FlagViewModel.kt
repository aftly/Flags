package dev.aftly.flags.ui.screen.flag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.flagsMapId
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
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class FlagViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(FlagUiState())
    val uiState: StateFlow<FlagUiState> = _uiState.asStateFlow()

    init {
        /* Initialise state from nav arg via SavedStateHandle */
        val flagArg = savedStateHandle.get<Int>("flag")
        val flagsArg = savedStateHandle.get<String>("flags")

        if (flagArg != null && flagsArg != null) {
            val flag = flagsMapId.getValue(flagArg)
            val flags = flagsArg.split(",").mapNotNull { it.toIntOrNull() }

            _uiState.update { currentState ->
                currentState.copy(
                    flag = flag,
                    flags = flags,
                    descriptionStringResIds = getDescriptionIds(flag = flag),
                )
            }
            updateDescriptionString()
        }
    }


    fun updateFlag(flagId: Int) {
        val newFlag = flagsMapId.getValue(flagId)
        if (newFlag != uiState.value.flag) {
            _uiState.update {
                it.copy(
                    flag = newFlag,
                    descriptionStringResIds = getDescriptionIds(flag = newFlag)
                )
            }
            updateDescriptionString()
        }
    }


    /* Convert the @StringRes list into a legible string
     * Also for execution upon locale/language configuration changes */
    fun updateDescriptionString() {
        val appResources = getApplication<Application>().applicationContext.resources
        val stringIds = uiState.value.descriptionStringResIds
        val whitespaceExceptions = uiState.value.descriptionIdsWhitespaceExceptions
        val strings = mutableListOf<String>()
        val flagOfIndexes = when (uiState.value.descriptionBoldWordIndexes) {
            emptyList<Int>() -> mutableListOf<Int>()
            else -> null
        }
        val allFlagOfIds = when (flagOfIndexes) {
            null -> emptyList()
            else -> allFlagsList.map { it.flagOf }
        }

        /* Loop through stringResIds and add their corresponding strings to stringList
         * (and with whitespace when appropriate) */
        for ((index, stringId) in stringIds.withIndex()) {
            if (index !in whitespaceExceptions) {
                strings.add(appResources.getString(R.string.string_whitespace))
            }
            strings.add(appResources.getString(stringId))

            /* Add index of word in stringList to flagOfPositions if it is a flag name */
            if (flagOfIndexes != null) {
                if (stringId in allFlagOfIds) {
                    flagOfIndexes.add(element = strings.lastIndex)
                }
            }
        }

        /* If boldWordPositions state has not yet been set, update it with flagOf positions */
        if (flagOfIndexes != null) {
            _uiState.update { currentState ->
                currentState.copy(descriptionBoldWordIndexes = flagOfIndexes.toList())
            }
        }

        /* Update description state with stringList */
        _uiState.update { currentState ->
            currentState.copy(description = strings.toList())
        }
    }


    /* Return appropriate @StringRes list from flag info to make a legible entity description
     * Also, update state with whitespace exceptions for @StringRes list */
    private fun getDescriptionIds(flag: FlagResources): List<Int> {
        val categories = flag.categories
        val stringIds = mutableListOf<Int>()
        val whitespaceExceptionIndexes = mutableListOf(0)

        val allCulturalCategories = FlagSuperCategory.Cultural.subCategories
        val allNonCulturalCategories = FlagSuperCategory.All.subCategories.filterNot {
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
            stringIds.add(element = flag.flagOf)

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
            if (flag.associatedState != null) {
                val associatedState = flagsMap.getValue(flag.associatedState)

                stringIds.add(element = R.string.category_free_association_in_description)
                if (associatedState.isFlagOfThe) stringIds.add(element = R.string.string_the)
                stringIds.add(element = associatedState.flagOf)
            }

            /* If relevant add strings about the sovereign state */
            if (flag.sovereignState != null) {
                val sovereign = flagsMap.getValue(flag.sovereignState)
                val isSovereignConstitutional = CONSTITUTIONAL in sovereign.categories

                /* If sovereign state isn't the associated state add it's name info */
                if (flag.associatedState == null) {
                    stringIds.add(element = R.string.string_within)
                    if (sovereign.isFlagOfThe) stringIds.add(element = R.string.string_the)
                    stringIds.add(element = sovereign.flagOf)
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
            stringIds.add(element = flag.flagOf)

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
                if (categories.any { it in FlagSuperCategory.ExecutiveStructure.subCategories } &&
                    !categories.any {
                        it in FlagSuperCategory.TerritorialDistributionOfAuthority.subCategories
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
                    it in FlagSuperCategory.TerritorialDistributionOfAuthority.subCategories }) {
                    stringIds.add(element = R.string.string_comma)
                    whitespaceExceptions.add(element = stringIds.lastIndex)
                    stringIds.add(element = category.string)
                    stringIds.add(element = R.string.string_and)

                } else {
                    stringIds.add(element = R.string.string_and)
                    stringIds.add(element = category.string)
                }

            } else if (category in FlagSuperCategory.IdeologicalOrientation.subCategories &&
                AUTONOMOUS_REGION in categories) {
                if (DEVOLVED_GOVERNMENT in categories) {
                    stringIds.add(element = R.string.string_that_is)
                } else if (ONE_PARTY in categories) {
                    stringIds.add(element = R.string.string_and)
                } else {
                    stringIds.add(element = R.string.string_with_a)
                }
                stringIds.add(element = category.string)

            } else if (category in FlagSuperCategory.ExecutiveStructure.subCategories &&
                !categories.any { it in FlagSuperCategory.International.subCategories } &&
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