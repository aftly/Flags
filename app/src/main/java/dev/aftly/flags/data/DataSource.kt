package dev.aftly.flags.data

import android.content.Context
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.ui.util.getBooleanExplicitOrInherit
import dev.aftly.flags.ui.util.getChronologicalDirectRelatedFlagKeys
import dev.aftly.flags.ui.util.getChronologicalIndirectRelatedFlagKeys
import dev.aftly.flags.ui.util.getFlagOfAlternativeResIds
import dev.aftly.flags.ui.util.getPoliticalExternalRelatedFlagKeys
import dev.aftly.flags.ui.util.getPoliticalInternalRelatedFlagKeys
import dev.aftly.flags.ui.util.getStringResExplicitOrInherit
import dev.aftly.flags.ui.util.getStringResExplicitOrInheritOrNull
import dev.aftly.flags.ui.util.toWrapper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream


data object DataSource {
    const val SOURCE_URL = "https://github.com/aftly/Flags"
    const val NAV_SEPARATOR = ","

    /* For use in Filter Menu */
    val menuSuperCategoryList = listOf(
        FlagSuperCategory.All,
        FlagSuperCategory.Sovereign,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International,
        FlagSuperCategory.Institution,
        FlagSuperCategory.Cultural,
        FlagSuperCategory.OtherParameters,
        FlagSuperCategory.Historical,
        FlagSuperCategory.Political
    )

    /* For use in updating flags list when selecting category in Filter Menu */
    val historicalSuperCategoryBlacklist = listOf(
        FlagSuperCategory.Sovereign,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International,
        FlagSuperCategory.Political
    )
    val historicalSubCategoryWhitelist = listOf(
        FlagCategory.CONFEDERATION,
        FlagCategory.FASCIST
    )
    val absenceCategoriesMap = mapOf(
        FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL to listOf(FlagCategory.CONSTITUTIONAL)
    )
    val absenceCategoriesAddAnyMap = mapOf(
        FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL to FlagSuperCategory.Sovereign.enums()
    )

    /* ----- For use in multi-selection in Filter Menu ----- */
    val categoriesMutuallyExclusive: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Sovereign,
                FlagSuperCategory.Regional,
                FlagSuperCategory.International
            )
        )
        addAll(elements = FlagSuperCategory.Sovereign.enums().map { it.toWrapper() })
        addAll(elements = FlagSuperCategory.Regional.enums().map { it.toWrapper() })
        addAll(elements = FlagSuperCategory.International.enums().map { it.toWrapper() })
    }

    val categoriesSovereignState: List<FlagCategoryBase> = listOf(
        FlagSuperCategory.Sovereign,
        FlagCategory.SOVEREIGN_STATE.toWrapper(),
        FlagCategory.MICROSTATE.toWrapper(),
    )
    val categoriesInclusiveOfSovereignState: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Sovereign,
                FlagSuperCategory.AutonomousRegion,
                FlagSuperCategory.Historical,
                FlagCategory.SOVEREIGN_STATE.toWrapper(),
                FlagCategory.FREE_ASSOCIATION.toWrapper(),
                FlagCategory.ANNEXED_TERRITORY.toWrapper(),
                FlagCategory.MICROSTATE.toWrapper(),
            )
        )
        addAll(elements = FlagSuperCategory.Political.allEnums().map { it.toWrapper() })
    }
    val categoriesSovereignStateExceptionPairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> =
        listOf(
            Pair(
                first = FlagSuperCategory.Sovereign,
                second = FlagCategory.RELIGIOUS.toWrapper()
            ),
            Pair(
                first = FlagSuperCategory.Sovereign,
                second = FlagSuperCategory.Cultural
            ),
            Pair(
                first = FlagCategory.MICROSTATE.toWrapper(),
                second = FlagCategory.UNRECOGNIZED_STATE.toWrapper()
            )
        )

    val categoriesSovereignEntity: List<FlagCategoryBase> = listOf(
        FlagCategory.SOVEREIGN_ENTITY.toWrapper()
    )
    val categoriesInclusiveOfSovereignEntity: List<FlagCategoryBase> = listOf(
        FlagSuperCategory.Cultural,
        FlagCategory.RELIGIOUS.toWrapper()
    )

    val categoriesAutonomousRegion: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.AutonomousRegion)
        addAll(elements = FlagSuperCategory.AutonomousRegion.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfAutonomousRegion: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Institution,
                FlagCategory.POLITICAL_MOVEMENT.toWrapper(),
                FlagCategory.TRIBE.toWrapper(),
                FlagCategory.ETHNIC.toWrapper(),
                FlagCategory.SOCIAL.toWrapper(),
                FlagCategory.MARITIME.toWrapper(),
                FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
                FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
            )
        )
        addAll(elements = FlagSuperCategory.Institution.allChildSupers())
        addAll(elements = FlagSuperCategory.Institution.allEnums().map { it.toWrapper() })
    }
    val categoriesAutonomousRegionPairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> = listOf(
        Pair(
            first = FlagSuperCategory.AutonomousRegion,
            second = FlagCategory.POLITICAL_MOVEMENT.toWrapper()
        ),
        Pair(
            first = FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
            second = FlagCategory.POLITICAL_MOVEMENT.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.AutonomousRegion,
            second = FlagCategory.TRIBE.toWrapper()
        ),
        Pair(
            first = FlagCategory.AUTONOMOUS_REGION.toWrapper(),
            second = FlagCategory.TRIBE.toWrapper()
        ),
        Pair(
            first = FlagCategory.INDIGENOUS_TERRITORY.toWrapper(),
            second = FlagCategory.TRIBE.toWrapper()
        ),
        Pair(
            first = FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
            second = FlagCategory.TRIBE.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.AutonomousRegion,
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagCategory.AUTONOMOUS_REGION.toWrapper(),
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagCategory.DEVOLVED_GOVERNMENT.toWrapper(),
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagCategory.INDIGENOUS_TERRITORY.toWrapper(),
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.AutonomousRegion,
            second = FlagCategory.MILITANT_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
            second = FlagCategory.MILITANT_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.AutonomousRegion,
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
    )

    val categoriesRegional: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.Regional)
        addAll(elements = FlagSuperCategory.Regional.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfRegional: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Institution,
                FlagCategory.MARITIME.toWrapper(),
                FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
                FlagCategory.TERRORIST_ORGANIZATION.toWrapper(),
                FlagCategory.INDIGENOUS_TERRITORY.toWrapper(),
                FlagCategory.UNRECOGNIZED_STATE.toWrapper()
            )
        )
        addAll(elements = FlagSuperCategory.Institution.allChildSupers())
        addAll(elements = FlagSuperCategory.Institution.allEnums().map { it.toWrapper() })
        addAll(elements = FlagSuperCategory.Political.allEnums().map { it.toWrapper() })
    }
    val categoriesRegionalPairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> = listOf(
        Pair(
            first = FlagSuperCategory.Regional,
            second = FlagCategory.UNRECOGNIZED_STATE.toWrapper()
        ),
        Pair(
            first = FlagCategory.TERRITORY.toWrapper(),
            second = FlagCategory.UNRECOGNIZED_STATE.toWrapper()
        )
    )

    val categoriesInternational: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.International)
        addAll(elements = FlagSuperCategory.International.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfInternational: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.AutonomousRegion,
                FlagSuperCategory.Cultural
            )
        )
        addAll(elements = FlagSuperCategory.AutonomousRegion.enums().map { it.toWrapper() })
        addAll(elements = FlagSuperCategory.Cultural.enums()
            .filterNot { it == FlagCategory.POLITICAL_MOVEMENT }.map { it.toWrapper() }
        )
        addAll(elements = FlagSuperCategory.TerritorialDistributionOfAuthority.enums()
            .filterNot { it == FlagCategory.CONFEDERATION }.map { it.toWrapper() }
        )
        addAll(elements = FlagSuperCategory.PowerDerivation.enums().map { it.toWrapper() })
        addAll(elements = FlagSuperCategory.IdeologicalOrientation.enums().map { it.toWrapper() })
    }

    val categoriesLegislature: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.Legislature)
        addAll(elements = FlagSuperCategory.Legislature.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfLegislature: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Cultural,
                FlagCategory.MARITIME.toWrapper(),
                FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
                FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
            )
        )
        addAll(elements = FlagSuperCategory.Cultural.enums().map { it.toWrapper() })
    }

    val categoriesExecutive: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.Executive)
        addAll(elements = FlagSuperCategory.Executive.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfExecutive: List<FlagCategoryBase> = buildList {
        addAll(
            elements = listOf(
                FlagSuperCategory.Cultural,
                FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
                FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
            )
        )
        addAll(elements = FlagSuperCategory.Cultural.enums().map { it.toWrapper() })
    }
    val categoriesExecutivePairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> = listOf(
        Pair(
            first = FlagCategory.MILITARY.toWrapper(),
            second = FlagCategory.MILITANT_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Executive,
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagCategory.MILITARY.toWrapper(),
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
    )


    val categoriesCivilian: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.Civilian)
        addAll(
            elements = FlagSuperCategory.Civilian.enums().filterNot { it == FlagCategory.RELIGIOUS }
                .map { it.toWrapper() }
        )
    }
    val categoriesExclusiveOfCivilian: List<FlagCategoryBase> = listOf(
        FlagCategory.POLITICAL_MOVEMENT.toWrapper(),
        FlagCategory.RELIGIOUS.toWrapper(),
        FlagCategory.ETHNIC.toWrapper(),
        FlagCategory.SOCIAL.toWrapper(),
        FlagCategory.MARITIME.toWrapper(),
        FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
        FlagCategory.TERRORIST_ORGANIZATION.toWrapper(),
    )
    val categoriesCivilianPairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> = listOf(
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.POLITICAL_MOVEMENT.toWrapper()
        ),
        Pair(
            first = FlagCategory.CHARITY.toWrapper(),
            second = FlagCategory.POLITICAL_MOVEMENT.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.RELIGIOUS.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
            second = FlagCategory.RELIGIOUS.toWrapper()
        ),
        Pair(
            first = FlagCategory.CHARITY.toWrapper(),
            second = FlagCategory.RELIGIOUS.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
            second = FlagCategory.ETHNIC.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.SOCIAL.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
            second = FlagCategory.SOCIAL.toWrapper()
        ),
        Pair(
            first = FlagCategory.CHARITY.toWrapper(),
            second = FlagCategory.SOCIAL.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.MARITIME.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
            second = FlagCategory.MARITIME.toWrapper()
        ),
        Pair(
            first = FlagCategory.CHARITY.toWrapper(),
            second = FlagCategory.MARITIME.toWrapper()
        ),
        Pair(
            first = FlagCategory.VEXILLOLOGY.toWrapper(),
            second = FlagCategory.MARITIME.toWrapper()
        ),
        Pair(
            first = FlagSuperCategory.Civilian,
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
            second = FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
        ),
    )

    val categoriesCultural: List<FlagCategoryBase> = buildList {
        add(FlagSuperCategory.Cultural)
        addAll(elements = FlagSuperCategory.Cultural.enums().map { it.toWrapper() })
    }
    val categoriesExclusiveOfCultural: List<FlagCategoryBase> = listOf(
        FlagCategory.MARITIME.toWrapper(),
        FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
        FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
    )
    val categoriesCulturalPairs: List<Pair<FlagCategoryBase, FlagCategoryBase>> = listOf(
        Pair(
            first = FlagSuperCategory.Cultural,
            second = FlagCategory.MARITIME.toWrapper()
        ),
        Pair(
            first = FlagCategory.POLITICAL_MOVEMENT.toWrapper(),
            second = FlagCategory.MARITIME.toWrapper()
        )
    )

    val categoriesPolitical: List<FlagCategoryBase> =
        FlagSuperCategory.Political.allEnums().map { it.toWrapper() }
    val categoriesExclusiveOfPolitical: List<FlagCategoryBase> = buildList {
        addAll(elements = FlagSuperCategory.Institution.allSupers())
        addAll(elements = FlagSuperCategory.Cultural.enums().map { it.toWrapper() })
        add(FlagSuperCategory.Cultural)
        add(FlagCategory.MARITIME.toWrapper())
    }

    val categoriesDevolvedGovernment: List<FlagCategoryBase> = listOf(
        FlagCategory.DEVOLVED_GOVERNMENT.toWrapper()
    )
    val categoriesExclusiveOfDevolvedGovernment: List<FlagCategoryBase> = listOf(
        FlagCategory.INDIGENOUS_TERRITORY.toWrapper(),
        FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
        FlagCategory.ANNEXED_TERRITORY.toWrapper()
    )

    val categoriesIndigenousTerritory: List<FlagCategoryBase> = listOf(
        FlagCategory.INDIGENOUS_TERRITORY.toWrapper()
    )
    val categoriesExclusiveOfIndigenousTerritory: List<FlagCategoryBase> = listOf(
        FlagCategory.FREE_ASSOCIATION.toWrapper(),
        FlagCategory.AUTONOMOUS_REGION.toWrapper()
    )

    val categoriesQuasiState: List<FlagCategoryBase> = listOf(FlagCategory.QUASI_STATE.toWrapper())
    val categoriesInclusiveOfQuasiState: List<FlagCategoryBase> = listOf(
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Executive,
        FlagSuperCategory.Civilian,
        FlagSuperCategory.Cultural,
        FlagSuperCategory.Historical,
        FlagCategory.UNRECOGNIZED_STATE.toWrapper(),
        FlagCategory.ANNEXED_TERRITORY.toWrapper(),
        FlagCategory.MILITARY.toWrapper(),
        FlagCategory.POLITICAL_ORGANIZATION.toWrapper(),
        FlagCategory.POLITICAL_MOVEMENT.toWrapper(),
        FlagCategory.MILITANT_ORGANIZATION.toWrapper(),
        FlagCategory.TERRORIST_ORGANIZATION.toWrapper()
    )

    val switchSupersSuperCategories = listOf(FlagSuperCategory.Institution)
    val switchSubsSuperCategories = listOf(
        FlagSuperCategory.Sovereign,
        FlagSuperCategory.LegislatureDivision,
        FlagSuperCategory.LegislatureBody,
        FlagSuperCategory.Executive,
        FlagSuperCategory.Regional,
        FlagSuperCategory.TerritorialDistributionOfAuthority,
        FlagSuperCategory.ExecutiveStructure,
        FlagSuperCategory.LegalConstraint,
        FlagSuperCategory.PowerDerivation,
        FlagSuperCategory.IdeologicalOrientation
    )


    val nullFlag = FlagView(
        id = 0,
        wikipediaUrlPath = R.string.error,
        image = R.drawable.flags_icon_circle,
        imagePreview = R.drawable.flags_icon_circle,
        fromYear = null,
        fromYearCirca = null,
        toYear = null,
        toYearCirca = null,
        flagOf = R.string.error,
        flagOfDescriptor = R.string.error,
        flagOfOfficial = R.string.error,
        flagOfAlternate = emptyList(),
        isFlagOfThe = false,
        isFlagOfOfficialThe = false,
        internationalOrganisationKeys = emptyList(),
        associatedStateKey = null,
        sovereignStateKey = null,
        parentUnitKey = null,
        latestEntityKeys = emptyList(),
        previousFlagOfKey = null,
        flagStringResIds = listOf(R.string.error),
        politicalInternalRelatedFlagKeys = emptyList(),
        politicalExternalRelatedFlagKeys = emptyList(),
        chronologicalDirectRelatedFlagKeys = emptyList(),
        chronologicalIndirectRelatedFlagKeys = emptyList(),
        categories = emptyList(),
    )

    private val json = Json { ignoreUnknownKeys = true }

    lateinit var flagResMap: Map<String, FlagResources>; private set
    lateinit var inverseFlagResMap: Map<FlagResources, String>; private set
    lateinit var annexedFlagResMap: Map<String, FlagResources>; private set
    lateinit var unrecognizedStateFlagResMap: Map<String, FlagResources>; private set
    lateinit var flagViewMap: Map<String, FlagView>; private set
    lateinit var inverseFlagViewMap: Map<FlagView, String>; private set
    lateinit var flagViewMapId: Map<Int, FlagView>; private set
    lateinit var allFlagsList: List<FlagView>; private set
    lateinit var countryFlagsList: List<FlagView>; private set

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
            annexedFlagResMap = getAnnexedFlagResMap(flagResMap)
            unrecognizedStateFlagResMap = getUnrecognizedStateFlagResMap(flagResMap)
            flagViewMap = getFlagViewMap(flagResMap, context)
            inverseFlagViewMap = getInverseFlagViewMap(flagViewMap)
            flagViewMapId = getFlagViewMapId(flagViewMap)
            allFlagsList = getAllFlags(flagViewMap)
            countryFlagsList = getCountryFlags(allFlagsList)

            isInit = true
        }
    }

    private fun getInverseFlagResMap(
        map: Map<String, FlagResources>
    ): Map<FlagResources, String> =
        map.entries.associate { it.value to it.key }

    private fun getAnnexedFlagResMap(
        map: Map<String, FlagResources>
    ): Map<String, FlagResources> =
        map.filterValues { FlagCategory.ANNEXED_TERRITORY in it.categories }

    private fun getUnrecognizedStateFlagResMap(
        map: Map<String, FlagResources>
    ): Map<String, FlagResources> =
        map.filterValues { FlagCategory.UNRECOGNIZED_STATE in it.categories }

    private fun getFlagViewMap(
        map: Map<String, FlagResources>,
        context: Context,
    ): Map<String, FlagView> = map.mapValues { (flagKey, flagRes) ->
        val polIntRelFlagKeys = getPoliticalInternalRelatedFlagKeys(flagKey, flagRes)
        val polExtRelFlagKeys = getPoliticalExternalRelatedFlagKeys(flagKey, flagRes)

        val chronDirRelFlagKeys = getChronologicalDirectRelatedFlagKeys(flagKey, flagRes)
        val chronIndirRelFlagKeys = getChronologicalIndirectRelatedFlagKeys(flagRes)

        val flagOfResId = getStringResExplicitOrInherit(
            flagKey = flagKey,
            flagRes = flagRes,
            prop = FlagResources::flagOf,
            propName = "name",
            context = context,
        )
        val flagOfDescriptorResId = getStringResExplicitOrInheritOrNull(
            flagKey = flagKey,
            flagRes = flagRes,
            prop = FlagResources::flagOfDescriptor,
            propName = "descriptor (name)",
            context = context,
        )
        val flagOfOfficialResId = getStringResExplicitOrInherit(
            flagKey = flagKey,
            flagRes = flagRes,
            prop = FlagResources::flagOfOfficial,
            propName = "official name",
            context = context,
        )
        val flagOfAlternativeResIds = getFlagOfAlternativeResIds(flagKey, flagRes, context)

        /* Transform expression */
        FlagView(
            id = flagRes.id,
            wikipediaUrlPath = getStringResExplicitOrInherit(
                flagKey = flagKey,
                flagRes = flagRes,
                prop = FlagResources::wikipediaUrlPath,
                propName = "wikipedia url path",
                context = context,
            ),
            image = flagRes.image.resId(context),
            imagePreview = flagRes.imagePreview.resId(context),
            fromYear = flagRes.fromYear,
            fromYearCirca = flagRes.fromYearCirca,
            toYear = flagRes.toYear,
            toYearCirca = flagRes.toYearCirca,
            flagOf = flagOfResId,
            flagOfDescriptor = flagOfDescriptorResId,
            flagOfOfficial = flagOfOfficialResId,
            flagOfAlternate = flagOfAlternativeResIds,
            isFlagOfThe = getBooleanExplicitOrInherit(
                flagKey = flagKey,
                flagRes = flagRes,
                prop = FlagResources::isFlagOfThe,
                propName = "isFlagOfThe value",
            ),
            isFlagOfOfficialThe = getBooleanExplicitOrInherit(
                flagKey = flagKey,
                flagRes = flagRes,
                prop = FlagResources::isFlagOfOfficialThe,
                propName = "isFlagOfOfficialThe value",
            ),
            internationalOrganisationKeys = flagRes.internationalOrganisations,
            associatedStateKey = flagRes.associatedState,
            sovereignStateKey = flagRes.sovereignState,
            parentUnitKey = flagRes.parentUnit,
            latestEntityKeys = flagRes.latestEntities,
            previousFlagOfKey = flagRes.previousFlagOf,
            flagStringResIds = buildList {
                add(flagOfResId)
                flagOfDescriptorResId?.let { add(it) }
                add(flagOfOfficialResId)
                addAll(elements = flagOfAlternativeResIds)
            },
            politicalInternalRelatedFlagKeys = polIntRelFlagKeys,
            politicalExternalRelatedFlagKeys = polExtRelFlagKeys,
            chronologicalDirectRelatedFlagKeys = chronDirRelFlagKeys,
            chronologicalIndirectRelatedFlagKeys = chronIndirRelFlagKeys,
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

    private fun getCountryFlags(
        list: List<FlagView>
    ): List<FlagView> = list.filter { flag ->
        FlagCategory.SOVEREIGN_STATE in flag.categories &&
                FlagCategory.HISTORICAL !in flag.categories
    }
}
