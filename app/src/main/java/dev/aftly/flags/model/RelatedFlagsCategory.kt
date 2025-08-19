package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R

enum class RelatedFlagsCategory(
    @param:StringRes val title: Int
) {
    LATEST_ENTITY (title = R.string.menu_related_flags_chronological_latest),
    PREVIOUS_ENTITIES (title = R.string.menu_related_flags_chronological_previous),
    HISTORICAL_FLAGS (title = R.string.menu_related_flags_chronological_historical),
    PREVIOUS_ENTITIES_OF_SOVEREIGN (title = R.string.menu_related_flags_chronological_previous_of_sovereign),
    DEPENDENTS_OF_LATEST_ENTITY (title = R.string.menu_related_flags_chronological_dependents_of_latest)
}