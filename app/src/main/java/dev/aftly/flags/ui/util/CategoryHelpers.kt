package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.absenceCategoriesMap
import dev.aftly.flags.data.DataSource.absenceCategoriesAddAnyMap
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSubsSuperCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.data.DataSource.subsExclusiveOfCountry
import dev.aftly.flags.data.DataSource.supersExclusiveOfInternational
import dev.aftly.flags.data.DataSource.supersExclusiveOfInstitution
import dev.aftly.flags.data.DataSource.supersExclusiveOfPolitical
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.DEVOLVED_GOVERNMENT
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
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
import dev.aftly.flags.model.RelatedFlagsMenu
import dev.aftly.flags.model.RelatedFlagsMenu.CHRONOLOGICAL
import dev.aftly.flags.model.RelatedFlagsMenu.POLITICAL


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

fun getFlagsByCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    allFlags: List<FlagView>,
    parentCategory: FlagSuperCategory = getParentSuperCategory(
        superCategory = superCategory,
        subCategory = subCategory,
    ),
    exceptionCategories: List<FlagCategory> = SovereignCountry.enums(),
): List<FlagView> {
    /* Mutable list for adding flags to */
    val flags = mutableListOf<FlagView>()

    /* Exclude flags if they have a particular category/categories */
    val categoriesNot = absenceCategoriesMap[subCategory] ?: emptyList()

    /* Exclude flags if they don't have any categories in this list */
    val categoriesHasAny = when (subCategory) {
        in Political.supersEnums() -> listOf(SOVEREIGN_STATE, INTERNATIONAL_ORGANIZATION)
        else -> null
    }

    /* Generate list of categories to iterate over (for flags list) from nullable values */
    val categories = if (subCategory != null) {
        when (subCategory) {
            NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(SOVEREIGN_STATE)
            THEOCRATIC -> listOf(subCategory, THEOCRACY)
            else -> listOf(subCategory)
        }
    } else superCategory?.enums() ?: exceptionCategories


    /* Search for flags that contain any category(s) from categories and add to list,
     * unless it's superCategory has historical exception */
    //val newFlags = allFlags.filter { flag -> }

    for (flag in allFlags) {
        /* Skip historical flags when super is exception and subcategory not in whitelist */
        if (parentCategory in DataSource.historicalSuperCategoryExceptions &&
            subCategory !in historicalSubCategoryWhitelist &&
            HISTORICAL in flag.categories) {
            continue
        }
        for (category in categories) {
            if (category in flag.categories) {
                if (flag !in flags) {
                    /* Add flag if none of it's categories are in categoriesNot and has any of
                    * categoriesHasAny */
                    if (categoriesNot.none { it in flag.categories }) {
                        categoriesHasAny?.let {
                            if (categoriesHasAny.any { it in flag.categories }) {
                                flags.add(flag)
                            }
                        } ?: flags.add(flag)
                    }
                }
                break
            }
        }
    }
    return flags.toList()
}


/* Handle special cases like when subcategory is Political, return SovereignCountry super when
 * flags doesn't contain INTERNATIONAL_ORGANIZATION flags */
fun getSuperCategories(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
): List<FlagSuperCategory> = when (superCategory) {
    null -> emptyList()
    else -> listOf(superCategory)
}


fun getParentSuperCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    exceptionCategory: FlagSuperCategory = SovereignCountry,
): FlagSuperCategory {
    /* If subCategory not null get it's superCategory, else if superCategory not null return it,
    * else return exception superCategory  */
    return if (subCategory != null) {
        menuSuperCategoryList.filterNot { it in listOf(All, Political) }
            .find { subCategory in it.enums() } ?: Political
    } else {
        superCategory ?: exceptionCategory
    }
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

    } else if (subCategories.any { it in superCategory.enums() }) {
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


fun updateCategoriesFromSuper(
    superCategory: FlagSuperCategory,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): Boolean {
    var isRemove = false

    /* Handle removal from superCategories */
    if (superCategory in superCategories) {
        superCategories.remove(superCategory)
        isRemove = true
    }
    /* Handle removal from subCategories (not mutually exclusive from above) */
    if (subCategories.isNotEmpty() && superCategory.subCategories.size == 1 &&
        superCategory.firstCategoryEnumOrNull() in subCategories) {
        subCategories.remove(superCategory.firstCategoryEnumOrNull())
        isRemove = true
    }
    /* If either of previous are true return true */
    if (isRemove) return true

    superCategories.add(superCategory)
    return false
}


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
    return if (isDeselectSwitch.first || isDeselectSwitch.second || superCategory == Historical) {
        allFlags /* Only when required */
    } else {
        currentFlags
    }.let { flags ->
        /* Filter by supercategory */
        if (superCategories.isNotEmpty()) flags.filter { flag ->
            superCategories.all { superCategory ->
                flag.categories.any { it in superCategory.enums() }
            }
        } else flags
    }.let { flags ->
        /* Filter by subcategory */
        if (subCategories.isNotEmpty()) {
            flags.filter { flag ->
                /* Handle nonAbsenceCategories (eg. Nominal/extra constitutional) */
                val absenceKeyValues = absenceCategoriesMap.filterKeys { it in subCategories }
                val nonAbsenceCategories = subCategories.filterNot { it in absenceKeyValues.keys }
                val absenceAddAny = absenceCategoriesAddAnyMap.filterKeys { it in subCategories }

                flag.categories.containsAll(elements = nonAbsenceCategories) &&
                        /* Empty absence values return true due to nature of none() and all() */
                        flag.categories.none { it in absenceKeyValues.values.flatten() } &&
                        flag.categories.any { cat -> absenceAddAny.all { cat in it.value } }
            }
        } else flags
    }.let { flags ->
        /* Handle unintended isDelect and isSwitch effects */
        if ((isDeselectSwitch.first || isDeselectSwitch.second) &&
            superCategories.none { it in listOf(All, Historical, Cultural) } &&
            subCategories.none { it in Cultural.enums() + historicalSubCategoryWhitelist }) {
            flags.filterNot { HISTORICAL in it.categories }

        } else flags
    }
}