package dev.aftly.flags.data

import android.content.Context
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
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
        FlagSuperCategory.Institution,
        FlagSuperCategory.Cultural,
        FlagSuperCategory.OtherParameters,
        FlagSuperCategory.Historical,
        FlagSuperCategory.Political
    )

    /* For use in updating flags list when selecting category in Filter Menu */
    val historicalSuperCategoryBlacklist = listOf(
        FlagSuperCategory.SovereignCountry,
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
        FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL to FlagSuperCategory.SovereignCountry.enums()
    )

    /* ----- For use in multi-selection in Filter Menu ----- */
    val mutuallyExclusiveSuperCategories1 = listOf(
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International
    )
    val mutuallyExclusiveSuperCategories2 = listOf(
        FlagSuperCategory.Cultural,
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.International
    )
    val supersExclusiveOfInternational = listOf(
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.TerritorialDistributionOfAuthority,
        FlagSuperCategory.PowerDerivation,
        FlagSuperCategory.IdeologicalOrientation,

    )
    val supersExclusiveOfInstitution = listOf(
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional
    )
    val supersExclusiveOfCultural = listOf(
        FlagSuperCategory.Legislature,
        FlagSuperCategory.Executive
    )
    val supersExclusiveOfPolitical = buildList {
        add(FlagSuperCategory.Regional)
        addAll(elements = FlagSuperCategory.Institution.allSupers())
        add(FlagSuperCategory.Cultural)
    }
    val subsExclusiveOfCountry = listOf(
        FlagCategory.AUTONOMOUS_REGION,
        FlagCategory.DEVOLVED_GOVERNMENT,
        FlagCategory.INDIGENOUS_TERRITORY,
        FlagCategory.UNRECOGNIZED_STATE,
        FlagCategory.SOVEREIGN_ENTITY,
        FlagCategory.MARITIME,
        FlagCategory.QUASI_STATE,
        FlagCategory.MILITANT_ORGANIZATION,
        FlagCategory.TERRORIST_ORGANIZATION
    )
    val subsExclusiveOfAutonomousRegion = listOf(
        FlagCategory.MICROSTATE,
        FlagCategory.MARITIME
    )
    val subsExclusiveOfRegional = listOf(
        FlagCategory.MICROSTATE,
        FlagCategory.MARITIME,
        FlagCategory.QUASI_STATE,
        FlagCategory.MILITANT_ORGANIZATION,
        FlagCategory.TERRORIST_ORGANIZATION,
        FlagCategory.INDIGENOUS_TERRITORY,
        FlagCategory.SOVEREIGN_ENTITY
    )
    val subsExclusiveOfInternational = listOf(
        FlagCategory.MICROSTATE,
        FlagCategory.QUASI_STATE
    )
    val subsExclusiveOfInstitution = listOf(FlagCategory.MICROSTATE)
    val subsExclusiveOfLegislature = listOf(
        FlagCategory.MARITIME,
        FlagCategory.QUASI_STATE,
        FlagCategory.MILITANT_ORGANIZATION,
        FlagCategory.TERRORIST_ORGANIZATION
    )
    val subsExclusiveOfExecutive = listOf(FlagCategory.MILITANT_ORGANIZATION)
    val subsExclusiveOfCultural = listOf(
        FlagCategory.MICROSTATE,
        FlagCategory.ANNEXED_TERRITORY
    )
    val subsExclusiveOfOtherParameters = listOf(FlagCategory.SOVEREIGN_ENTITY)
    val subsExclusiveOfPolitical = listOf(FlagCategory.MARITIME)
    val switchSupersSuperCategories = listOf(
        FlagSuperCategory.Institution
    )
    val switchSubsSuperCategories = listOf(
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
