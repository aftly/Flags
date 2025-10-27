package dev.aftly.flags.ui.util

import android.app.Application
import android.content.Context
import dev.aftly.flags.data.DataSource.NAV_SEPARATOR
import dev.aftly.flags.data.DataSource.annexedFlagResMap
import dev.aftly.flags.data.DataSource.flagResMap
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.flagViewMapId
import dev.aftly.flags.data.DataSource.inverseFlagResMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.CANTON
import dev.aftly.flags.model.FlagCategory.CITY
import dev.aftly.flags.model.FlagCategory.COLLECTIVITY
import dev.aftly.flags.model.FlagCategory.COLONY
import dev.aftly.flags.model.FlagCategory.COMMUNITY
import dev.aftly.flags.model.FlagCategory.CONFEDERATION
import dev.aftly.flags.model.FlagCategory.COUNCIL_AREA
import dev.aftly.flags.model.FlagCategory.COUNTRY
import dev.aftly.flags.model.FlagCategory.COUNTY
import dev.aftly.flags.model.FlagCategory.DISTRICT
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.HISTORICAL
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.ISLAND
import dev.aftly.flags.model.FlagCategory.KRAI
import dev.aftly.flags.model.FlagCategory.MEMBER_STATE
import dev.aftly.flags.model.FlagCategory.MICRONATION
import dev.aftly.flags.model.FlagCategory.MUNICIPALITY
import dev.aftly.flags.model.FlagCategory.OBLAST
import dev.aftly.flags.model.FlagCategory.OKRUG
import dev.aftly.flags.model.FlagCategory.POLITICAL_MOVEMENT
import dev.aftly.flags.model.FlagCategory.PROVINCE
import dev.aftly.flags.model.FlagCategory.REGION
import dev.aftly.flags.model.FlagCategory.REGIONAL
import dev.aftly.flags.model.FlagCategory.REPUBLIC_UNIT
import dev.aftly.flags.model.FlagCategory.RIDING
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.STATE
import dev.aftly.flags.model.FlagCategory.UNRECOGNIZED_STATE
import dev.aftly.flags.model.FlagCategory.TERRITORY
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory.Civilian
import dev.aftly.flags.model.FlagSuperCategory.Cultural
import dev.aftly.flags.model.FlagSuperCategory.Executive
import dev.aftly.flags.model.FlagSuperCategory.Institution
import dev.aftly.flags.model.FlagSuperCategory.International
import dev.aftly.flags.model.FlagSuperCategory.Legislature
import dev.aftly.flags.model.FlagSuperCategory.Regional
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.relatedmenu.LazyColumnItem
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagGroup
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagsCategory
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagsContent
import dev.aftly.flags.model.serialization.BooleanSource
import dev.aftly.flags.model.serialization.StringResSource

fun getFlagKey(flag: FlagView): String = inverseFlagViewMap.getValue(key = flag)

fun getFlagKeys(flags: List<FlagView>): List<String> =
    flags.map { inverseFlagViewMap.getValue(key = it) }

fun getFlagsFromKeys(flagKeys: List<String>): List<FlagView> =
    flagKeys.map { flagViewMap.getValue(key = it) }

fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagView>,
): List<FlagView> = flags.sortedBy { flag ->
    normalizeString(string = application.resources.getString(flag.flagOf))
}

fun getSavedFlagView(savedFlags: Set<SavedFlag>): List<FlagView> =
    savedFlags.map { it.getFlagView() }

fun getSavedFlagViewSorted(
    application: Application,
    savedFlags: Set<SavedFlag>,
): List<FlagView> =
    sortFlagsAlphabetically(application = application, flags = getSavedFlagView(savedFlags))


fun SavedFlag.getFlagView(): FlagView = flagViewMap.getValue(key = this.flagKey)
fun FlagView.toSavedFlag(): SavedFlag = SavedFlag(flagKey = getFlagKey(flag = this))


/* ---------- For navigation ---------- */
fun getFlagIds(flags: List<FlagView>): List<Int> = flags.map { it.id }

fun getFlagIdsString(flagIds: List<Int>): String = flagIds.joinToString(separator = NAV_SEPARATOR)

fun getFlagIdsFromString(string: String): List<Int> =
    string.split(NAV_SEPARATOR).mapNotNull { it.toIntOrNull() }

fun getFlagsFromIds(flagIds: List<Int>): List<FlagView> =
    flagIds.map { flagViewMapId.getValue(key = it) }

fun getFlagFromId(id: Int): FlagView = flagViewMapId.getValue(key = id)
/* ------------------------------------- */


/* ---------- For flagViewMap ---------- */

private fun isSovereignKeyFromParentKey(
    parentKey: String,
    sovereignKey: String,
): Boolean {
    val parent = flagResMap.getValue(parentKey)

    return if (parentKey == sovereignKey) true

    else if (parent.sovereignState == sovereignKey) true

    else if (parent.parentUnit != null && isSovereignKeyFromParentKey(
            parentKey = parent.parentUnit,
            sovereignKey = sovereignKey)) true
    else false
}

private fun isFlagAnnexedChild(
    flag: FlagResources,
    sovereignKey: String
): Boolean =
    flag in annexedFlagResMap.values && flag.parentUnit != null &&
            isSovereignKeyFromParentKey(parentKey = flag.parentUnit, sovereignKey)

fun getChronologicalDirectRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    return if (flagRes.previousFlagOf != null) {
        emptyList()

    } else {
        val latestKeys = buildList {
            add(flagKey)
            addAll(elements = flagRes.latestEntities)
        }

        val childKeys = flagResMap.values.filter { flag ->
            flag.latestEntities.any { it in latestKeys } || flag.previousFlagOf == flagKey ||
                    /* Historical unit expression */
                    ((flag.sovereignState == flagKey || flag.parentUnit == flagKey) &&
                    HISTORICAL in flag.categories &&
                    HISTORICAL !in flagRes.categories &&
                    UNRECOGNIZED_STATE !in flag.categories)
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }

        /* Return expression */
        buildList {
            addAll(elements = latestKeys)
            addAll(elements = childKeys)
        }.distinct()
    }
}

fun getChronologicalIndirectRelatedFlagKeys(flagRes: FlagResources): List<String> {
    return if (flagRes.previousFlagOf != null ||
        flagRes.sovereignState in flagRes.latestEntities) {
        emptyList()

    } else {
        flagResMap.values.filter { flag ->
             flagRes.sovereignState in flag.latestEntities ||
                     flag.sovereignState in flagRes.latestEntities
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.distinct()
    }
}

enum class ExternalCategoryExceptions(val key: String) {
    /* For flags with any externalCategories as internal units */
    ITALY (key = "italy"),
    FRANCE (key = "france")
}
val extCatExceptions = ExternalCategoryExceptions.entries.map { it.key }
val externalCategories = listOf(TERRITORY, REGION, COLONY, UNRECOGNIZED_STATE)
val internalCategories = listOf(SovereignCountry.enums(), Regional.enums(), Institution.allEnums())
    .flatten().filterNot { it in externalCategories } + CONFEDERATION + POLITICAL_MOVEMENT

fun getPoliticalInternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    return if (flagRes.previousFlagOf != null) {
        emptyList()

    } else {
        val sovereignKey = flagRes.sovereignState ?: flagKey

        val childKeys = flagResMap.values.filter { flag ->
            flag.sovereignState == sovereignKey || flagKey in flag.internationalOrganisations ||
                    isFlagAnnexedChild(flag, sovereignKey)
        }.filterNot { flag ->
            /* Exclude non-internal (external) categories unless in exceptions */
            (flag.categories.none { it in internalCategories } &&
                    flag.sovereignState !in extCatExceptions) ||

                    /* Exclude historic institutions when sovereign non-historic */
                    (HISTORICAL in flag.categories && flag.sovereignState != null &&
                    HISTORICAL !in flagResMap.getValue(flag.sovereignState).categories &&
                    flag.categories.any { it in Institution.allEnums() + POLITICAL_MOVEMENT })
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }

        /* Return expression */
        buildList {
            add(flagKey)
            add(sovereignKey)
            addAll(elements = childKeys)
        }.distinct()
    }
}

fun getPoliticalExternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    return if (flagRes.previousFlagOf != null) {
        emptyList()

    } else {
        val sovereignKey = flagRes.sovereignState ?: flagKey

        val internationalOrgKeys = buildList {
            addAll(elements = flagRes.internationalOrganisations)
        }

        val childKeys = flagResMap.values.filter { flag ->
            val isSovChild =
                flag.sovereignState == sovereignKey || isFlagAnnexedChild(flag, sovereignKey)

            flag.associatedState == flagKey || (isSovChild &&
                    externalCategories.any { it in flag.categories } &&
                    flag.sovereignState !in extCatExceptions)
        }.filterNot { flag ->
            /* Exclude historical children when parent is not historical */
            HISTORICAL in flag.categories &&
                    HISTORICAL !in flagRes.categories &&
                    flag.sovereignState == flagKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }

        /* Return expression */
        buildList {
            flagRes.associatedState?.let { add(it) }
            addAll(elements = internationalOrgKeys)
            addAll(elements = childKeys)
        }.distinct()
    }
}

fun <T> getBooleanExplicitOrInherit(
    flagKey: String,
    flagRes: FlagResources,
    prop: (FlagResources) -> T,
    propName: String,
): Boolean {
    val boolSrc = prop(flagRes)

    return when (boolSrc) {
        is BooleanSource.Explicit -> boolSrc.bool
        is BooleanSource.Inherit ->
            flagRes.previousFlagOf?.let { parentKey ->
                flagResMap.getValue(parentKey).let { parentFlagRes ->
                    val parentBoolSrc = prop(parentFlagRes)

                    when (parentBoolSrc) {
                        is BooleanSource.Explicit -> parentBoolSrc.bool
                        is BooleanSource.Inherit -> error("$flagKey & $parentKey: No $propName")
                        else -> error("flagResProp not StringResSource type")
                    }
                }
            } ?: error("$flagKey has no previousFlagOf key")
        else -> error("flagResProp not BooleanSource type")
    }
}

fun <T> getStringResExplicitOrInherit(
    flagKey: String,
    flagRes: FlagResources,
    prop: (FlagResources) -> T,
    propName: String,
    context: Context,
): Int {
    val strResSrc = prop(flagRes)

    return when (strResSrc) {
        is StringResSource.Explicit -> strResSrc.resName.resId(context)
        is StringResSource.Inherit? ->
            flagRes.previousFlagOf?.let { parentKey ->
                flagResMap.getValue(parentKey).let { parentFlagRes ->
                    val parentStrResSrc = prop(parentFlagRes)

                    when (parentStrResSrc) {
                        is StringResSource.Explicit -> parentStrResSrc.resName.resId(context)
                        is StringResSource.Inherit -> error("$flagKey & $parentKey: No $propName")
                        else -> error("flagResProp not StringResSource type")
                    }
                }
            } ?: error("$flagKey has no previousFlagOf key")
        else -> error("flagResProp not StringResSource type")
    }
}

fun <T> getStringResExplicitOrInheritOrNull(
    flagKey: String,
    flagRes: FlagResources,
    prop: (FlagResources) -> T,
    propName: String,
    context: Context,
): Int? {
    val strResSrc = prop(flagRes)

    return when (strResSrc) {
        null -> null
        is StringResSource.Explicit -> strResSrc.resName.resId(context)
        is StringResSource.Inherit ->
            flagRes.previousFlagOf?.let { parentKey ->
                flagResMap.getValue(parentKey).let { parentFlagRes ->
                    val parentStrResSrc = prop(parentFlagRes)

                    when (parentStrResSrc) {
                        null -> null
                        is StringResSource.Explicit -> parentStrResSrc.resName.resId(context)
                        is StringResSource.Inherit -> error("$flagKey & $parentKey: No $propName")
                        else -> error("flagResProp not StringResSource type")
                    }
                }
            }
        else -> error("flagResProp not StringResSource type")
    }
}

fun getFlagOfAlternativeResIds(
    flagKey: String,
    flagRes: FlagResources,
    context: Context,
): List<Int> = buildList {
    val explicitList = flagRes.flagOfAlternate.filterIsInstance<StringResSource.Explicit>()
        .map { it.resName.resId(context) }

    val inheritList = when (StringResSource.Inherit) {
        in flagRes.flagOfAlternate ->
            flagRes.previousFlagOf?.let { parentKey ->
                flagResMap.getValue(parentKey).let { parentFlagRes ->
                    parentFlagRes.flagOfAlternate.filterIsInstance<StringResSource.Explicit>()
                        .map { it.resName.resId(context) }
                }
            } ?: error("$flagKey has no previousFlagOf key")
        else -> emptyList()
    }

    addAll(elements = explicitList)
    addAll(elements = inheritList)
}

/* ------------------------------------------ */


/* ---------- For related flags (eg. RelatedFlagsMenu) ---------- */

fun getLatestFlag(flag: FlagView): FlagView =
    flag.previousFlagOfKey?.let { flagViewMap.getValue(it) } ?: flag

fun getLatestFlagKey(flag: FlagView): String =
    flag.previousFlagOfKey ?: inverseFlagViewMap.getValue(flag)

val perLevelAdminDivisionOrder = listOf(
    STATE,
    REGION,
    COUNTRY,
    COLLECTIVITY,
    COMMUNITY,
    PROVINCE,
    TERRITORY,
    REGIONAL,
    ISLAND,
    CANTON,
    COUNCIL_AREA,
    DISTRICT,
    OBLAST,
    KRAI,
    OKRUG,
    REPUBLIC_UNIT,
    COUNTY,
    MUNICIPALITY,
    COLONY,
    RIDING,
    CITY,
    CONFEDERATION,
    MICRONATION
    //EMIRATE,
    //ENTITY,
    //GOVERNORATE,
    //MEMBER_STATE,
)

private fun getAdminDivisionUnitLevelFromUnits(units: List<FlagView>): Int {
    var maxUnitLevel = 1

    units.forEach { unit ->
        val unitLevel = getAdminDivisionUnitLevel(unit)
        if (unitLevel > maxUnitLevel) maxUnitLevel = unitLevel
    }

    return maxUnitLevel
}

private fun getAdminDivisionUnitLevel(
    unit: FlagView,
    unitLevel: Int = 1,
): Int {
    return when (unit.parentUnitKey) {
        null -> unitLevel
        else -> {
            val newUnitLevel = unitLevel.inc()
            val parentUnit = flagViewMap.getValue(unit.parentUnitKey)

            when (parentUnit.parentUnitKey) {
                null -> newUnitLevel
                else -> getAdminDivisionUnitLevel(parentUnit, newUnitLevel)
            }
        }
    }
}

fun getChronologicalRelatedFlagsContentOrNull(
    sourceFlag: FlagView,
    application: Application,
): RelatedFlagsContent.Chronological? {
    val flag = getLatestFlag(sourceFlag)

    return if (!flag.isChronologicalRelatedFlags()) {
        null

    } else {
        val directRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.chronologicalDirectRelatedFlagKeys)
        )
        val indirectRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.chronologicalIndirectRelatedFlagKeys)
        )

        val historicalFlags = directRelatedFlags.filter { flag ->
            flag.previousFlagOfKey != null
        }.sortedBy { it.id }

        val historicalUnits = directRelatedFlags.filter { flag ->
            val latestKeys = directRelatedFlags.filter { it.latestEntityKeys.isEmpty() }
                .map { inverseFlagViewMap.getValue(it) }
            val isChild = flag.sovereignStateKey != null || flag.parentUnitKey != null

            HISTORICAL in flag.categories && UNRECOGNIZED_STATE !in flag.categories &&
                    isChild &&
                    flag.latestEntityKeys.none { it in latestKeys } &&
                    flag !in historicalFlags
        }

        val latestEntities = directRelatedFlags.filter { flag ->
            flag.latestEntityKeys.isEmpty() && flag !in historicalFlags + historicalUnits
        }

        val previousEntities = directRelatedFlags.filter { flag ->
            flag.latestEntityKeys.isNotEmpty() && flag !in historicalUnits
        }.sortedByDescending { it.fromYear }

        val dependentsOfLatest = indirectRelatedFlags.filter { flag ->
            latestEntities.any { flag.sovereignStateKey == inverseFlagViewMap.getValue(it) }
        }

        val previousEntitiesOfSovereign = indirectRelatedFlags.filterNot { indirectFlag ->
            indirectFlag in dependentsOfLatest
        }.sortedByDescending { it.fromYear }

        RelatedFlagsContent.Chronological(
            historicalFlags = if (historicalFlags.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = historicalFlags,
                    category = RelatedFlagsCategory.HISTORICAL_FLAGS.title,
                    categoryKey = RelatedFlagsCategory.HISTORICAL_FLAGS.name
                )
            },
            latestEntities = when (latestEntities.isEmpty() to historicalUnits.isEmpty()) {
                true to true -> null
                true to false -> {
                    val parentSovList = buildList {
                        historicalUnits.forEach { unit ->
                            unit.parentUnitKey?.let { add(flagViewMap.getValue(it)) }
                                ?: unit.sovereignStateKey?.let { add(flagViewMap.getValue(it)) }
                        }
                    }
                    val latestEntitiesType =
                        if (parentSovList.size == 1) RelatedFlagsCategory.LATEST_ENTITIES_POLITY
                        else RelatedFlagsCategory.LATEST_ENTITIES_POLITIES

                    /* Return expression */
                    if (parentSovList.isEmpty()) null else {
                        RelatedFlagGroup.Multiple(
                            flags = parentSovList,
                            category = latestEntitiesType.title,
                            categoryKey = latestEntitiesType.name,
                        )
                    }
                }
                else -> {
                    val nonPolityCategories = Institution.allEnums() + Cultural.allEnums()
                    val latestEntitiesType =
                        if (latestEntities.any { entity ->
                                entity.categories.any { it in nonPolityCategories } }) {
                            RelatedFlagsCategory.LATEST_ENTITY_NON_POLITY
                        } else if (latestEntities.size == 1) {
                            RelatedFlagsCategory.LATEST_ENTITIES_POLITY
                        } else {
                            RelatedFlagsCategory.LATEST_ENTITIES_POLITIES
                        }

                    /* Return expression */
                    RelatedFlagGroup.Multiple(
                        flags = latestEntities,
                        category = latestEntitiesType.title,
                        categoryKey = latestEntitiesType.name,
                    )
                }
            },
            previousEntities = if (previousEntities.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = previousEntities,
                    category = RelatedFlagsCategory.PREVIOUS_ENTITIES_POLITY.title,
                    categoryKey = RelatedFlagsCategory.PREVIOUS_ENTITIES_POLITY.name,
                )
            },
            historicalUnits = if (historicalUnits.isEmpty()) null else {
                val historicalUnitType =
                    if (latestEntities.isNotEmpty()) RelatedFlagsCategory.HISTORICAL_UNITS
                    else RelatedFlagsCategory.HISTORICAL_UNIT_SELECTED

                /* Return expression */
                RelatedFlagGroup.Multiple(
                    flags = historicalUnits,
                    category = historicalUnitType.title,
                    categoryKey = historicalUnitType.name,
                )
            },
            previousEntitiesOfSovereign = if (previousEntitiesOfSovereign.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = previousEntitiesOfSovereign,
                    category = RelatedFlagsCategory.PREVIOUS_ENTITIES_OF_SOVEREIGN.title,
                    categoryKey = RelatedFlagsCategory.PREVIOUS_ENTITIES_OF_SOVEREIGN.name,
                )
            },
            dependentsOfLatest = if (dependentsOfLatest.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = dependentsOfLatest,
                    category = RelatedFlagsCategory.DEPENDENTS_OF_LATEST_ENTITY.title,
                    categoryKey = RelatedFlagsCategory.DEPENDENTS_OF_LATEST_ENTITY.name,
                )
            },
        )
    }
}

fun getPoliticalRelatedFlagsContentOrNull(
    sourceFlag: FlagView,
    application: Application,
): RelatedFlagsContent.Political? {
    val flag = getLatestFlag(sourceFlag)
    val flagKey = getLatestFlagKey(sourceFlag)

    return if (!flag.isPoliticalRelatedFlags()) {
        null

    } else if (flag.categories.any { it in International.enums() }) {
        val internalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
        )
        val internationalOrgMembers = internalRelatedFlags.filterNot { it == flag }

        RelatedFlagsContent.Political(
            sovereign = null,
            adminUnits = null,
            externalTerritories = null,
            statesLimitedRecognition = null,
            institutions = null,
            associatedStates = null,
            internationalOrgs = RelatedFlagGroup.Multiple(
                flags = listOf(flag),
                category = INTERNATIONAL_ORGANIZATION.title,
                categoryKey = INTERNATIONAL_ORGANIZATION.name,
            ),
            internationalOrgMembers = if (internationalOrgMembers.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = internationalOrgMembers,
                    category = MEMBER_STATE.title,
                    categoryKey = MEMBER_STATE.name,
                )
            },
        )
    } else {
        val internalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
        )
        val externalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)
        )

        val sovereign = internalRelatedFlags.firstOrNull { flag ->
            SOVEREIGN_STATE in flag.categories
        }

        val institutions = internalRelatedFlags.filter { flag ->
            flag.categories.any { it in Institution.allEnums() + POLITICAL_MOVEMENT }
        }

        val legislatureInstitutions = institutions.filter { flag ->
            flag.categories.any { it in Legislature.enums() }
        }
        val executiveInstitutions = institutions.filter { flag ->
            flag.categories.any { it in Executive.enums() }
        }
        val civilianInstitutions = institutions.filter { flag ->
            flag.categories.any { it in Civilian.enums() }
        }
        val politicalMovements = institutions.filter { flag ->
            flag.categories.any { it == POLITICAL_MOVEMENT }
        }

        val associatedStates = externalRelatedFlags.filter { flag ->
            flag.associatedStateKey?.let { flagViewMap.getValue(it) } == flag ||
                    flag.associatedStateKey == flagKey
        }

        val internationalOrgs = externalRelatedFlags.filter { flag ->
            flag.categories.any { it in International.enums() }
        }

        val statesLimitedRecognition = externalRelatedFlags.filter { flag ->
            UNRECOGNIZED_STATE in flag.categories
        }
        val externalTerritories = externalRelatedFlags.filterNot { flag ->
            flag in listOf(
                associatedStates, internationalOrgs, institutions, statesLimitedRecognition
            ).flatten()
        }.sortedWith { p1, p2 ->
            when {
                AUTONOMOUS_REGION in p1.categories && AUTONOMOUS_REGION !in p2.categories -> -1
                AUTONOMOUS_REGION !in p1.categories && AUTONOMOUS_REGION in p2.categories -> 1
                else -> 0
            }
        }

        val adminUnits = internalRelatedFlags.filterNot { it == sovereign || it in institutions }

        val flagInternalCategories =
            if (flagKey in extCatExceptions || flag.sovereignStateKey in extCatExceptions) {
                internalCategories + externalCategories
            } else {
                internalCategories
            }

        RelatedFlagsContent.Political(
            sovereign = sovereign?.let { sovereign ->
                RelatedFlagGroup.Single(
                    flag = sovereign,
                    category = SOVEREIGN_STATE.title,
                    categoryKey = SOVEREIGN_STATE.name,
                )
            },
            adminUnits = if (adminUnits.isEmpty()) null else {
                buildList {
                    val adminDivisions = adminUnits.flatMap { it.categories }
                        .filter { it in flagInternalCategories }
                        .distinct()

                    adminDivisions.forEach { division ->
                        val divisionUnits = adminUnits.filter { flag ->
                            division in flag.categories
                        }
                        val divisionExclusiveUnits = divisionUnits.filter { flag ->
                            flag.categories.all { it in listOf(division, HISTORICAL) }
                        }
                        val divisionUnitLevel = getAdminDivisionUnitLevelFromUnits(
                            units = divisionExclusiveUnits.ifEmpty { divisionUnits }
                        )

                        add(
                            RelatedFlagGroup.AdminUnits(
                                flags = divisionUnits,
                                unitLevel = divisionUnitLevel,
                                unitCategory = division,
                                category = division.title,
                                categoryKey = division.name,
                            )
                        )
                    }
                }.sortedBy { perLevelAdminDivisionOrder.indexOf(it.unitCategory) }
            },
            externalTerritories = if (externalTerritories.isEmpty()) null else {
                val category = externalTerritories.first().categories
                    .first { it in externalCategories }

                RelatedFlagGroup.Multiple(
                    flags = externalTerritories,
                    category = category.title,
                    categoryKey = category.name,
                )
            },
            statesLimitedRecognition = if (statesLimitedRecognition.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = statesLimitedRecognition,
                    category = UNRECOGNIZED_STATE.title,
                    categoryKey = UNRECOGNIZED_STATE.name,
                )
            },
            institutions = if (institutions.isEmpty()) null else buildList {
                if (legislatureInstitutions.isNotEmpty()) {
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = legislatureInstitutions,
                            category = Legislature.title,
                            categoryKey = "LEGISLATURE_INSTITUTIONS",
                        )
                    )
                }
                if (executiveInstitutions.isNotEmpty()) {
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = executiveInstitutions,
                            category = Executive.title,
                            categoryKey = "EXECUTIVE_INSTITUTIONS",
                        )
                    )
                }
                if (civilianInstitutions.isNotEmpty()) {
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = civilianInstitutions,
                            category = Civilian.title,
                            categoryKey = "CIVILIAN_INSTITUTIONS",
                        )
                    )
                }
                if (politicalMovements.isNotEmpty()) {
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = politicalMovements,
                            category = POLITICAL_MOVEMENT.title,
                            categoryKey = POLITICAL_MOVEMENT.name,
                        )
                    )
                }
            },
            associatedStates = if (associatedStates.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = associatedStates,
                    category = FREE_ASSOCIATION.title,
                    categoryKey = FREE_ASSOCIATION.name,
                )
            },
            internationalOrgs = if (internationalOrgs.isEmpty()) null else {
                RelatedFlagGroup.Multiple(
                    flags = internationalOrgs,
                    category = INTERNATIONAL_ORGANIZATION.title,
                    categoryKey = INTERNATIONAL_ORGANIZATION.name,
                )
            },
            internationalOrgMembers = null,
        )
    }
}

fun getRelatedFlagItems(
    relatedFlagsContent: RelatedFlagsContent
): List<LazyColumnItem> = relatedFlagsContent.groups.flatMap { group ->
    buildList {
        add(
            LazyColumnItem.Header(
                title = group.category,
                keyTag = group.categoryKey,
            )
        )
        when (group) {
            is RelatedFlagGroup.Single -> add(
                LazyColumnItem.Flag(
                    flag = group.flag,
                    keyTag = group.categoryKey,
                )
            )
            is RelatedFlagGroup.Multiple -> group.flags.forEach { flag ->
                add(
                    LazyColumnItem.Flag(
                        flag = flag,
                        keyTag = group.categoryKey,
                    )
                )
            }
            is RelatedFlagGroup.AdminUnits -> group.flags.forEach { flag ->
                add(
                    LazyColumnItem.Flag(
                        flag = flag,
                        keyTag = group.categoryKey,
                    )
                )
            }
        }
    }
}

fun getPoliticalRelatedFlags(flag: FlagView?): List<FlagView> = buildList {
    flag?.let {
        add(flag)
        addAll(elements =
            getFlagsFromKeys(flagKeys =
                flag.politicalInternalRelatedFlagKeys + flag.politicalExternalRelatedFlagKeys
            )
        )
    }
}