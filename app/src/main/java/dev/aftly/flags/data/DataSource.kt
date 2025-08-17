package dev.aftly.flags.data

import dev.aftly.flags.R
import dev.aftly.flags.model.BooleanSource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.StringResSource
import dev.aftly.flags.ui.util.getPoliticalInternalRelatedFlagKeys
import dev.aftly.flags.ui.util.getChronologicalRelatedFlagKeys
import android.content.Context
import dev.aftly.flags.ui.util.getFlagNameResIds
import dev.aftly.flags.ui.util.getChronologicalAdminsOfParentKeys
import dev.aftly.flags.ui.util.getPoliticalExternalRelatedFlagKeys
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream


data object DataSource {
    const val SOURCE_URL = "https://github.com/aftly/Flags"
    const val NAV_SEPARATOR = ","

    /* For use in Filter Menu */
    val menuSuperCategoryList = listOf(
        FlagSuperCategory.All,
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International,
        FlagSuperCategory.Cultural,
        FlagSuperCategory.Historical,
        FlagSuperCategory.Political
    )

    /* For use in updating flags list when selecting category in Filter Menu */
    val historicalSuperCategoryExceptions = listOf(
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International,
        FlagSuperCategory.Political
    )

    /* For use in multi-selection in Filter Menu */
    val mutuallyExclusiveSuperCategories1 = listOf(
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International
    )

    /* For use in multi-selection in Filter Menu */
    val mutuallyExclusiveSuperCategories2 = listOf(
        FlagSuperCategory.Cultural,
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.International
    )

    /* For use in multi-selection in Filter Menu */
    val mutuallyExclusiveSubCategories = listOf(
        FlagSuperCategory.Regional,
        FlagSuperCategory.TerritorialDistributionOfAuthority,
        FlagSuperCategory.ExecutiveStructure,
        FlagSuperCategory.LegalConstraint,
        FlagSuperCategory.PowerDerivation,
        FlagSuperCategory.IdeologicalOrientation
    )

    val nullFlag = FlagView(
        id = 0,
        wikipediaUrlPath = 0,
        image = 0,
        imagePreview = 0,
        fromYear = null,
        toYear = null,
        flagOf = 0,
        flagOfOfficial = 0,
        flagOfAlternate = null,
        isFlagOfThe = false,
        isFlagOfOfficialThe = false,
        isLatestEntity = false,
        associatedStateKey = null,
        sovereignStateKey = null,
        parentUnitKey = null,
        previousFlagOfKey = null,
        flagStringResIds = emptyList(),
        politicalInternalRelatedFlagKeys = emptyList(),
        politicalExternalRelatedFlagKeys = emptyList(),
        chronologicalRelatedFlagKeys = emptyList(),
        otherLocaleRelatedFlagKeys = emptyList(),
        categories = emptyList(),
    )

    private val json = Json { ignoreUnknownKeys = true }

    lateinit var flagResMap: Map<String, FlagResources>; private set
    lateinit var inverseFlagResMap: Map<FlagResources, String>; private set
    lateinit var flagViewMap: Map<String, FlagView>; private set
    lateinit var inverseFlagViewMap: Map<FlagView, String>; private set
    lateinit var flagViewMapId: Map<Int, FlagView>; private set
    lateinit var allFlagsList: List<FlagView>; private set

    @Volatile private var isInit = false

    @OptIn(ExperimentalSerializationApi::class)
    fun init(context: Context) {
        if (isInit) return

        synchronized(this) {
            if (isInit) return

            context.resources.openRawResource(R.raw.flags_map).use {
                flagResMap = json.decodeFromStream<Map<String, FlagResources>>(it)
            }
            inverseFlagResMap = getInverseFlagResMap(flagResMap)
            flagViewMap = getFlagViewMap(flagResMap, context)
            inverseFlagViewMap = getInverseFlagViewMap(flagViewMap)
            flagViewMapId = getFlagViewMapId(flagViewMap)
            allFlagsList = getAllFlags(flagViewMap)

            isInit = true
        }
    }

    private fun getInverseFlagResMap(
        map: Map<String, FlagResources>
    ): Map<FlagResources, String> =
        map.entries.associate { it.value to it.key }

    private fun getFlagViewMap(
        map: Map<String, FlagResources>,
        context: Context,
    ): Map<String, FlagView> = map.mapValues { (flagKey, flagRes) ->
        /* Transform expression */
        FlagView(
            id = flagRes.id,
            wikipediaUrlPath = when (flagRes.wikipediaUrlPath) {
                is StringResSource.Explicit -> flagRes.wikipediaUrlPath.resName.resId(context)
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagResMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.wikipediaUrlPath) {
                            is StringResSource.Explicit ->
                                parentFlagRes.wikipediaUrlPath.resName.resId(context)
                            is StringResSource.Inherit ->
                                error("$flagKey and $parentKey have no wikipedia url path")
                        }
                    }
                } ?: error("$flagKey has no previousFlagOf key")
            },
            image = flagRes.image.resId(context),
            imagePreview = flagRes.imagePreview.resId(context),
            fromYear = flagRes.fromYear,
            toYear = flagRes.toYear,
            flagOf = when (flagRes.flagOf) {
                is StringResSource.Explicit -> flagRes.flagOf.resName.resId(context)
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagResMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.flagOf) {
                            is StringResSource.Explicit ->
                                parentFlagRes.flagOf.resName.resId(context)
                            is StringResSource.Inherit ->
                                error("$flagKey and $parentKey have no name")
                        }
                    }
                } ?: error("$flagKey has no previousFlagOf key")
            },
            flagOfOfficial = when (flagRes.flagOfOfficial) {
                is StringResSource.Explicit -> flagRes.flagOfOfficial.resName.resId(context)
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagResMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.flagOfOfficial) {
                            is StringResSource.Explicit ->
                                parentFlagRes.flagOfOfficial.resName.resId(context)
                            is StringResSource.Inherit ->
                                error("$flagKey and $parentKey have no official name")
                        }
                    }
                } ?: error("$flagKey has no previousFlagOf key")
            },
            flagOfAlternate = flagRes.flagOfAlternate?.map { it.resId(context) },
            isFlagOfThe = when (flagRes.isFlagOfThe) {
                is BooleanSource.Explicit -> flagRes.isFlagOfThe.bool
                is BooleanSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagResMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.isFlagOfThe) {
                            is BooleanSource.Explicit -> parentFlagRes.isFlagOfThe.bool
                            is BooleanSource.Inherit ->
                                error("$flagKey and $parentKey have no isFlagOfThe")
                        }
                    }
                } ?: error("$flagKey has no previousFlagOf key")
            },
            isFlagOfOfficialThe = when (flagRes.isFlagOfOfficialThe) {
                is BooleanSource.Explicit -> flagRes.isFlagOfOfficialThe.bool
                is BooleanSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagResMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.isFlagOfOfficialThe) {
                            is BooleanSource.Explicit -> parentFlagRes.isFlagOfOfficialThe.bool
                            is BooleanSource.Inherit ->
                                error("$flagKey and $parentKey have no isFlagOfOfficialThe")
                        }
                    }
                } ?: error("$flagKey has no previousFlagOf key")
            },
            isLatestEntity = flagRes.latestEntity == null,
            associatedStateKey = flagRes.associatedState,
            sovereignStateKey = flagRes.sovereignState,
            parentUnitKey = flagRes.parentUnit,
            previousFlagOfKey = flagRes.previousFlagOf,
            flagStringResIds = getFlagNameResIds(flagKey, flagRes, context),
            politicalInternalRelatedFlagKeys = getPoliticalInternalRelatedFlagKeys(flagKey, flagRes),
            politicalExternalRelatedFlagKeys = getPoliticalExternalRelatedFlagKeys(flagKey, flagRes),
            chronologicalRelatedFlagKeys = getChronologicalRelatedFlagKeys(flagKey, flagRes),
            otherLocaleRelatedFlagKeys = getChronologicalAdminsOfParentKeys(flagKey, flagRes),
            categories = flagRes.categories,
        )
    }

    private fun getInverseFlagViewMap(
        map: Map<String, FlagView>
    ): Map<FlagView, String> =
        map.entries.associate { it.value to it.key }

    private fun getFlagViewMapId(
        map: Map<String, FlagView>
    ): Map<Int, FlagView> =
        map.entries.associate { it.value.id to it.value }

    private fun getAllFlags(
        map: Map<String, FlagView>
    ): List<FlagView> =
        map.values.toList()
}
