package dev.aftly.flags.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aftly.flags.R

enum class RelatedFlagsMenu(
    @param:StringRes val title: Int,
    @param:StringRes val titleShort: Int,
    val icon: ImageVector
) {
    CHRONOLOGICAL (
        title = R.string.menu_related_flags_chronological,
        titleShort = R.string.menu_related_flags_chronological_short,
        icon = Icons.Default.History
    ),
    POLITICAL (
        title = R.string.menu_related_flags_political,
        titleShort = R.string.menu_related_flags_political_short,
        icon = Icons.Default.AccountBalance
    )
}