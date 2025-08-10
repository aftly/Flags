package dev.aftly.flags.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* For use in DataSource to handle compile time enforcement for downstream usage of the data */
@Serializable
sealed interface StringResSource {
    @Serializable
    @SerialName("StringRes.Explicit")
    data class Explicit(val resName: StringResName) : StringResSource

    @Serializable
    @SerialName("StringRes.Inherit")
    object Inherit : StringResSource
}