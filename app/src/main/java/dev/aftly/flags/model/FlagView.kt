package dev.aftly.flags.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/* Launch process class from flagsMap FlagResources for complete information for view and search */
data class FlagView(
    //val flagsMapKey: String, /* For flagsMap and <>RelatedFlags properties lookups */
    val id: Int,
    @param:StringRes val wikipediaUrlPath: Int,
    @param:DrawableRes val image: Int,
    @param:DrawableRes val imagePreview: Int,
    val fromYear: Int?,
    val toYear: Int?,
    @param:StringRes val flagOf: Int,
    @param:StringRes val flagOfOfficial: Int,
    val flagOfAlternate: List<Int>?,
    val isFlagOfThe: Boolean,
    val isFlagOfOfficialThe: Boolean,
    val isPreviousFlag: Boolean,
    val associatedStateKey: String?,
    val sovereignStateKey: String?,
    val flagStringResIds: List<Int>, /* All stringResIds of this flag for search exact match */
    val externalRelatedFlagKeys: List<String>, /* flagsMap keys search results and lists */
    val internalRelatedFlagKeys: List<String>, /* flagsMap keys for search results and lists */
    val previousAdminsOfSovereignKeys: List<String>, /* flagsMap keys for search and lists */
    val categories: List<FlagCategory>,
)