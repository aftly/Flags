package dev.aftly.flags.ui.util

import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory

/* Get single category (stringResId) title from single or multiple selected categories */
fun getCategoryTitleSingle(
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
): Int? {
    return if (superCategories.size == 1 && subCategories.isEmpty()) {
        superCategories.first().gameCategory

    } else if (subCategories.size == 1 && superCategories.isEmpty()) {
        subCategories.first().title

    } else {
        null
    }
}