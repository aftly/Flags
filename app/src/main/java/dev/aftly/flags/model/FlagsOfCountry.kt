package dev.aftly.flags.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* Eg. For use in Game as a supercategory for ScoreItem */
@Serializable
@SerialName(value = "flags_of_country")
data class FlagsOfCountry(val countryKey: String) : FlagCategoryBase