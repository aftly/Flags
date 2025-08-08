package dev.aftly.flags.model

/* For use in DataSource to handle compile time enforcement for downstream usage of the data */
sealed interface BooleanSource {
    data class Explicit(val bool: Boolean) : BooleanSource
    object Inherit : BooleanSource
}