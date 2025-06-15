package dev.aftly.flags.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
sealed interface FlagCategoryType