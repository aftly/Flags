package dev.aftly.flags.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("flag_category")
data class FlagCategoryWrapper(val enum: FlagCategory) : FlagCategoryBase()