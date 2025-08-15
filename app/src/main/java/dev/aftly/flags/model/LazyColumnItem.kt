package dev.aftly.flags.model

interface LazyColumnItem {
    val key: String
    val type: String

    data class Header(val title: String) : LazyColumnItem {
        override val key = "header_$title"
        override val type = "header"
    }

    data class Flag(val flag: FlagView) : LazyColumnItem {
        override val key = "${flag.id}"
        override val type = "flag"
    }
}