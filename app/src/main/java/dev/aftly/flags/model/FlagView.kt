package dev.aftly.flags.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap

/* Launch process class from flagsMap FlagResources for complete information for view and search */
data class FlagView(
    val id: Int,
    @param:StringRes val wikipediaUrlPath: Int,
    @param:DrawableRes val image: Int,
    @param:DrawableRes val imagePreview: Int,
    val fromYear: Int?,
    val fromYearCirca: Boolean?,
    val toYear: Int?,
    val toYearCirca: Boolean?,
    @param:StringRes val flagOf: Int,
    @param:StringRes val flagOfDescriptor: Int?, // for annotating flagOf with additional info
    @param:StringRes val flagOfOfficial: Int,
    val flagOfAlternate: List<Int>,
    val isFlagOfThe: Boolean,
    val isFlagOfOfficialThe: Boolean,
    val internationalOrganisationKeys: List<String>,
    val associatedStateKey: String?,
    val sovereignStateKey: String?,
    val parentUnitKey: String?,
    val latestEntityKeys: List<String>,
    val previousFlagOfKey: String?,
    val flagStringResIds: List<Int>, /* All stringResIds of this flag for search exact match */
    val politicalInternalRelatedFlagKeys: List<String>, /* flagsMap keys search results and lists */
    val politicalExternalRelatedFlagKeys: List<String>, /* flagsMap keys search results and lists */
    val chronologicalDirectRelatedFlagKeys: List<String>, /* flagsMap keys for search results and lists */
    val chronologicalIndirectRelatedFlagKeys: List<String>, /* flagsMap keys for search and lists */
    val categories: List<FlagCategory>,
) {
    val isDated = fromYear != null || (toYear != null && toYear != 0)

    fun isPoliticalRelatedFlags(): Boolean {
        val primaryFlag = previousFlagOfKey?.let { flagViewMap.getValue(it) } ?: this
        val primaryKey = previousFlagOfKey ?: inverseFlagViewMap.getValue(this)

        return (primaryFlag.politicalInternalRelatedFlagKeys +
                primaryFlag.politicalExternalRelatedFlagKeys).any { it != primaryKey }
    }

    fun isChronologicalRelatedFlags(): Boolean {
        val primaryFlag = previousFlagOfKey?.let { flagViewMap.getValue(it) } ?: this
        val primaryKey = previousFlagOfKey ?: inverseFlagViewMap.getValue(this)

        return (primaryFlag.chronologicalDirectRelatedFlagKeys +
                primaryFlag.chronologicalIndirectRelatedFlagKeys).any { it != primaryKey }
    }
}