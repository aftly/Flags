package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.absenceCategoriesAddAnyMap
import dev.aftly.flags.data.DataSource.absenceCategoriesMap
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.switchSubsSuperCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.data.DataSource.subsExclusiveOfCountry
import dev.aftly.flags.data.DataSource.supersExclusiveOfCultural
import dev.aftly.flags.data.DataSource.supersExclusiveOfInstitution
import dev.aftly.flags.data.DataSource.supersExclusiveOfInternational
import dev.aftly.flags.data.DataSource.supersExclusiveOfPolitical
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
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
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
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagSuperCategory.TerritorialDistributionOfAuthority
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu.CHRONOLOGICAL
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu.POLITICAL
import kotlin.collections.plus


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
fun RelatedFlagsMenu.color(): Color = when (this) {
    CHRONOLOGICAL -> MaterialTheme.colorScheme.tertiary
    POLITICAL -> MaterialTheme.colorScheme.secondary
}

@Composable
fun RelatedFlagsMenu.colorSelect(): Color = when (this) {
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
    }

    /* For skipping historical flags when category in exception */
    val parentCategory = getParentSuperCategory(category)
    val isHistoricalException = parentCategory in DataSource.historicalSuperCategoryExceptions &&
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
}


/* ------ For updateCurrentCategories() in ViewModels ------ */

fun isSuperCategoryExit(
    superCategory: FlagSuperCategory,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Boolean {
    val exclusive1Supers = mutuallyExclusiveSuperCategories1
    val exclusive1Subs = exclusive1Supers.flatMap { it.enums() }
    val exclusive2Supers = mutuallyExclusiveSuperCategories2
    val exclusive2Subs = exclusive2Supers.flatMap { it.enums() }
    val intExclusiveSupers = supersExclusiveOfInternational
    val intExclusiveSubs = intExclusiveSupers.flatMap { it.enums() }
        .filterNot { it == CONFEDERATION }
    val instExclusiveSupers = supersExclusiveOfInstitution
    val instExclusiveSubs = instExclusiveSupers.flatMap { it.enums() }
    val cultExclusiveSupers = supersExclusiveOfCultural
    val cultExclusiveSubs = cultExclusiveSupers.flatMap { it.enums() }
    val cultSubs = Cultural.enums()
    val polExclusiveSupers = supersExclusiveOfPolitical
    val polSubs = Political.allEnums()
    val countryExclusiveSubs = subsExclusiveOfCountry

    return if (superCategories.isEmpty() && subCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (superCategory in superCategories) {
        false /* Escape function if supercategory already selected (so it can be deselected) */

    } else if (superCategory != All &&
        subCategories.any { it in superCategory.enums() }) {
        true

    } else if (superCategory in exclusive1Supers &&
        (exclusive1Supers.any { it in superCategories } ||
        exclusive1Subs.any { it in subCategories })) {
        true

    } else if (superCategory in exclusive2Supers &&
        (exclusive2Supers.any { it in superCategories } ||
        exclusive2Subs.any { it in subCategories })) {
        true

    } else if (superCategory in Institution.allSupers() &&
        (superCategories.any { it in instExclusiveSupers } ||
        subCategories.any { it in instExclusiveSubs })) {
        true

    } else if (superCategory in instExclusiveSupers &&
        Institution.allSupers().any { it in superCategories }) {
        true

    } else if (superCategory in Institution.supers() &&
        Institution.allEnums().any { it in subCategories }) {
        true /* Make Institution supers mutually exclusive of each other's subs */

    } else if (superCategory in polExclusiveSupers &&
        subCategories.any { it in polSubs }) {
        true

    } else if (superCategory == International &&
        (superCategories.any { it in intExclusiveSupers } ||
        subCategories.any { it in intExclusiveSubs })) {
        true

    } else if (superCategory in intExclusiveSupers &&
        International in superCategories) {
        true

    } else if (superCategory == Cultural &&
        (superCategories.any { it in cultExclusiveSupers } ||
        subCategories.any { it in cultExclusiveSubs })) {
        true

    } else if (superCategory in cultExclusiveSupers &&
        (Cultural in superCategories ||
        cultSubs.any { it in subCategories })) {
        true

    } else if (superCategory == SovereignCountry &&
        subCategories.any { it in countryExclusiveSubs }) {
        true

    } else {
        false
    }
}

fun isSubCategoryExit(
    subCategory: FlagCategory,
    subCategories: MutableList<FlagCategory>,
    superCategories: MutableList<FlagSuperCategory>,
): Boolean {
    val exclusive1Supers = mutuallyExclusiveSuperCategories1.filterNot { subCategory in it.enums() }
    val exclusive1Subs = mutuallyExclusiveSuperCategories1.flatMap { it.enums() }
    val exclusive1SubsSansSuper = exclusive1Supers.flatMap { it.enums() }
    val exclusive2Supers = mutuallyExclusiveSuperCategories2.filterNot { subCategory in it.enums() }
    val exclusive2Subs = mutuallyExclusiveSuperCategories2.flatMap { it.enums() }
    val exclusive2SubsSansSuper = exclusive2Supers.flatMap { it.enums() }
    val intExclusiveSubs = supersExclusiveOfInternational.flatMap { it.enums() }
        .filterNot { it == CONFEDERATION }
    val instExclusiveSupers = supersExclusiveOfInstitution
    val instExclusiveSubs = supersExclusiveOfInstitution.flatMap { it.enums() }
    val cultExclusiveSupers = supersExclusiveOfCultural
    val cultExclusiveSubs = cultExclusiveSupers.flatMap { it.enums() }
    val cultSubs = Cultural.enums()
    val polExclusiveSupers = supersExclusiveOfPolitical
    val polExclusiveSubs = polExclusiveSupers.flatMap { it.enums() } +
            AUTONOMOUS_REGION + DEVOLVED_GOVERNMENT
    val polSubs = Political.allEnums()
    val countryExclusiveSubs = subsExclusiveOfCountry

    return if (subCategories.isEmpty() && superCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (subCategory in subCategories) {
        false /* Escape function if subcategory already selected (so it can be deselected) */

    } else {
        isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = exclusive1Subs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = exclusive1Supers,
            selectedSubCategories = subCategories,
            selectedSubsExclusives = exclusive1SubsSansSuper,

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = exclusive2Subs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = exclusive2Supers,
            selectedSubCategories = subCategories,
            selectedSubsExclusives = exclusive2SubsSansSuper,

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = intExclusiveSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = listOf(International),

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = instExclusiveSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = Institution.allSupers(),
            selectedSubCategories = subCategories,
            selectedSubsExclusives = Institution.allEnums(),

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = Institution.allEnums(),
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = instExclusiveSupers,
            selectedSupersExclusives2 =
                Institution.supers().filterNot { subCategory in it.enums() },
            selectedSubCategories = subCategories,
            selectedSubsExclusives = instExclusiveSubs,
            selectedSubsExclusives2 = Institution.allEnums().filterNot { enum ->
                Institution.supers().find { subCategory in it.enums() }
                    ?.enums()?.contains(enum) == true
            },

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = cultSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = cultExclusiveSupers,
            selectedSubCategories = subCategories,
            selectedSubsExclusives = cultExclusiveSubs,

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = cultExclusiveSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = listOf(Cultural),
            selectedSubCategories = subCategories,
            selectedSubsExclusives = cultSubs,

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = countryExclusiveSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = listOf(SovereignCountry),

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = polSubs,
            selectedSuperCategories = superCategories,
            selectedSupersExclusives = polExclusiveSupers,
            selectedSubCategories = subCategories,
            selectedSubsExclusives = polExclusiveSubs,

        ) || isSubExclusive(
            subCategory = subCategory,
            subCategoryExclusives = polExclusiveSubs,
            selectedSubCategories = subCategories,
            selectedSubsExclusives = polSubs,
        )
    }
}

private fun isSubExclusive(
    subCategory: FlagCategory, /* Eg. New selection */
    subCategoryExclusives: List<FlagCategory>, /* Eg. Exclusion group */
    selectedSuperCategories: List<FlagSuperCategory>? = null, /* Eg. Currently selected */
    selectedSupersExclusives: List<FlagSuperCategory>? = null, /* Eg. Exclusion group */
    selectedSupersExclusives2: List<FlagSuperCategory>? = null, /* Eg. Exclusion group */
    selectedSubCategories: List<FlagCategory>? = null, /* Eg. Currently selected */
    selectedSubsExclusives: List<FlagCategory>? = null, /* Eg. Exclusion group */
    selectedSubsExclusives2: List<FlagCategory>? = null, /* Eg. Exclusion group */
): Boolean {
    val isSub = subCategory in subCategoryExclusives

    val anySelected = buildList {
        add(selectedSupersExclusives?.any { selectedSuperCategories?.contains(it) == true })
        add(selectedSupersExclusives2?.any { selectedSuperCategories?.contains(it) == true })
        add(selectedSubsExclusives?.any { selectedSubCategories?.contains(it) == true })
        add(selectedSubsExclusives2?.any { selectedSubCategories?.contains(it) == true })
    }
    
    return isSub && anySelected.any { it == true }
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
        subCategories.remove(subCategory)
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
): List<FlagSuperCategory> = superCategories.filterNot { superCategory ->
    /* Filter parent supercategories or All super if other categories */
    subCategories.any { it in superCategory.enums() } &&
            (superCategory != All || (superCategories.size > 1 || subCategories.isNotEmpty()))
}

fun getCategoriesTitleIds(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    isAppendFlags: Boolean = true,
): List<Int> {
    /* ---------- Early escape --------- */
    if (superCategories.isEmpty() && subCategories.isEmpty()) {
        return buildList {
            add(R.string.saved_flags)
            if (isAppendFlags) add(R.string.button_title_flags)
        }
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

    val isInternational =
        International in superCategoriesFiltered &&
                (Institution in superCategoriesFiltered || CONFEDERATION in subCategories)

    /* For string exceptions when supers or subs mean associated countries */
    val isAssociatedCountry = SovereignCountry in superCategoriesFiltered &&
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
                    subCategories.any { it in Regional.enums() + AUTONOMOUS_REGION + DEVOLVED_GOVERNMENT })


    /* ------------------- Category groups ------------------- */
    val culturalCategories = subCategories.filter { it in Cultural.enums() }

    /* Political categories, sorted semantically, and exceptions applied */
    val polCats = subCategories.filter { it in Political.allEnums() }

    val isConfederationException = CONFEDERATION in subCategories && subCategories
        .none { it in PowerDerivation.enums() } && polCats.size > 1 || isInternational

    val politicalCategories = polCats.filterNot { it == CONFEDERATION && isConfederationException }
        .sortedBy { politicalCategoriesSortOrder.indexOf(it) }

    /* Subcategories not in other list derivatives, minus duplicates */
    val remainingCategories = subCategories.filterNot { it in culturalCategories }
        .filterNot { it in politicalCategories }.toMutableList()


    /* -------------------------------- GET ITERATIONS -------------------------------- */
    val strings = mutableListOf<Int>()

    if (isHistorical) {
        superCategoriesFiltered.remove(Historical)
        strings.add(Historical.title)
        strings.add(R.string.string_whitespace)
    }

    if (isCultural) {
        superCategoriesFiltered.remove(Cultural)
        strings.add(Cultural.title)
        if (superCategoriesFiltered.isNotEmpty() || politicalCategories.isNotEmpty() ||
            remainingCategories.isNotEmpty()) {
            strings.add(R.string.string_whitespace)
        }
    }

    culturalCategories.forEachIndexed { index, culturalCategory ->
        strings.add(culturalCategory.title)

        if (index != culturalCategories.lastIndex) strings.add(R.string.categories_and)
        else strings.add(R.string.string_whitespace)
    }

    politicalCategories.forEachIndexed { index, politicalCategory ->
        if (politicalCategory == CONFEDERATION && politicalCategories.size > 1) {
            strings.add(R.string.category_confederal_title)
        } else {
            strings.add(politicalCategory.title)
        }
        strings.add(R.string.string_whitespace)

        /* On last iteration, if not international, remove SovereignCountry super and
         * append "State" if not exception */
        if (index == politicalCategories.lastIndex && International !in superCategoriesFiltered) {
            superCategoriesFiltered.remove(SovereignCountry)

            if (politicalCategory !in PowerDerivation.enums()
                    .filterNot { it in listOf(ONE_PARTY, PROVISIONAL_GOVERNMENT) } &&
                politicalCategory != CONFEDERATION &&
                AutonomousRegion !in superCategories &&
                AutonomousRegion.enums().none { it in subCategories } &&
                !isConfederationException) {
                strings.add(R.string.category_state_title)
            }
        }
    }

    if (isInternational) {
        superCategoriesFiltered.remove(International)
        strings.add(International.title)
        strings.add(R.string.string_whitespace)
    }

    if (isAssociatedCountry) {
        superCategoriesFiltered.remove(SovereignCountry)
        superCategoriesFiltered.remove(AutonomousRegion)
        remainingCategories.remove(FREE_ASSOCIATION)
        strings.add(R.string.categories_associated_country)
        strings.add(R.string.string_whitespace)
    }

    if (isDevolvedRegion) {
        remainingCategories.remove(DEVOLVED_GOVERNMENT)
        strings.add(R.string.categories_devolved_title)
        strings.add(R.string.string_whitespace)
    }

    if (isAutonomousRegionalSupers) {
        superCategoriesFiltered.remove(AutonomousRegion)
        superCategoriesFiltered.remove(Regional)
        strings.add(R.string.categories_autonomous_regional)
        strings.add(R.string.string_whitespace)

    } else if (isAutonomousRegion) {
        superCategoriesFiltered.remove(AutonomousRegion)
        remainingCategories.remove(AUTONOMOUS_REGION)
        strings.add(R.string.categories_autonomous_title)
        strings.add(R.string.string_whitespace)
    }

    superCategoriesFiltered.forEachIndexed { index, superCategory ->
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

    remainingCategories.forEach { subCategory ->
        strings.add(subCategory.title)
        strings.add(R.string.string_whitespace)
    }

    if (strings.last() == R.string.string_comma_whitespace ||
        strings.last() == R.string.string_whitespace) {
        strings.removeLastOrNull()
    }
    if (isAppendFlags) strings.add(R.string.button_title_flags)

    return strings
}