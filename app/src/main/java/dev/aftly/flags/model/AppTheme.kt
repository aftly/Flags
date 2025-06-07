package dev.aftly.flags.model

enum class AppTheme {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun from(name: String?): AppTheme =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}