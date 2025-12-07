package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.absenceCategoriesAddAnyMap
import dev.aftly.flags.data.DataSource.absenceCategoriesMap
import dev.aftly.flags.data.DataSource.categoriesAutonomousRegion
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfAutonomousRegion
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfExecutive
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfInternational
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfLegislature
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfPolitical
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfRegional
import dev.aftly.flags.data.DataSource.categoriesExclusiveOfSovereign
import dev.aftly.flags.data.DataSource.categoriesExecutive
import dev.aftly.flags.data.DataSource.categoriesInclusiveOfMicrostate
import dev.aftly.flags.data.DataSource.categoriesInternational
import dev.aftly.flags.data.DataSource.categoriesLegislature
import dev.aftly.flags.data.DataSource.categoriesMicrostate
import dev.aftly.flags.data.DataSource.categoriesMutuallyExclusive
import dev.aftly.flags.data.DataSource.categoriesPolitical
import dev.aftly.flags.data.DataSource.categoriesRegional
import dev.aftly.flags.data.DataSource.categoriesSovereign
import dev.aftly.flags.data.DataSource.categoriesSovereignExceptionPairs
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.switchSubsSuperCategories
import dev.aftly.flags.data.DataSource.switchSupersSuperCategories
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.ONE_PARTY
import dev.aftly.flags.model.FlagCategory.PROVISIONAL_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_ENTITY
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
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

fun isSuperCategoryExit(
    superCategory: FlagSuperCategory,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Boolean {
    return if (superCategories.isEmpty() && subCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (superCategory in superCategories) {
        false /* Escape function if supercategory already selected (so it can be deselected) */

    } else if (superCategory != All &&
        subCategories.any { it in superCategory.enums() }) {
        true

    } else {
        isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesMutuallyExclusive,
            categories = categoriesMutuallyExclusive,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesSovereign,
            categories = categoriesExclusiveOfSovereign,
            pairExceptions = categoriesSovereignExceptionPairs,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesAutonomousRegion,
            categories = categoriesExclusiveOfAutonomousRegion,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesRegional,
            categories = categoriesExclusiveOfRegional,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesInternational,
            categories = categoriesExclusiveOfInternational,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesLegislature,
            categories = categoriesExclusiveOfLegislature,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesExecutive,
            categories = categoriesExclusiveOfExecutive,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = superCategory,
            exclusiveOf = categoriesPolitical,
            categories = categoriesExclusiveOfPolitical,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryNotInclusive(
            category = superCategory,
            inclusiveOf = categoriesMicrostate,
            categories = categoriesInclusiveOfMicrostate,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,
        )
    }
}

fun isSubCategoryExit(
    subCategory: FlagCategory,
    subCategories: MutableList<FlagCategory>,
    superCategories: MutableList<FlagSuperCategory>,
): Boolean {
    val subCategoryWrapper = subCategory.toWrapper()

    return if (subCategories.isEmpty() && superCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (subCategory in subCategories) {
        false /* Escape function if subcategory already selected (so it can be deselected) */

    } else {
        isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesMutuallyExclusive,
            categories = categoriesMutuallyExclusive,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesSovereign,
            categories = categoriesExclusiveOfSovereign,
            pairExceptions = categoriesSovereignExceptionPairs,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesAutonomousRegion,
            categories = categoriesExclusiveOfAutonomousRegion,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesRegional,
            categories = categoriesExclusiveOfRegional,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesInternational,
            categories = categoriesExclusiveOfInternational,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesLegislature,
            categories = categoriesExclusiveOfLegislature,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesExecutive,
            categories = categoriesExclusiveOfExecutive,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryExclusive(
            category = subCategoryWrapper,
            exclusiveOf = categoriesPolitical,
            categories = categoriesExclusiveOfPolitical,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        ) || isCategoryNotInclusive(
            category = subCategoryWrapper,
            inclusiveOf = categoriesMicrostate,
            categories = categoriesInclusiveOfMicrostate,
            selectedSuperCategories = superCategories,
            selectedSubCategories = subCategories,

        )
    }
}

private fun isCategoryExclusive(
    category: FlagCategoryBase, /* New selection */
    exclusiveOf: List<FlagCategoryBase>,
    categories: List<FlagCategoryBase>, /* All categories exclusive of any exclusiveOf category */
    pairExceptions: List<Pair<FlagCategoryBase, FlagCategoryBase>> = emptyList(),
    selectedSuperCategories: List<FlagSuperCategory>, /* Currently selected */
    selectedSubCategories: List<FlagCategory>, /* Currently selected */
): Boolean {
    /* flatten selected categories for simpler parsing */
    val flatSelectedCategories = buildList {
        addAll(elements = selectedSuperCategories)
        addAll(elements = selectedSubCategories.map { it.toWrapper() })
    }

    val isCategoryExclusiveOf = category in exclusiveOf
    val isCategoryRelevant = category in categories
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

    /* Enforce switch exceptions when exclusiveOf and categories are the same */
    val isSwitch = when {
        exclusiveOf == categories && category is FlagCategoryWrapper -> {
            val categorySwitchSuper = switchSubsSuperCategories.find { category.enum in it.enums() }
            val categorySwitchSubs = categorySwitchSuper?.enums()

            categorySwitchSubs?.any { it in selectedSubCategories } == true &&
                    category.enum in categorySwitchSubs
        }
        else -> false
    }

    return when {
        isCategoryExclusiveOf && isAnyCategoriesSelected && !isPairException && !isSwitch -> true
        isCategoryRelevant && isAnyExclusiveOfSelected && !isPairException && !isSwitch  -> true
        else -> false
    }
}

private fun isCategoryNotInclusive(
    category: FlagCategoryBase, /* New selection */
    inclusiveOf: List<FlagCategoryBase>,
    categories: List<FlagCategoryBase>, /* All categories inclusive of any inclusiveOf category */
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

    return when {
        isCategoryInclusiveOf && !isOnlyInclusiveCategoriesSelected -> true
        isAnyInclusiveOfSelected && !isCategoryRelevant -> true
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
        superCategories.remove(superCategory)
        true to false // isDeselect

    } else if (subCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
        superCategory.firstCategoryEnumOrNull() in subCategories) {
        /* Handle removal from subCategories (not mutually exclusive from above) */
        subCategories.remove(superCategory.firstCategoryEnumOrNull())
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

    val isSovereignState = SOVEREIGN_STATE in subCategories
    val isSovereignEntity = SOVEREIGN_ENTITY in subCategories

    /* For string exceptions when supers or subs mean associated countries */
    val isAssociatedCountry = (Sovereign in superCategoriesFiltered || isSovereignState) &&
            (AutonomousRegion in superCategoriesFiltered || FREE_ASSOCIATION in subCategories)

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
    val culturalCategories = subCategories.filter { it in Cultural.enums() }

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

    if (isCultural) {
        superCategoriesFiltered.remove(element = Cultural)
        strings.add(Cultural.title)
        if (superCategoriesFiltered.isNotEmpty() || politicalCategories.isNotEmpty() ||
            remainingCategories.isNotEmpty()) {
            strings.add(R.string.string_whitespace)
        }
    }

    culturalCategories.forEachIndexed { index, culturalCategory ->
        strings.add(culturalCategory.title)

        if (index != culturalCategories.lastIndex) strings.add(R.string.string_ampersand)
        else strings.add(R.string.string_whitespace)
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

    if (isAssociatedCountry) {
        superCategoriesFiltered.remove(element = Sovereign)
        superCategoriesFiltered.remove(element = AutonomousRegion)
        remainingCategories.remove(element = SOVEREIGN_STATE)
        remainingCategories.remove(element = FREE_ASSOCIATION)
        strings.add(R.string.categories_associated_country)
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
        if (superCategory == Regional &&
            (isHistorical || isCultural || isAutonomousRegion ||
            isDevolvedRegion || culturalCategories.isNotEmpty())) {
            strings.add(R.string.category_region_title)
            strings.add(R.string.string_whitespace)
        } else {
            superCategory.categoriesMenuButton?.let { strings.add(it) }
            strings.add(R.string.string_whitespace)
        }
    }

    if (VEXILLOLOGY in remainingCategories && remainingCategories.size > 1) {
        remainingCategories.remove(element = VEXILLOLOGY)
        strings.add(R.string.category_vexillology_description_title)
        strings.add(R.string.string_whitespace)
    }

    remainingCategories.forEach { subCategory ->
        if (subCategory == VEXILLOLOGY) {
            strings.add(R.string.category_vexillology_category_button_title)
        } else {
            strings.add(subCategory.title)
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