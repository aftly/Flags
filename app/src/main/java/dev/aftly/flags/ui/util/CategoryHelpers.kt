package dev.aftly.flags.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.historicalSubCategoryWhitelist
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSubCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.data.DataSource.supersExclusiveOfInstitution
import dev.aftly.flags.data.DataSource.supersExclusiveOfPolitical
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.IdeologicalOrientation
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Political
import dev.aftly.flags.model.FlagSuperCategory.PowerDerivation
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagSuperCategory.TerritorialDistributionOfAuthority
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
    val categoriesNot = when (subCategory) {
        NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(CONSTITUTIONAL, HISTORICAL)
        else -> emptyList()
    }

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
    for (flag in allFlags) {
        if (parentCategory in DataSource.historicalSuperCategoryExceptions &&
            subCategory !in historicalSubCategoryWhitelist && HISTORICAL in flag.categories) {
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
    flags: List<FlagView>,
): List<FlagSuperCategory> {
    val isPoliticalSubCategory = subCategory in Political.supersEnums()

    return if (superCategory != null) {
        listOf(superCategory)
    } else if (isPoliticalSubCategory &&
        flags.none { INTERNATIONAL_ORGANIZATION in it.categories }) {
        listOf(SovereignCountry)
    } else {
        emptyList()
    }
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
    val polSubsExclOfInternational =
        listOf(TerritorialDistributionOfAuthority, PowerDerivation, IdeologicalOrientation)
            .flatMap { it.enums() }
            .filterNot { it == CONFEDERATION }

    return if (superCategories.isEmpty() && subCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (subCategories.any { it in superCategory.enums() }) {
        true

    } else if (superCategory !in superCategories &&
        superCategory in exclusive1Supers &&
        (exclusive1Supers.any { it in superCategories } ||
        exclusive1Subs.any { it in subCategories })) {
        true

    } else if (superCategory !in superCategories &&
        superCategory in exclusive2Supers &&
        (exclusive2Supers.any { it in superCategories } ||
        exclusive2Subs.any { it in subCategories })) {
        true

    } else if (superCategory !in superCategories &&
        superCategory == Institution &&
        (superCategories.any { it in instExclusiveSupers } ||
        subCategories.any { it in instExclusiveSubs })) {
        true

    } else if (superCategory !in superCategories &&
        superCategory in instExclusiveSupers &&
        Institution in superCategories) {
        true

    } else if (superCategory !in superCategories &&
        superCategory in polExclusiveSupers &&
        subCategories.any { it in polSubs }) {
        true

    } else if (superCategory !in superCategories &&
        superCategory == International &&
        subCategories.any { it in polSubsExclOfInternational }) {
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
    val exclusiveSubsInSupers = mutuallyExclusiveSubCategories
    val subCategorySuper = exclusiveSubsInSupers.find { subCategory in it.enums() }
    val exclusive1Supers = mutuallyExclusiveSuperCategories1
    val exclusive1Subs = exclusive1Supers.flatMap { it.enums() }
    val exclusive2Supers = mutuallyExclusiveSuperCategories2
    val exclusive2Subs = exclusive2Supers.flatMap { it.enums() }
    val instExclusiveSupers = supersExclusiveOfInstitution
    val instExclusiveSubs = instExclusiveSupers.flatMap { it.enums() }
    val polExclusiveSupers = supersExclusiveOfPolitical
    val polSubs = Political.supersEnums()
    val polSubsExclOfInternational =
        listOf(TerritorialDistributionOfAuthority, PowerDerivation, IdeologicalOrientation)
            .flatMap { it.enums() }
            .filterNot { it == CONFEDERATION }

    return if (subCategories.isEmpty() && superCategories.isEmpty()) {
        true /* SavedFlags (currently) represented by no selected categories */

    } else if (subCategory !in subCategories &&
        subCategorySuper != null &&
        subCategory in subCategorySuper.enums() &&
        subCategories.any { it in subCategorySuper.enums() }) {
        true

    } else if (subCategory !in subCategories &&
        subCategory in exclusive1Subs &&
        (superCategories.any { it in exclusive1Supers } ||
        subCategories.any { it in exclusive1Subs })) {
        true

    } else if (subCategory !in subCategories &&
        subCategory in exclusive1Subs &&
        (superCategories.any { it in exclusive2Supers } ||
        subCategories.any { it in exclusive2Subs })) {
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
    if (superCategories.isNotEmpty() && superCategory in superCategories) {
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
): Boolean {
    if (subCategories.isNotEmpty() && subCategory in subCategories) {
        subCategories.remove(subCategory)
        return true

    } else {
        subCategories.add(subCategory)
        return false
    }
}


fun getFlagsFromCategories(
    allFlags: List<FlagView>,
    currentFlags: List<FlagView>,
    isDeselect: Boolean,
    newSuperCategory: FlagSuperCategory?,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): List<FlagView> {
    return if (isDeselect || newSuperCategory == Historical) {
        allFlags
    } else {
        currentFlags
    }.let { flags ->
        when (subCategories.isNotEmpty()) {
            true -> flags.filter { it.categories.containsAll(elements = subCategories) }
            false -> flags
        }
    }.let { flags ->
        when (superCategories.isNotEmpty()) {
            true ->
                flags.filter { flag ->
                    superCategories.all { superCategory ->
                        flag.categories.any { it in superCategory.enums() }
                    }
                }
            false -> flags
        }
    }
}