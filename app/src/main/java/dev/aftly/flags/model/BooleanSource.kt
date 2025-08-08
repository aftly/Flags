package dev.aftly.flags.model

sealed interface BooleanSource {
    data class Explicit(val bool: Boolean) : BooleanSource
    object Inherit : BooleanSource
}