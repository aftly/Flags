package dev.aftly.flags.model.relatedmenu

import androidx.annotation.StringRes
import dev.aftly.flags.R

enum class RelatedFlagsCategory(
    @param:StringRes val title: Int,
) {
    HISTORICAL_FLAGS (title = R.string.menu_related_flags_chronological_historical_flags),
    LATEST_ENTITIES_POLITIES (title = R.string.menu_related_flags_chronological_latest_polities),
    LATEST_ENTITIES_POLITY (title = R.string.menu_related_flags_chronological_latest_polity),
    LATEST_ENTITY_NON_POLITY (title = R.string.menu_related_flags_chronological_latest_non_polity),
    PREVIOUS_ENTITIES_POLITY (title = R.string.menu_related_flags_chronological_previous_polity),
    PREVIOUS_ENTITIES_NON_POLITY (title = R.string.menu_related_flags_chronological_previous_non_polity),
    HISTORICAL_UNITS (title = R.string.menu_related_flags_chronological_historical_units),
    HISTORICAL_UNIT_SELECTED (title = R.string.menu_related_flags_chronological_historical_unit_selected),
    PREVIOUS_ENTITIES_OF_SOVEREIGN (title = R.string.menu_related_flags_chronological_previous_of_sovereign),
    DEPENDENTS_OF_LATEST_ENTITY (title = R.string.menu_related_flags_chronological_dependents_of_latest)
}