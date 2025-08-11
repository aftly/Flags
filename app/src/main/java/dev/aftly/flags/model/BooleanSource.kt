package dev.aftly.flags.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* For use in DataSource to handle compile time enforcement for downstream usage of the data */
@Serializable
sealed interface BooleanSource {
    @Serializable
    @SerialName("BooleanSource.Explicit")
    data class Explicit(val bool: Boolean) : BooleanSource

    @Serializable
    @SerialName("BooleanSource.Inherit")
    object Inherit : BooleanSource
}