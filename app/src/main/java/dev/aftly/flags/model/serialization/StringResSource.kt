package dev.aftly.flags.model.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* For use in DataSource to handle compile time enforcement for downstream usage of the data */
@Serializable
sealed interface StringResSource {
    @Serializable
    @SerialName(value = "StringResSource.Explicit")
    data class Explicit(val resName: StringResName) : StringResSource

    @Serializable
    @SerialName(value = "StringResSource.Inherit")
    object Inherit : StringResSource
}