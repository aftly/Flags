package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.absenceCategoriesAddAnyMap
import dev.aftly.flags.data.DataSource.absenceCategoriesMap
import dev.aftly.flags.data.DataSource.categoriesAutonomousRegion
import dev.aftly.flags.data.DataSource.categoriesAutonomousRegionPairs
import dev.aftly.flags.data.DataSource.categoriesCivilian
import dev.aftly.flags.data.DataSource.categoriesCivilianPairs
import dev.aftly.flags.data.DataSource.categoriesCultural
import dev.aftly.flags.data.DataSource.categoriesCulturalPairs
import dev.aftly.flags.data.DataSource.categoriesDevolvedGovernment
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfAutonomousRegion
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfCivilian
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfCultural
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfDevolvedGovernment
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfExecutive
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfIndigenousTerritory
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfInternational
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfLegislature
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfPolitical
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfRegional
import dev.aftly.flags.data.DataSource.categoriesExecutive
import dev.aftly.flags.data.DataSource.categoriesExecutivePairs
import dev.aftly.flags.data.DataSource.categoriesInclusiveOfQuasiState
import dev.aftly.flags.data.DataSource.categoriesInclusiveOfSovereignEntity
import dev.aftly.flags.data.DataSource.categoriesInclusiveOfSovereignState
import dev.aftly.flags.data.DataSource.categoriesIndigenousTerritory
import dev.aftly.flags.data.DataSource.categoriesInternational
import dev.aftly.flags.data.DataSource.categoriesLegislature
import dev.aftly.flags.data.DataSource.categoriesMutuallyExclusive
import dev.aftly.flags.data.DataSource.categoriesPolitical
import dev.aftly.flags.data.DataSource.categoriesQuasiState
import dev.aftly.flags.data.DataSource.categoriesRegional
import dev.aftly.flags.data.DataSource.categoriesRegionalPairs
import dev.aftly.flags.data.DataSource.categoriesSovereignEntity
import dev.aftly.flags.data.DataSource.categoriesSovereignState
import dev.aftly.flags.data.DataSource.categoriesSovereignStateExceptionPairs
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.switchSubsSuperCategories
import dev.aftly.flags.data.DataSource.switchSupersSuperCategories
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.ANNEXED_TERRITORY
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.MICROSTATE
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.ONE_PARTY
import dev.aftly.flags.model.FlagCategory.POLITICAL_MOVEMENT
import dev.aftly.flags.model.FlagCategory.PROVISIONAL_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.QUASI_STATE
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_ENTITY
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.RELIGIOUS
import dev.aftly.flags.model.FlagCategory.TERRITORY
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagCategory.UNRECOGNIZED_STATE
import dev.aftly.flags.model.FlagCategory.VEXILLOLOGY
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.AutonomousRegion
import dev.aftly.flags.model.FlagSuperCategory.Cultural
import dev.aftly.flags.model.FlagSuperCategory.ExecutiveStructure
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.IdeologicalOrientation
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.LegalConstraint
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.PowerDerivation
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.Sovereign
import dev.aftly.flags.model.FlagSuperCategory.TerritorialDistributionOfAuthority
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.FlagsOfCountry
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.model.menu.FlagsMenu.CHRONOLOGICAL
import dev.aftly.flags.model.menu.FlagsMenu.FILTER
import dev.aftly.flags.model.menu.FlagsMenu.POLITICAL


/* ------ General category helpers ------ */

/* Get single category (stringResId) title from single or multiple selected categories */
fun getSingleCategoryPreviewTitleOrNull(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): Int? {
    return if (superCategories.size == 1 && subCategories.isEmpty()) {
        superCategories.first().gameScoreCategoryPreview

    } else if (subCategories.size == 1 && superCategories.isEmpty()) {
        subCategories.first().title

    } else {
        null
    }
}

fun isOnlySovereignState(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): Boolean = when (superCategories.size to subCategories.size) {
    1 to 1 -> Sovereign in superCategories && SOVEREIGN_STATE in subCategories
    0 to 1 -> SOVEREIGN_STATE in subCategories
    else -> false
}

/* Extension functions */
fun FlagCategory.toWrapper(): FlagCategoryWrapper = FlagCategoryWrapper(enum = this)

fun ScoreItem.isCategoriesEmpty(): Boolean =
    gameSuperCategories.isEmpty() && gameSubCategories.isEmpty()

fun ScoreItem.superCategories(): List<FlagSuperCategory> =
    gameSuperCategories.filterIsInstance<FlagSuperCategory>()


@Composable
fun FlagsMenu.color(): Color = when (this) {
    FILTER -> MaterialTheme.colorScheme.onSecondaryContainer
    CHRONOLOGICAL -> MaterialTheme.colorScheme.tertiary
    POLITICAL -> MaterialTheme.colorScheme.secondary
}

@Composable
fun FlagsMenu.colorSelect(): Color = when (this) {
    FILTER -> MaterialTheme.colorScheme.secondary
    CHRONOLOGICAL -> MaterialTheme.colorScheme.onTertiaryContainer
    POLITICAL -> MaterialTheme.colorScheme.onSecondaryContainer
}


/* ------ For updateCurrentCategory() in ViewModels ------ */

fun getFlagsFromCategory(
    category: FlagCategoryBase,
    allFlags: List<FlagView>,
): List<FlagView> {
    /* Exclude flags if they have a particular category/categories */
    val categoriesNot = when (category) {
        is FlagCategoryWrapper -> absenceCategoriesMap[category.enum] ?: emptyList()
        else -> emptyList()
    }

    /* Generate list of categories to iterate over (for flags list) from params */
    val categories = when (category) {
        is FlagSuperCategory -> category.allEnums()
        is FlagCategoryWrapper -> {
            if (category.enum == NOMINAL_EXTRA_CONSTITUTIONAL) listOf(SOVEREIGN_STATE)
            else if (category.enum == THEOCRATIC) listOf(category.enum, THEOCRACY)
            else listOf(category.enum)
        }
        is FlagsOfCountry -> emptyList()
    }

    /* For skipping historical flags when category in exception */
    val parentCategory = getParentSuperCategory(category)
    val isHistoricalException = parentCategory in DataSource.historicalSuperCategoryBlacklist &&
            if (category is FlagCategoryWrapper) category.enum !in historicalSubCategoryWhitelist
            else true

    return allFlags.filter { flag ->
        when {
            isHistoricalException && HISTORICAL in flag.categories -> false
            categoriesNot.any { it in flag.categories } -> false
            categories.any { it in flag.categories } -> true
            else -> false
        }
    }
}

fun getParentSuperCategory(
    category: FlagCategoryBase
): FlagSuperCategory = when (category) {
    is FlagSuperCategory -> category
    is FlagCategoryWrapper -> menuSuperCategoryList.filterNot { it == All || it == Political }
        .find { category.enum in it.enums() } ?: Political
    is FlagsOfCountry -> Political
}


/* ------ For updateCurrentCategories() in ViewModels ------ */

fun isCategoryExit(
    category: FlagCategoryBase,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Boolean = when {
    subCategories.isEmpty() && superCategories.isEmpty() -> true /* If SavedFlags */

    category is FlagSuperCategory && category in superCategories -> false /* For deselection */
    category is FlagCategoryWrapper && category.enum in subCategories -> false /* For deselection */

    category is FlagSuperCategory && category != All &&
            subCategories.any { it in category.enums() } -> true /* If category is selected parent */

    else -> isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesMutuallyExclusive,
        categories = categoriesMutuallyExclusive,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryNotInclusive(
        category = category,
        inclusiveOf = categoriesSovereignState,
        categories = categoriesInclusiveOfSovereignState,
        pairExceptions = categoriesSovereignStateExceptionPairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryNotInclusive(
        category = category,
        inclusiveOf = categoriesSovereignEntity,
        categories = categoriesInclusiveOfSovereignEntity,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesAutonomousRegion,
        categories = categoriesExclusiveOfAutonomousRegion,
        pairExceptions = categoriesAutonomousRegionPairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesRegional,
        categories = categoriesExclusiveOfRegional,
        pairExceptions = categoriesRegionalPairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesInternational,
        categories = categoriesExclusiveOfInternational,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesLegislature,
        categories = categoriesExclusiveOfLegislature,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesExecutive,
        categories = categoriesExclusiveOfExecutive,
        pairExceptions = categoriesExecutivePairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesCivilian,
        categories = categoriesExclusiveOfCivilian,
        pairExceptions = categoriesCivilianPairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesCultural,
        categories = categoriesExclusiveOfCultural,
        pairExceptions = categoriesCulturalPairs,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesPolitical,
        categories = categoriesExclusiveOfPolitical,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesDevolvedGovernment,
        categories = categoriesExclusiveOfDevolvedGovernment,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryExclusive(
        category = category,
        exclusiveOf = categoriesIndigenousTerritory,
        categories = categoriesExclusiveOfIndigenousTerritory,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories

    ) || isCategoryNotInclusive(
        category = category,
        inclusiveOf = categoriesQuasiState,
        categories = categoriesInclusiveOfQuasiState,
        selectedSuperCategories = superCategories,
        selectedSubCategories = subCategories
    )
}

private fun isCategoryExclusive(
    category: FlagCategoryBase, /* New selection */
    exclusiveOf: List<FlagCategoryBase>,
    categories: List<FlagCategoryBase>, /* All categories exclusive of any exclusiveOf category */
    pairExceptions: List<Pair<FlagCategoryBase, FlagCategoryBase>> = emptyList(),
    selectedSuperCategories: List<FlagSuperCategory>, /* Currently selected */
    selectedSubCategories: List<FlagCategory>, /* Currently selected */
): Boolean {
    val isCategoryExclusiveOf = category in exclusiveOf
    val isCategoryRelevant = category in categories

    /* Escape early if new category not relevant */
    if (!isCategoryExclusiveOf && !isCategoryRelevant) return false

    /* flatten selected categories for simpler parsing */
    val flatSelectedCategories = buildList {
        addAll(elements = selectedSuperCategories)
        addAll(elements = selectedSubCategories.map { it.toWrapper() })
    }
    val isAnyExclusiveOfSelected = exclusiveOf.any { it in flatSelectedCategories }
    val isAnyCategoriesSelected = flatSelectedCategories.any { it in categories }

    val isPairException = pairExceptions.let { exceptions ->
        for (pair in exceptions) {
            val isPairValid = pair.first in exclusiveOf && pair.second in categories

            val isCatPairFirst = pair.first == category
            val isCatPairSecond = pair.second == category

            val isOnlyPairFirstSelected = flatSelectedCategories.none {
                it != pair.first && it in exclusiveOf
            }
            val isOnlyPairSecondSelected = flatSelectedCategories.none {
                it != pair.second && it in categories
            }

            /* If pair exception is relevant return true to let */
            when {
                isPairValid && isCatPairFirst && isOnlyPairSecondSelected -> return@let true
                isPairValid && isCatPairSecond && isOnlyPairFirstSelected -> return@let true
                else -> continue
            }
        }
        /* If no pair exception is relevant return false to let */
        return@let false
    }

    /* Enforce intra-category exceptions when exclusiveOf and categories are the same */
    val isException = when {
        exclusiveOf == categories && category is FlagCategoryWrapper -> {
            val superOfCategory = categories.filterIsInstance<FlagSuperCategory>()
                .find { category.enum in it.enums() }

            superOfCategory in selectedSuperCategories ||
                    superOfCategory?.enums()?.any { it in selectedSubCategories } == true
        }
        else -> false
    }

    return when {
        isCategoryExclusiveOf && isAnyCategoriesSelected && !isPairException && !isException -> true
        isCategoryRelevant && isAnyExclusiveOfSelected && !isPairException && !isException  -> true
        else -> false
    }
}

private fun isCategoryNotInclusive(
    category: FlagCategoryBase, /* New selection */
    inclusiveOf: List<FlagCategoryBase>,
    categories: List<FlagCategoryBase>, /* All categories inclusive of any inclusiveOf category */
    pairExceptions: List<Pair<FlagCategoryBase, FlagCategoryBase>> = emptyList(),
    selectedSuperCategories: List<FlagSuperCategory>, /* Currently selected */
    selectedSubCategories: List<FlagCategory>, /* Currently selected */
): Boolean {
    /* flatten selected categories for simpler parsing */
    val flatSelectedCategories = buildList {
        addAll(elements = selectedSuperCategories)
        addAll(elements = selectedSubCategories.map { it.toWrapper() })
    }

    val isCategoryInclusiveOf = category in inclusiveOf
    val isCategoryRelevant = category in categories

    val isAnyInclusiveOfSelected = inclusiveOf.any { it in flatSelectedCategories }
    val isOnlyInclusiveCategoriesSelected = flatSelectedCategories.none { it !in categories }

    val relevantPairExceptions = buildList {
        pairExceptions.forEach { pair ->
            val isFirstValid = pair.first in inclusiveOf
            val isFirstCategory = pair.first == category
            val isFirstSelected = pair.first in flatSelectedCategories
            val isSecondCategory = pair.second == category
            val isSecondSelected = pair.second in flatSelectedCategories

            /* If pair exception is relevant add to list */
            when {
                isFirstValid && isFirstCategory && isSecondSelected -> add(pair)
                isFirstValid && isSecondCategory && isFirstSelected -> add(pair)
            }
        }
    }
    val relevantPairFirsts = relevantPairExceptions.map { it.first }
    val isPairException = relevantPairExceptions.isNotEmpty() &&
            !inclusiveOf.any { it in flatSelectedCategories && it !in relevantPairFirsts }

    /* Enforce intra-category select/switch exceptions when relevant */
    val isException = when {
        category is FlagCategoryWrapper -> selectedSuperCategories.any { superCategory ->
            val enums = superCategory.enums()
            category.enum in enums && enums.none { it in selectedSubCategories }

        } || switchSubsSuperCategories.any { switchSuper ->
            val enums = switchSuper.enums()
            val enumsWrapper = enums.map { it.toWrapper() }

            category.enum in enums && flatSelectedCategories.all {
                it == switchSuper || it in enumsWrapper
            }
        }
        else -> false
    }

    return when {
        isCategoryInclusiveOf && !isOnlyInclusiveCategoriesSelected && !isPairException && !isException -> true
        isAnyInclusiveOfSelected && !isCategoryRelevant && !isPairException && !isException -> true
        else -> false
    }
}

/* Returns bool pair, first if update deselects a category, second if update replaces a category */
fun updateCategoriesFromSuper(
    superCategory: FlagSuperCategory,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Pair<Boolean, Boolean> {
    val isSwitchSuper = switchSupersSuperCategories.find { superCategory in it.supers() }

    return if (superCategory in superCategories) {
        /* Handle removal from superCategories */
        superCategories.remove(element = superCategory)
        true to false // isDeselect

    } else if (subCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
        superCategory.firstCategoryEnumOrNull() in subCategories) {
        /* Handle removal from subCategories (not mutually exclusive from above) */
        subCategories.remove(element = superCategory.firstCategoryEnumOrNull())
        true to false // isDeselect

    } else if (isSwitchSuper != null) {
        superCategories.removeAll(elements = isSwitchSuper.supers())
        superCategories.add(superCategory)
        false to true // isSwitch

    } else {
        superCategories.add(superCategory)
        false to false
    }
}

/* Returns bool pair, first if update deselects a category, second if update replaces a category */
fun updateCategoriesFromSub(
    subCategory: FlagCategory,
    subCategories: MutableList<FlagCategory>,
): Pair<Boolean, Boolean> {
    val isDeselect = subCategory in subCategories
    val isSwitchSuper = switchSubsSuperCategories.find { subCategory in it.enums() }

    return if (isDeselect) {
        subCategories.remove(element = subCategory)
        true to false // isDeselect

    } else if (isSwitchSuper != null) {
        subCategories.removeAll(elements = isSwitchSuper.enums())
        subCategories.add(subCategory)
        false to true // isSwitch

    } else {
        subCategories.add(subCategory)
        false to false
    }
}

fun getFlagsFromCategories(
    allFlags: List<FlagView>,
    currentFlags: List<FlagView>,
    isDeselectSwitch: Pair<Boolean, Boolean>,
    superCategory: FlagSuperCategory?,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): List<FlagView> {
    val isDeselectOrSwitch = isDeselectSwitch.first || isDeselectSwitch.second
    val isSuperCategories = superCategories.isNotEmpty()
    val isSubCategories = subCategories.isNotEmpty()
    val flags = if (isDeselectOrSwitch || superCategory in listOf(All, Historical))
        allFlags else currentFlags

    return flags.filter { flag ->
        /* Filter flags from not empty superCategories */
        if (!isSuperCategories) true
        else superCategories.all { superCategory ->
            flag.categories.any { it in superCategory.allEnums() }
        }
    }.filter { flag ->
        /* Filter flags from not empty subCategories */
        if (!isSubCategories) true
        else {
            val absenceKeyValues = absenceCategoriesMap.filterKeys { it in subCategories }
            val nonAbsenceCategories = subCategories.filterNot { it in absenceKeyValues.keys }
            val absenceAddAny = absenceCategoriesAddAnyMap.filterKeys { it in subCategories }

            val isCategoryMatch = flag.categories.containsAll(elements = nonAbsenceCategories)
            /* Empty absence values return true due to nature of none() and all() */
            val isNotAbsent = flag.categories.none { it in absenceKeyValues.values.flatten() }
            val isAbsenceAdd = flag.categories.any { cat -> absenceAddAny.all { cat in it.value } }

            isCategoryMatch && isNotAbsent && isAbsenceAdd
        }
    }.filterNot { flag ->
        /* Handle unintended isDelect and isSwitch effects */
        if (!isDeselectOrSwitch) false
        else {
            val isHistorical = HISTORICAL in flag.categories
            val isNotSuperExceptions = superCategories
                .none { it in listOf(All, Historical, Cultural) }
            val isNotSubExceptions = subCategories
                .none { it in Cultural.enums() + historicalSubCategoryWhitelist }

            isHistorical && isNotSuperExceptions && isNotSubExceptions
        }
    }
}


/* ------ For CategoriesMenuButton title and GameOver share text ------ */

fun filterRedundantSuperCategories(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): List<FlagSuperCategory> {
    return if (superCategories.size <= 1 && subCategories.isEmpty()) {
        /* Skip filtering when redundant */
        superCategories
    } else {
        superCategories.filterNot { superCategory ->
            val isSuperOverlap = superCategories.any { it in superCategory.allChildSupers() }
            val isSubOverlap = subCategories.any { it in superCategory.allEnums() }

            /* Filter super when children in supers or subs */
            superCategory == All || isSuperOverlap || isSubOverlap
        }
    }
}

fun getCategoriesTitleIds(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    filterByCountry: FlagView?,
    isAppendFlags: Boolean = true,
): List<Int> {
    val strings = mutableListOf<Int>()

    /* -------------- Apply country filter strings -------------- */
    if (filterByCountry != null) {
        strings.add(filterByCountry.flagOf)
        strings.add(R.string.string_colon_whitespace)
    }

    /* ---------- Escape when saved flags --------- */
    if (superCategories.isEmpty() && subCategories.isEmpty()) {
        strings.add(R.string.saved_flags)
        if (isAppendFlags) strings.add(R.string.button_title_flags)
        return strings
    }


    /* -------------------------------- GET PROPERTIES -------------------------------- */

    /* Filter out supers when any of it's subs are selected and filter out All super when multiple
     * and return mutable list for further filtering */
    val superCategoriesFiltered = filterRedundantSuperCategories(
        superCategories = superCategories,
        subCategories = subCategories,
    ).toMutableList()

    /* For sorting political categories by their index in this list */
    val politicalCategoriesSortOrder = listOf(
        TerritorialDistributionOfAuthority.enums(),
        ExecutiveStructure.enums(),
        LegalConstraint.enums(),
        IdeologicalOrientation.enums(),
        PowerDerivation.enums()
    ).flatten()


    /* ------------------- Boolean exceptions ------------------- */
    val isHistorical = Historical in superCategoriesFiltered

    val isCultural = Cultural in superCategoriesFiltered

    val isInternational = International in superCategoriesFiltered &&
            (Institution.allSupers().any { it in superCategoriesFiltered } ||
                    Institution.allEnums().any { it in subCategories } ||
                    CONFEDERATION in subCategories)

    val isSovereign = Sovereign in superCategoriesFiltered
    val isSovereignState = SOVEREIGN_STATE in subCategories
    val isSovereignEntity = SOVEREIGN_ENTITY in subCategories
    val isMicrostate = MICROSTATE in subCategories
    val isCountry = isSovereign || isSovereignState || isMicrostate

    val isCountryAutonomousCategories = isCountry && AutonomousRegion in superCategoriesFiltered
    val isAssociatedCountry = isCountry && FREE_ASSOCIATION in subCategories

    val isAnnexed = ANNEXED_TERRITORY in subCategories
    val isUnrecognizedState = UNRECOGNIZED_STATE in subCategories
    val isQuasiState = QUASI_STATE in subCategories

    /* For string exceptions when supers mean non-sovereign autonomous and associated regions */
    val isAutonomousRegionalSupers = superCategoriesFiltered.containsAll(
        elements = listOf(AutonomousRegion, Regional)
    )

    /* For string exceptions when regional super or subs are autonomous */
    val isAutonomousRegion =
        (AutonomousRegion in superCategoriesFiltered || AUTONOMOUS_REGION in subCategories) &&
                (Regional in superCategoriesFiltered || subCategories.any { it in Regional.enums() })

    /* For string exceptions when regional super or subs are devolved */
    val isDevolvedRegion = DEVOLVED_GOVERNMENT in subCategories &&
            (Regional in superCategoriesFiltered ||
                    subCategories
                        .any { it in Regional.enums() + AUTONOMOUS_REGION + DEVOLVED_GOVERNMENT })


    /* ------------------- Category groups ------------------- */
    val culturalCategories = subCategories.filter { it in Cultural.enums() }.toMutableList()

    /* Political categories, sorted semantically, and exceptions applied */
    val polCats = subCategories.filter { it in Political.allEnums() }

    val isConfederationException = CONFEDERATION in subCategories && subCategories
        .none { it in PowerDerivation.enums() } && polCats.size > 1 || isInternational

    val politicalCategories = polCats.filterNot { it == CONFEDERATION && isConfederationException }
        .sortedBy { politicalCategoriesSortOrder.indexOf(it) }

    /* Subcategories not in other list derivatives, minus duplicates */
    val remainingCategories = subCategories.filterNot { subCategory ->
        subCategory in culturalCategories || subCategory in politicalCategories
    }.toMutableList()


    /* -------------------------------- GET ITERATIONS -------------------------------- */
    if (isHistorical) {
        superCategoriesFiltered.remove(element = Historical)
        strings.add(Historical.title)
        strings.add(R.string.string_whitespace)
    }

    if (isQuasiState && POLITICAL_MOVEMENT in culturalCategories) {
        culturalCategories.remove(element = POLITICAL_MOVEMENT)
        strings.add(POLITICAL_MOVEMENT.title)
        strings.add(R.string.string_ampersand)
    }

    if (isAnnexed) {
        remainingCategories.remove(element = ANNEXED_TERRITORY)

        val isEmpty =
            (politicalCategories + superCategoriesFiltered + remainingCategories).isEmpty()

        if (isEmpty) {
            strings.add(ANNEXED_TERRITORY.title)
        } else {
            strings.add(R.string.category_annexed_territory_title_short)
            strings.add(R.string.string_whitespace)
        }
    }

    if (isUnrecognizedState) {
        remainingCategories.remove(element = UNRECOGNIZED_STATE)

        val isEmpty =
            (politicalCategories + superCategoriesFiltered + remainingCategories).isEmpty()

        if (isEmpty) {
            strings.add(UNRECOGNIZED_STATE.title)
        } else {
            strings.add(R.string.category_unrecognized_state_short_title)
            strings.add(R.string.string_whitespace)
        }
    }

    if (isQuasiState && AutonomousRegion in superCategoriesFiltered) {
        superCategoriesFiltered.remove(element = AutonomousRegion)
        strings.add(R.string.categories_unrecognized_or_annexed)
        strings.add(R.string.string_whitespace)
    }

    culturalCategories.forEachIndexed { index, culturalCategory ->
        strings.add(culturalCategory.title)

        if (culturalCategory == RELIGIOUS && isSovereign) {
            superCategoriesFiltered.remove(element = Sovereign)
            strings.add(R.string.string_whitespace)
            strings.add(SOVEREIGN_ENTITY.title)
        }

        if (index != culturalCategories.lastIndex) strings.add(R.string.string_ampersand)
        else strings.add(R.string.string_whitespace)
    }

    if (isCultural) {
        superCategoriesFiltered.remove(element = Cultural)

        val isEmpty =
            (politicalCategories + superCategoriesFiltered + remainingCategories).isEmpty()

        when {
            isSovereign -> {
                superCategoriesFiltered.remove(element = Sovereign)
                strings.add(RELIGIOUS.title)
                strings.add(R.string.string_whitespace)
                strings.add(SOVEREIGN_ENTITY.title)
            }
            else -> {
                strings.add(Cultural.title)
                if (!isEmpty) strings.add(R.string.string_whitespace)
            }
        }
    }

    politicalCategories.forEachIndexed { index, politicalCategory ->
        if (politicalCategory == CONFEDERATION && politicalCategories.size > 1) {
            strings.add(R.string.category_confederal_title)
        } else {
            strings.add(politicalCategory.title)
        }
        strings.add(R.string.string_whitespace)

        /* On last iteration, if not international or sov entity, remove Sovereign super and
         * append "State" if not exception */
        if (index == politicalCategories.lastIndex && International !in superCategoriesFiltered) {
            superCategoriesFiltered.remove(element = Sovereign)
            remainingCategories.remove(element = SOVEREIGN_STATE)
            remainingCategories.remove(element = SOVEREIGN_ENTITY)

            val isNotPowerException = politicalCategory !in PowerDerivation.enums()
                .filterNot { it in listOf(ONE_PARTY, PROVISIONAL_GOVERNMENT) }
            val isNotConfederation = politicalCategory != CONFEDERATION
            val isNotAutonomous = AutonomousRegion !in superCategories &&
                    AutonomousRegion.enums().none { it in subCategories }

            if (isNotPowerException && isNotAutonomous &&
                isNotConfederation && !isConfederationException) {
                when {
                    isMicrostate -> null
                    isSovereignEntity -> strings.add(R.string.category_entity_title)
                    else -> strings.add(R.string.category_state_title)
                }
            }
        }
    }

    if (isInternational) {
        superCategoriesFiltered.remove(element = International)
        strings.add(International.title)
        strings.add(R.string.string_whitespace)
    }

    if (isCountryAutonomousCategories || isAssociatedCountry) {
        superCategoriesFiltered.removeAll(elements = listOf(Sovereign, AutonomousRegion))
        remainingCategories.removeAll(
            elements = listOf(SOVEREIGN_STATE, FREE_ASSOCIATION, MICROSTATE)
        )
        when {
            isCountryAutonomousCategories && isMicrostate -> strings.add(
                R.string.categories_associated_or_annexed_microstate
            )
            isCountryAutonomousCategories -> strings.add(
                R.string.categories_associated_or_annexed_country
            )
            isMicrostate -> strings.add(R.string.categories_associated_microstate)
            else -> strings.add(R.string.categories_associated_country)
        }
        strings.add(R.string.string_whitespace)
    }

    if (isDevolvedRegion) {
        remainingCategories.remove(element = DEVOLVED_GOVERNMENT)
        strings.add(R.string.categories_devolved_title)
        strings.add(R.string.string_whitespace)
    }

    if (isAutonomousRegionalSupers) {
        superCategoriesFiltered.remove(element = AutonomousRegion)
        superCategoriesFiltered.remove(element = Regional)
        strings.add(R.string.categories_autonomous_regional)
        strings.add(R.string.string_whitespace)

    } else if (isAutonomousRegion) {
        superCategoriesFiltered.remove(element = AutonomousRegion)
        remainingCategories.remove(element = AUTONOMOUS_REGION)
        strings.add(R.string.categories_autonomous_title)
        strings.add(R.string.string_whitespace)
    }

    superCategoriesFiltered.forEach { superCategory ->
        val isRegionException = isHistorical || isCultural || isAutonomousRegion ||
                isDevolvedRegion || culturalCategories.isNotEmpty()

        when (superCategory to true) {
            Sovereign to isMicrostate -> null
            Sovereign to isAnnexed -> {
                strings.add(R.string.category_sovereign_state_button_title)
                strings.add(R.string.string_whitespace)
            }
            AutonomousRegion to isMicrostate -> null

            Regional to isUnrecognizedState -> {
                strings.add(TERRITORY.title)
            }
            Regional to isRegionException -> {
                strings.add(R.string.category_region_title)
                strings.add(R.string.string_whitespace)
            }
            else -> {
                superCategory.categoriesMenuButton?.let {
                    strings.add(it)
                    strings.add(R.string.string_whitespace)
                }
            }
        }
    }

    if (VEXILLOLOGY in remainingCategories && remainingCategories.size > 1) {
        remainingCategories.remove(element = VEXILLOLOGY)
        strings.add(R.string.category_vexillology_description_title)
        strings.add(R.string.string_whitespace)
    }

    remainingCategories.forEach { subCategory ->
        when (subCategory) {
            SOVEREIGN_STATE -> if (!isMicrostate) {
                strings.add(R.string.category_sovereign_state_button_title)
            }
            VEXILLOLOGY -> strings.add(R.string.category_vexillology_category_button_title)
            else -> strings.add(subCategory.title)
        }
        strings.add(R.string.string_whitespace)
    }

    if (strings.last() == R.string.string_comma_whitespace ||
        strings.last() == R.string.string_whitespace) {
        strings.removeLastOrNull()
    }
    if (isAppendFlags) strings.add(R.string.button_title_flags)

    return strings
}