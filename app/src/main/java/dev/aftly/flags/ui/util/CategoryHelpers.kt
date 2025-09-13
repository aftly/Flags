package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.absenceCategoriesAddAnyMap
import dev.aftly.flags.data.DataSource.absenceCategoriesMap
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSubsSuperCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.data.DataSource.subsExclusiveOfCountry
import dev.aftly.flags.data.DataSource.supersExclusiveOfInstitution
import dev.aftly.flags.data.DataSource.supersExclusiveOfInternational
import dev.aftly.flags.data.DataSource.supersExclusiveOfPolitical
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Cultural
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu.CHRONOLOGICAL
import dev.aftly.flags.model.relatedmenu.RelatedFlagsMenu.POLITICAL


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
fun ScoreItem.isCategoriesEmpty(): Boolean =
    gameSuperCategories.isEmpty() && gameSubCategories.isEmpty()

fun ScoreItem.superCategories(): List<FlagSuperCategory> =
    gameSuperCategories.filterIsInstance<FlagSuperCategory>()

@Composable
fun RelatedFlagsMenu.color(): Color = when (this) {
    CHRONOLOGICAL -> MaterialTheme.colorScheme.tertiary
    POLITICAL -> MaterialTheme.colorScheme.secondary
}


/* ------ For updateCurrentCategory() in ViewModels ------ */

fun getFlagsFromCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    allFlags: List<FlagView>,
    exceptionCategories: List<FlagCategory> = SovereignCountry.enums(),
): List<FlagView> {
    /* Exclude flags if they have a particular category/categories */
    val categoriesNot = absenceCategoriesMap[subCategory] ?: emptyList()

    /* Generate list of categories to iterate over (for flags list) from params */
    val categories = when (subCategory) {
        null -> superCategory?.enums() ?: exceptionCategories
        NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(SOVEREIGN_STATE)
        THEOCRATIC -> listOf(subCategory, THEOCRACY)
        else -> listOf(subCategory)
    }

    /* For skipping historical flags when category in exception */
    val parentCategory = getParentSuperCategory(superCategory, subCategory)
    val isHistoricalException = parentCategory in DataSource.historicalSuperCategoryExceptions &&
            subCategory !in historicalSubCategoryWhitelist

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
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    exceptionCategory: FlagSuperCategory = SovereignCountry,
): FlagSuperCategory = when (subCategory) {
    null -> superCategory ?: exceptionCategory
    else -> menuSuperCategoryList.filterNot { it in listOf(All, Political) }
        .find { subCategory in it.enums() } ?: Political
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
    val instExclusiveSupers = supersExclusiveOfInstitution
    val instExclusiveSubs = instExclusiveSupers.flatMap { it.enums() }
    val polExclusiveSupers = supersExclusiveOfPolitical
    val polSubs = Political.supersEnums()
    val intExclusiveSupers = supersExclusiveOfInternational
    val intExclusiveSubs = intExclusiveSupers.flatMap { it.enums() }
        .filterNot { it == CONFEDERATION }
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

    } else if (superCategory == Institution &&
        (superCategories.any { it in instExclusiveSupers } ||
        subCategories.any { it in instExclusiveSubs })) {
        true

    } else if (superCategory in instExclusiveSupers &&
        Institution in superCategories) {
        true

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
    val instExclusiveSubs = supersExclusiveOfInstitution.flatMap { it.enums() }
    val polExclusiveSupers = supersExclusiveOfPolitical
    val polExclusiveSubs = polExclusiveSupers.flatMap { it.enums() } +
            AUTONOMOUS_REGION + DEVOLVED_GOVERNMENT
    val polSubs = Political.supersEnums()
    val intExclusiveSubs = supersExclusiveOfInternational.flatMap { it.enums() }
        .filterNot { it == CONFEDERATION }
    val countryExclusiveSubs = subsExclusiveOfCountry

    return if (subCategories.isEmpty() && superCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (subCategory in subCategories) {
        false /* Escape function if subcategory already selected (so it can be deselected) */

    } else if (subCategory in exclusive1Subs &&
        (superCategories.any { it in exclusive1Supers } ||
        subCategories.any { it in exclusive1SubsSansSuper })) {
        true

    } else if (subCategory in exclusive2Subs &&
        (superCategories.any { it in exclusive2Supers } ||
        subCategories.any { it in exclusive2SubsSansSuper })) {
        true

    } else if (subCategory in instExclusiveSubs &&
        Institution in superCategories) {
        true

    } else if (subCategory in polSubs &&
        (superCategories.any { it in polExclusiveSupers } ||
        subCategories.any { it in polExclusiveSubs })) {
        true

    } else if (subCategory in polExclusiveSubs &&
        subCategories.any { it in polSubs }) {
        true

    } else if (subCategory in intExclusiveSubs &&
        International in superCategories) {
        true

    } else if (subCategory in countryExclusiveSubs &&
        SovereignCountry in superCategories) {
        true

    } else {
        false
    }
}

/* Returns true if update deselects a category, else false */
fun updateCategoriesFromSuper(
    superCategory: FlagSuperCategory,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Boolean {
    return if (superCategory in superCategories) {
        /* Handle removal from superCategories */
        superCategories.remove(superCategory)
        true

    } else if (subCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
        superCategory.firstCategoryEnumOrNull() in subCategories) {
        /* Handle removal from subCategories (not mutually exclusive from above) */
        subCategories.remove(superCategory.firstCategoryEnumOrNull())
        true

    } else {
        superCategories.add(superCategory)
        false
    }
}

/* Returns bool pair, first if update deselects a category, second if update replaces a category */
fun updateCategoriesFromSub(
    subCategory: FlagCategory,
    subCategories: MutableList<FlagCategory>,
): Pair<Boolean, Boolean> {
    val isDeselect = subCategory in subCategories
    val isSwitchSuper = if (isDeselect) null else
        mutuallyExclusiveSubsSuperCategories.find { subCategory in it.enums() }

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
            flag.categories.any { it in superCategory.enums() }
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