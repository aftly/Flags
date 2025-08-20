package dev.aftly.flags.model

import androidx.annotation.StringRes

sealed interface RelatedFlagGroup {
    val category: Int
    val categoryKey: String

    data class Single(
        val flag: FlagView,
        @param:StringRes override val category: Int,
        override val categoryKey: String,
    ) : RelatedFlagGroup

    data class Multiple(
        val flags: List<FlagView>,
        @param:StringRes override val category: Int,
        override val categoryKey: String,
    ) : RelatedFlagGroup

    data class AdminUnits(
        val flags: List<FlagView>,
        val unitLevel: Int,
        @param:StringRes override val category: Int,
        override val categoryKey: String,
    ) : RelatedFlagGroup
}