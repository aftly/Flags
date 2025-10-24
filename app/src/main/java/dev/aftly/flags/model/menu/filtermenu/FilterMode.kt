package dev.aftly.flags.model.filtermenu

import androidx.annotation.StringRes
import dev.aftly.flags.R

enum class FilterMode(@param:StringRes val title: Int) {
    CATEGORY (title = R.string.menu_filter_button_category),
    COUNTRY (title = R.string.menu_filter_button_country),
}