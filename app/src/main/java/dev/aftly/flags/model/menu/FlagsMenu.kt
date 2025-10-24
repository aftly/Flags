package dev.aftly.flags.model.menu

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aftly.flags.R

enum class FlagsMenu(
    @param:StringRes val title: Int,
    @param:StringRes val titleShort: Int,
    val icon: ImageVector,
) {
    FILTER (
        title = R.string.menu_filter_title,
        titleShort = R.string.menu_filter_title_short,
        icon = Icons.Default.FilterList,
    ),
    CHRONOLOGICAL (
        title = R.string.menu_related_flags_chronological,
        titleShort = R.string.menu_related_flags_chronological_short,
        icon = Icons.Default.History,
    ),
    POLITICAL (
        title = R.string.menu_related_flags_political,
        titleShort = R.string.menu_related_flags_political_short,
        icon = Icons.Default.AccountBalance,
    )
}