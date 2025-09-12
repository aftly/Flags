package dev.aftly.flags.model

sealed interface SearchFlow {
    data class Str(val value: String) : SearchFlow
    data class FlagsList(val value: List<FlagView>) : SearchFlow
    data class Bool(val value: Boolean) : SearchFlow
}