package dev.aftly.flags.model

import androidx.annotation.StringRes

interface LazyColumnItem {
    val key: String
    val keyTag: String
    val type: String

    data class Header(
        @param:StringRes val title: Int,
        override val keyTag: String,
    ) : LazyColumnItem {
        override val key = "header_$keyTag"
        override val type = "header"
    }

    data class Flag(
        val flag: FlagView,
        override val keyTag: String,
    ) : LazyColumnItem {
        override val key = "${flag.id}_$keyTag"
        override val type = "flag"
    }
}