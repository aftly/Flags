package dev.aftly.flags.ui.util

import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSubCategories
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories1
import dev.aftly.flags.data.DataSource.mutuallyExclusiveSuperCategories2
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagSuperCategory.AutonomousRegion
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Cultural
import dev.aftly.flags.model.FlagSuperCategory.Historical
import dev.aftly.flags.model.FlagSuperCategory.Political



/* ------ For updateCurrentCategory() in ViewModels ------ */

fun getFlagsByCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    allFlags: List<FlagResources>,
    parentCategory: FlagSuperCategory = getParentSuperCategory(
        superCategory = superCategory,
        subCategory = subCategory,
    ),
    exceptionCategories: List<FlagCategory> = SovereignCountry.enums(),
): List<FlagResources> {
    /* Mutable list for adding flags to */
    val flags = mutableListOf<FlagResources>()

    /* Exclude flags if they have a particular category/categories */
    val categoriesNot = when (subCategory) {
        NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(CONSTITUTIONAL, HISTORICAL)
        else -> emptyList()
    }

    /* Exclude flags if they don't have any categories in this list */
    val categoriesHasAny = when (Political.subCategories.filterIsInstance<FlagSuperCategory>()
        .any { subCategory in it.enums() }) {
        true -> listOf(SOVEREIGN_STATE, INTERNATIONAL_ORGANIZATION)
        false -> null
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


fun getParentSuperCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    exceptionCategory: FlagSuperCategory = SovereignCountry,
): FlagSuperCategory {
    /* If subCategory not null get it's superCategory, else if superCategory not null return it,
    * else return exception superCategory  */
    return if (subCategory != null) {
        menuSuperCategoryList.filterNot { it in listOf(All, Political) }.find { superCat ->
            subCategory in superCat.enums()
        } ?: Political
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
    val exclusive1 = mutuallyExclusiveSuperCategories1
    val exclusive2 = mutuallyExclusiveSuperCategories2

    /* TODO: Make hardcoded solutions (#2 & #4) dynamic */
    /* First 2 conditionals regard exclusive1, subsequent 2 regard exclusive2 */
    return if (superCategory !in superCategories &&
        exclusive1.contains(superCategory) && exclusive1.any { it in superCategories }) {
        true

    } else if (subCategories.any { subCategory ->
        subCategory in AutonomousRegion.enums() || subCategory in Regional.enums() } &&
        (superCategory == SovereignCountry || superCategory == International)) {
        true

    } else if (superCategory !in superCategories &&
        exclusive2.contains(superCategory) && exclusive2.any { it in superCategories }) {
        true

    } else if (subCategories.any { it in Cultural.enums() } &&
        (superCategory == SovereignCountry || superCategory == International)) {
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
    mutuallyExclusiveSubCategories.forEach { superCategory ->
        if (subCategory !in subCategories &&
            superCategory.enums().any { it in subCategories } &&
            subCategory in superCategory.enums()) {
            return true
        }
    }
    mutuallyExclusiveSuperCategories1.let { exclusive1 ->
        val subCategorySuper = exclusive1.find { subCategory in it.enums() }

        if (subCategorySuper != null) {
            /* If Sovereign or International selected reject selection of subcategories
             * from Regional */
            val selectedExclusiveSupers = exclusive1.filter { it in superCategories }
                .filter { it in listOf(SovereignCountry, International) }

            val unselectedExclusiveSupers = exclusive1.filterNot { it in superCategories }
                .filter { it == Regional }

            if (selectedExclusiveSupers.isNotEmpty() &&
                subCategorySuper in unselectedExclusiveSupers) {
                return true
            }
        }
    }
    mutuallyExclusiveSuperCategories2.let { exclusive2 ->
        val subCategorySuper = exclusive2.find { subCategory in it.enums() }

        if (subCategorySuper != null) {
            val selectedExclusiveSupers = exclusive2.filter { it in superCategories }
            val unselectedExclusiveSupers = exclusive2.filterNot { it in superCategories }

            if (selectedExclusiveSupers.isNotEmpty() &&
                subCategorySuper in unselectedExclusiveSupers) {
                return true
            }
        }
    }
    return false
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
    allFlags: List<FlagResources>,
    currentFlags: List<FlagResources>,
    isDeselect: Boolean,
    newSuperCategory: FlagSuperCategory?,
    superCategories: MutableList<FlagSuperCategory>,
    subCategories: MutableList<FlagCategory>,
): List<FlagResources> {
    return if (isDeselect || newSuperCategory == Historical) {
        allFlags
    } else {
        currentFlags
    }.let { flags ->
        when (subCategories.isNotEmpty()) {
            true -> flags.filter { it.categories.containsAll(subCategories) }
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