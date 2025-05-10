package dev.aftly.flags.ui.util

import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.menuSuperCategoryList
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategory.CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.THEOCRACY
import dev.aftly.flags.model.FlagCategory.THEOCRATIC
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory


fun getFlagsByCategory(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    allFlags: List<FlagResources>,
    parentCategory: FlagSuperCategory = getParentSuperCategory(
        superCategory = superCategory,
        subCategory = subCategory,
    ),
    exceptionCategories: List<FlagCategory> = FlagSuperCategory.SovereignCountry.subCategories
        .filterIsInstance<FlagCategory>(),
): List<FlagResources> {
    /* Mutable list for adding flags to */
    val flags = mutableListOf<FlagResources>()

    /* Make a categoriesNot list to exclude flags if they have a particular category/categories */
    val categoriesNot = when (subCategory) {
        NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(CONSTITUTIONAL, HISTORICAL)
        else -> null
    }

    /* Generate list of categories to iterate over (for flags list) from nullable values */
    val categories = if (subCategory != null) {
        when (subCategory) {
            NOMINAL_EXTRA_CONSTITUTIONAL -> listOf(SOVEREIGN_STATE)
            THEOCRATIC -> listOf(subCategory, THEOCRACY)
            else -> listOf(subCategory)
        }
    } else superCategory?.subCategories ?: exceptionCategories


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
                    /* Exclude flag if has category in categoriesNot, else add to list */
                    if (categoriesNot != null) {
                        if (categoriesNot.none { it in flag.categories }) {
                            flags.add(flag)
                        }
                    } else {
                        flags.add(flag)
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
    exceptionCategory: FlagSuperCategory = FlagSuperCategory.SovereignCountry,
): FlagSuperCategory {
    /* If subCategory not null get it's superCategory, else if superCategory not null return it,
    * else return exception superCategory  */
    return if (subCategory != null) {
        menuSuperCategoryList.filterNot {
            it in listOf(FlagSuperCategory.All, FlagSuperCategory.Political)
        }.find { item ->
            subCategory in item.subCategories
        } ?: FlagSuperCategory.Political
    } else {
        superCategory ?: exceptionCategory
    }
}


fun getCategoryTitle(
    superCategory: FlagSuperCategory?,
    subCategory: FlagCategory?,
    @StringRes titleException: Int = FlagSuperCategory.SovereignCountry.title,
): Int {
    return subCategory?.title ?: superCategory?.title ?: titleException
}