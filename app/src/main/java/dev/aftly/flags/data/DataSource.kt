package dev.aftly.flags.data

import dev.aftly.flags.R
import dev.aftly.flags.model.BooleanSource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.StringResSource
import dev.aftly.flags.ui.util.getExternalRelatedFlagKeys
import dev.aftly.flags.ui.util.getInternalRelatedFlagKeys
import android.content.Context
import dev.aftly.flags.ui.util.getFlagNameResIds
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
        associatedState = null,
        sovereignState = null,
        flagStringResIds = emptyList(),
        allSearchStringResIds = emptyList(),
        externalRelatedFlags = emptyList(),
        internalRelatedFlags = emptyList(),
        categories = emptyList(),
    )

    private val json = Json { ignoreUnknownKeys = true }

    lateinit var flagResMap: Map<String, FlagResources>; private set
    lateinit var inverseFlagsMap: Map<FlagResources, String>; private set
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
            inverseFlagsMap = getInverseFlagResMap(flagResMap)
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
        val externalRelatedFlags = getExternalRelatedFlagKeys(flagKey, flagRes)
        val internalRelatedFlags = getInternalRelatedFlagKeys(flagKey, flagRes)

        val flagStringResIds = getFlagNameResIds(flagKey, flagRes, context)

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
            associatedState = flagRes.associatedState,
            sovereignState = flagRes.sovereignState,
            flagStringResIds = flagStringResIds,
            allSearchStringResIds = flagStringResIds + (externalRelatedFlags + internalRelatedFlags)
                .distinct().flatMap { relatedFlagKey ->
                    val relatedFlagRes = flagResMap.getValue(relatedFlagKey)

                    getFlagNameResIds(relatedFlagKey, relatedFlagRes, context)
                },
            /*
            allSearchStringResIds = flagStringResIds + (externalRelatedFlags + internalRelatedFlags)
                .distinct().flatMap { relatedKey ->
                val relatedRes = flagResMap.getValue(relatedKey)

                val flagOf = when (relatedRes.flagOf) {
                    is StringResSource.Explicit -> relatedRes.flagOf.resName.resId(context)
                    is StringResSource.Inherit -> relatedRes.previousFlagOf?.let { parentKey ->
                        flagResMap.getValue(parentKey).let { parentFlagRes ->
                            when (parentFlagRes.flagOf) {
                                is StringResSource.Explicit ->
                                    parentFlagRes.flagOf.resName.resId(context)
                                is StringResSource.Inherit ->
                                    error("$relatedKey and $parentKey have no name")
                            }
                        }
                    } ?: error("$relatedKey has no previousFlagOf key")
                }
                val flagOfOfficial = when (relatedRes.flagOfOfficial) {
                    is StringResSource.Explicit -> relatedRes.flagOfOfficial.resName.resId(context)
                    is StringResSource.Inherit -> relatedRes.previousFlagOf?.let { parentKey ->
                        flagResMap.getValue(parentKey).let { parentFlagRes ->
                            when (parentFlagRes.flagOfOfficial) {
                                is StringResSource.Explicit ->
                                    parentFlagRes.flagOfOfficial.resName.resId(context)
                                is StringResSource.Inherit ->
                                    error("$relatedKey and $parentKey have no official name")
                            }
                        }
                    } ?: error("$relatedKey has no previousFlagOf key")
                }

                val primaryNames = listOf(flagOf, flagOfOfficial)

                /* Transform expression */
                relatedRes.flagOfAlternate?.map { it.resId(context) }?.let { secondaryNames ->
                    primaryNames + secondaryNames
                } ?: primaryNames
            },
             */
            externalRelatedFlags = externalRelatedFlags,
            internalRelatedFlags = internalRelatedFlags,
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


    //@Volatile var flagsMap: Map<String, FlagResources>? = null

    /*
    @OptIn(ExperimentalSerializationApi::class)
    fun getFlagsMap(context: Context): Map<String, FlagResources> {
        flagsMap?.let { return it }

        val parsed = context.resources.openRawResource(R.raw.flags_map).use {
            json.decodeFromStream<Map<String, FlagResources>>(it)
        }
        return parsed
    }
     */



    // TODO rename flagResMap
    //val flagsMap = mapOf()

    /* TODO remove
    val flagsMapIdOld = flagsMap.entries.groupBy(
        keySelector = { it.value.id },
        valueTransform = { it.value }
    ).mapValues { entry ->
        entry.value.first()
    }
     */

    // TODO remove
    /*
    val flagsMapId = flagsMap.entries.associate { (key, flagRes) ->
        flagRes.id to flagRes
    }
     */

    /* TODO remove
    val inverseFlagsMapOld = flagsMap.entries.groupBy(
        keySelector = { it.value },
        valueTransform = { it.key },
    ).mapValues { entry ->
        entry.value.first()
    }
     */

    // TODO rename inverseFlagResMap
    /*
    val inverseFlagsMap = flagsMap.entries.associate { (key, value) ->
        value to key
    }
     */

    //val allFlagsList = flagsMap.values.toList() TODO remove

    /* Derive needed values from parents and related flag keys for view and search to preserve
     * SSOT in static flag data */
    /*
    val flagViewMap: Map<String, FlagView> = flagsMap.mapValues { (key, flagRes) ->
        val externalRelatedFlags = getExternalRelatedFlagKeys(key, flagRes)
        val internalRelatedFlags = getInternalRelatedFlagKeys(key, flagRes)

        /* Transform expression */
        FlagView(
            id = flagRes.id,
            wikipediaUrlPath = when (flagRes.wikipediaUrlPath) {
                is StringResSource.Explicit -> flagRes.wikipediaUrlPath.resId
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagsMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.wikipediaUrlPath) {
                            is StringResSource.Explicit -> parentFlagRes.wikipediaUrlPath.resId
                            is StringResSource.Inherit ->
                                error("$key and $parentKey have no wikipedia url path")
                        }
                    }
                } ?: error("$key has no previousFlagOf key")
            },
            image = flagRes.image,
            imagePreview = flagRes.imagePreview,
            fromYear = flagRes.fromYear,
            toYear = flagRes.toYear,
            flagOf = when (flagRes.flagOf) {
                is StringResSource.Explicit -> flagRes.flagOf.resId
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagsMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.flagOf) {
                            is StringResSource.Explicit -> parentFlagRes.flagOf.resId
                            is StringResSource.Inherit -> error("$key and $parentKey have no name")
                        }
                    }
                } ?: error("$key has no previousFlagOf key")
            },
            flagOfOfficial = when (flagRes.flagOfOfficial) {
                is StringResSource.Explicit -> flagRes.flagOfOfficial.resId
                is StringResSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagsMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.flagOfOfficial) {
                            is StringResSource.Explicit -> parentFlagRes.flagOfOfficial.resId
                            is StringResSource.Inherit ->
                                error("$key and $parentKey have no official name")
                        }
                    }
                } ?: error("$key has no previousFlagOf key")
            },
            flagOfAlternate = flagRes.flagOfAlternate,
            isFlagOfThe = when (flagRes.isFlagOfThe) {
                is BooleanSource.Explicit -> flagRes.isFlagOfThe.bool
                is BooleanSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagsMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.isFlagOfThe) {
                            is BooleanSource.Explicit -> parentFlagRes.isFlagOfThe.bool
                            is BooleanSource.Inherit ->
                                error("$key and $parentKey have no isFlagOfThe")
                        }
                    }
                } ?: error("$key has no previousFlagOf key")
            },
            isFlagOfOfficialThe = when (flagRes.isFlagOfOfficialThe) {
                is BooleanSource.Explicit -> flagRes.isFlagOfOfficialThe.bool
                is BooleanSource.Inherit -> flagRes.previousFlagOf?.let { parentKey ->
                    flagsMap.getValue(parentKey).let { parentFlagRes ->
                        when (parentFlagRes.isFlagOfOfficialThe) {
                            is BooleanSource.Explicit -> parentFlagRes.isFlagOfOfficialThe.bool
                            is BooleanSource.Inherit ->
                                error("$key and $parentKey have no isFlagOfOfficialThe")
                        }
                    }
                } ?: error("$key has no previousFlagOf key")
            },
            associatedState = flagRes.associatedState,
            sovereignState = flagRes.sovereignState,
            searchStrings = (externalRelatedFlags + internalRelatedFlags).distinct().flatMap {
                relatedKey ->
                val relatedRes = flagsMap.getValue(relatedKey)

                val flagOf = when (relatedRes.flagOf) {
                    is StringResSource.Explicit -> relatedRes.flagOf.resId
                    is StringResSource.Inherit -> relatedRes.previousFlagOf?.let { parentKey ->
                        flagsMap.getValue(parentKey).let { parentFlagRes ->
                            when (parentFlagRes.flagOf) {
                                is StringResSource.Explicit -> parentFlagRes.flagOf.resId
                                is StringResSource.Inherit ->
                                    error("$relatedKey and $parentKey have no name")
                            }
                        }
                    } ?: error("$relatedKey has no previousFlagOf key")
                }
                val flagOfOfficial = when (relatedRes.flagOfOfficial) {
                    is StringResSource.Explicit -> relatedRes.flagOfOfficial.resId
                    is StringResSource.Inherit -> relatedRes.previousFlagOf?.let { parentKey ->
                        flagsMap.getValue(parentKey).let { parentFlagRes ->
                            when (parentFlagRes.flagOfOfficial) {
                                is StringResSource.Explicit -> parentFlagRes.flagOfOfficial.resId
                                is StringResSource.Inherit ->
                                    error("$relatedKey and $parentKey have no official name")
                            }
                        }
                    } ?: error("$relatedKey has no previousFlagOf key")
                }

                val primaryNames = listOf(flagOf, flagOfOfficial)

                /* Transform expression */
                flagRes.flagOfAlternate?.let { secondaryNames ->
                    primaryNames + secondaryNames
                } ?: primaryNames
            },
            externalRelatedFlags = externalRelatedFlags,
            internalRelatedFlags = internalRelatedFlags,
            categories = flagRes.categories,
        )
    }
     */


    /*
    val inverseFlagViewMap = flagViewMap.entries.associate { (key, value) ->
        value to key
    }
     */

    /*
    val flagViewMapId = flagViewMap.entries.associate { (key, flagRes) ->
        flagRes.id to flagRes
    }
     */

    //val allFlagsList = flagViewMap.values.toList()
}
