package dev.aftly.flags.model

import androidx.annotation.StringRes

sealed interface RelatedFlagGroup {
    val category: Int

    data class Single(
        val flag: FlagView,
        @param:StringRes override val category: Int,
    ) : RelatedFlagGroup

    data class Multiple(
        val flags: List<FlagView>,
        @param:StringRes override val category: Int,
    ) : RelatedFlagGroup
}