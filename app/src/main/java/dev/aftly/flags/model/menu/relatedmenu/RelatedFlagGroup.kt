package dev.aftly.flags.model.menu.relatedmenu

import androidx.annotation.StringRes
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagView

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
        val unitCategory: FlagCategory,
        @param:StringRes override val category: Int,
        override val categoryKey: String,
    ) : RelatedFlagGroup
}