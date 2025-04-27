package dev.aftly.flags.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FlagResources(
    val id: Int, // For LazyColumn's items() key param
    @DrawableRes val image: Int,
    @DrawableRes val imagePreview: Int, // Smaller image for lists (if relevant)
    @StringRes val flagOf: Int, // flagOf<> params are for common, official & alt names (of entity)
    @StringRes val flagOfOfficial: Int,
    @StringRes val flagOfAlternate: List<Int>?,
    val isFlagOfThe: Boolean, // <>The params are for if name is preceded by "the"
    val isFlagOfOfficialThe: Boolean,
    val sovereignState: String?, // If applicable, flagsMap key string of it's sovereign entity
    val associatedState: String?, // Like above but for states in a Compact of Free Association
    val categories: List<FlagCategory>,
)