package dev.aftly.flags.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class FlagResources(
    // TODO: Add ISO codes? (get from WikiData)
    val id: Int, // TODO: Remove now that LazyColumn items() uses list index as key
    @DrawableRes val image: Int,
    @DrawableRes val imagePreview: Int,
    @StringRes val flagOf: Int,
    @StringRes val flagOfOfficial: Int,
    @StringRes val flagOfAlternate: List<Int>?,
    //@StringRes val flagOfNativeLanguages: List<Int>,
    //@StringRes val flagName: Int?,
    val isFlagOfThe: Boolean,
    val isFlagOfOfficialThe: Boolean,
    val sovereignState: String?, // if applicable, references it's sovereign state's flagsMap key. Info about the sovereign is shown in parts of the app
    val associatedState: String?, // for states in a Compact of Free Association, references flagsMap key of Associated State
    val categories: List<FlagCategory>,
)