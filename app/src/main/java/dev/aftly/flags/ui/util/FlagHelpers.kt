package dev.aftly.flags.ui.util

import android.app.Application
import android.content.Context
import dev.aftly.flags.data.DataSource.NAV_SEPARATOR
import dev.aftly.flags.data.DataSource.flagResMap
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.flagViewMapId
import dev.aftly.flags.data.DataSource.inverseFlagResMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.BooleanSource
import dev.aftly.flags.model.FlagCategory.SOVEREIGN_STATE
import dev.aftly.flags.model.FlagCategory.AUTONOMOUS_REGION
import dev.aftly.flags.model.FlagCategory.FREE_ASSOCIATION
import dev.aftly.flags.model.FlagCategory.INTERNATIONAL_ORGANIZATION
import dev.aftly.flags.model.FlagCategory.MEMBER_STATE
import dev.aftly.flags.model.FlagCategory.OBLAST
import dev.aftly.flags.model.FlagCategory.CANTON
import dev.aftly.flags.model.FlagCategory.COLLECTIVITY
import dev.aftly.flags.model.FlagCategory.COLONY
import dev.aftly.flags.model.FlagCategory.COMMUNITY
import dev.aftly.flags.model.FlagCategory.COUNTRY
import dev.aftly.flags.model.FlagCategory.COUNTY
import dev.aftly.flags.model.FlagCategory.CITY
import dev.aftly.flags.model.FlagCategory.DISTRICT
import dev.aftly.flags.model.FlagCategory.KRAI
import dev.aftly.flags.model.FlagCategory.MUNICIPALITY
import dev.aftly.flags.model.FlagCategory.OKRUG
import dev.aftly.flags.model.FlagCategory.PROVINCE
import dev.aftly.flags.model.FlagCategory.REGION
import dev.aftly.flags.model.FlagCategory.REPUBLIC_UNIT
import dev.aftly.flags.model.FlagCategory.STATE
import dev.aftly.flags.model.FlagCategory.TERRITORY
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.RelatedFlagGroup
import dev.aftly.flags.model.RelatedFlagsCategory
import dev.aftly.flags.model.RelatedFlagsContent
import dev.aftly.flags.model.StringResSource

fun getFlagKey(flag: FlagView): String = inverseFlagViewMap.getValue(key = flag)

fun getFlagKeys(flags: List<FlagView>): List<String> =
    flags.map { inverseFlagViewMap.getValue(key = it) }

fun getFlagsFromKeys(flagKeys: List<String>): List<FlagView> =
    flagKeys.map { flagViewMap.getValue(key = it) }

fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagView>,
): List<FlagView> {
    val appResources = application.applicationContext.resources

    return flags.sortedBy { flag ->
        normalizeString(string = appResources.getString(flag.flagOf))
    }
}


fun SavedFlag.getFlagView(): FlagView = flagViewMap.getValue(key = this.flagKey)


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
fun getChronologicalDirectRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val latestParentKeys = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
        addAll(elements = flagRes.latestEntities)
    }

    val previousParentKeys = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
    }

    val childKeys = flagResMap.values.filter { flag ->
         flag.latestEntities.any { it in latestParentKeys } || flag
             .previousFlagOf in previousParentKeys
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }

    return buildList {
        addAll(elements = latestParentKeys)
        addAll(elements = previousParentKeys)
        addAll(elements = childKeys)
    }.distinct()
}

fun getChronologicalIndirectRelatedFlagKeys(
    flagRes: FlagResources,
): List<String> {
    val latestFilterKeys = buildList {
        /* For all (direct & primary) historical admin flags of the sovereign */
        flagRes.sovereignState?.let { add(it) }
        /* For pre-admin keys of parent's sovereign (eg. USA for historical flag of a state) */
        flagRes.previousFlagOf?.let { flagOfKey ->
            flagResMap.getValue(flagOfKey).sovereignState?.let { add(it) }
        }
    }

    val sovereignFilterKeys = buildList {
        /* For all dependents of latest entity */
        addAll(elements = flagRes.latestEntities)
        /* For post-admin keys of parent entity (eg. primary USA for historical USA flags) */
        flagRes.previousFlagOf?.let { flagOfKey ->
            addAll(elements = flagResMap.getValue(flagOfKey).latestEntities)
        }
    }

    return flagResMap.values.filter { flag ->
        flag.latestEntities.any { it in latestFilterKeys } || flag
            .sovereignState in sovereignFilterKeys
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct()
}

enum class ExternalCategoryExceptions(val key: String) {
    /* For flags with any externalCategories as internal units */
    ITALY (key = "italy"),
    FRANCE (key = "france")
}
val extCatExceptions = ExternalCategoryExceptions.entries.map { it.key }
val externalCategories = listOf(TERRITORY, REGION, COLONY)
val internalCategories =
    (FlagSuperCategory.Regional.enums() + FlagSuperCategory.SovereignCountry.enums())
        .filterNot { it in externalCategories }

fun getPoliticalInternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val parentKeys = buildList {
        add(flagKey)
        flagRes.sovereignState?.let { add(it) }
        flagRes.previousFlagOf?.let { add(it) }
    }

    val childKeys = flagResMap.values.filter { flag ->
        flag.sovereignState in parentKeys || flagKey in flag.internationalOrganisations
    }.filterNot { flag ->
        flag.categories.none { it in internalCategories } && flag.sovereignState !in extCatExceptions
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }

    return buildList {
        addAll(
            elements = parentKeys.filterNot { key ->
                /* Exclude non-internal regional flags (some flags are both int and ext) */
                key == flagKey && flagRes.categories.none { it in internalCategories }
            }
        )
        addAll(elements = childKeys)
    }.distinct()
}

fun getPoliticalExternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val associatedKeys = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
    }

    val sovereignKeys = buildList {
        add(flagKey)
        flagRes.sovereignState?.let { add(it) }
        flagRes.previousFlagOf?.let { add(it) }
    }

    val internationalOrgKeys = buildList {
        addAll(flagRes.internationalOrganisations)
    }

    val childKeys = flagResMap.values.filter { flag ->
        flag.associatedState in associatedKeys || (flag.sovereignState in sovereignKeys &&
                externalCategories.any { it in flag.categories } &&
                flag.sovereignState !in extCatExceptions)
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }

    return buildList {
        flagRes.associatedState?.let { add(it) }
        flagRes.previousFlagOf?.let { flagOfKey ->
            flagResMap.getValue(flagOfKey).associatedState?.let { add(it) }
        }
        addAll(internationalOrgKeys)
        addAll(childKeys)
    }.distinct()
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
        is StringResSource.Inherit ->
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

    addAll(explicitList)
    addAll(inheritList)
}

/* ------------------------------------------ */


/* ---------- For RelatedFlagsMenu ---------- */
val perLevelAdminDivisionOrder = listOf(
    STATE,
    REGION,
    COUNTRY,
    COLLECTIVITY,
    COMMUNITY,
    PROVINCE,
    TERRITORY,
    CANTON,
    DISTRICT,
    OBLAST,
    KRAI,
    CITY,
    OKRUG,
    REPUBLIC_UNIT,
    COUNTY,
    MUNICIPALITY,
    COLONY
    //EMIRATE,
    //ENTITY,
    //GOVERNORATE,
    //ISLAND,
    //MEMBER_STATE,
)
fun getPoliticalRelatedFlagsContentOrNull(
    flag: FlagView,
    application: Application,
): RelatedFlagsContent.Political? {
    if (!flag.isPoliticalRelatedFlags) return null

    val isFlagInternationalOrg = flag.categories.any {
        it in FlagSuperCategory.International.enums()
    }

    return if (isFlagInternationalOrg) {
        val politicalInternalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
        )

        val internationalOrgMembers = politicalInternalRelatedFlags.filterNot { it == flag }

        RelatedFlagsContent.Political(
            sovereign = null,
            adminUnits = null,
            externalTerritories = null,
            associatedStates = null,
            internationalOrgs = RelatedFlagGroup.Multiple(
                flags = listOf(flag),
                category = INTERNATIONAL_ORGANIZATION.title,
                categoryKey = INTERNATIONAL_ORGANIZATION.name,
            ),
            internationalOrgMembers = when (internationalOrgMembers.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = internationalOrgMembers,
                    category = MEMBER_STATE.title,
                    categoryKey = MEMBER_STATE.name,
                )
            },
        )
    } else {
        val flagKey = when (flag.previousFlagOfKey) {
            null -> inverseFlagViewMap.getValue(flag)
            else -> flag.previousFlagOfKey
        }
        val politicalInternalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
        )
        val politicalExternalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)
        )

        val sovereign = politicalInternalRelatedFlags.firstOrNull { internalFlag ->
            internalFlag.categories.contains(SOVEREIGN_STATE)
        }

        val associatedStates = politicalExternalRelatedFlags.filter { relatedFlag ->
            flag.associatedStateKey?.let { flagViewMap.getValue(it) } == relatedFlag ||
                    relatedFlag.associatedStateKey == flagKey
        }

        val internationalOrgs = politicalExternalRelatedFlags.filter { relatedFlag ->
            relatedFlag.categories.any { it in FlagSuperCategory.International.enums() }
        }

        val externalTerritories = politicalExternalRelatedFlags.filterNot { relatedFlag ->
            relatedFlag in associatedStates || relatedFlag in internationalOrgs
        }.sortedWith { p1, p2 ->
            when {
                AUTONOMOUS_REGION in p1.categories && AUTONOMOUS_REGION !in p2.categories -> -1
                AUTONOMOUS_REGION !in p1.categories && AUTONOMOUS_REGION in p2.categories -> 1
                else -> 0
            }
        }

        val adminUnits = politicalInternalRelatedFlags.filterNot { it == sovereign }

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
            adminUnits = when (adminUnits.isEmpty()) {
                true -> null
                false -> buildList {
                    val adminDivisions = adminUnits.flatMap { it.categories }
                        .filter { it in flagInternalCategories }
                        .distinct()

                    adminDivisions.forEach { division ->
                        val divisionUnits = adminUnits.filter { flag ->
                            division in flag.categories
                        }
                        val divisionUnitLevel = divisionUnits.let { units ->
                            var maxUnitLevel = 1
                            units.forEach { unit ->
                                var unitLevel = 1
                                var parentUnit = unit

                                while (parentUnit.parentUnitKey != null) {
                                    unitLevel++
                                    parentUnit =
                                        flagViewMap.getValue(parentUnit.parentUnitKey)
                                }
                                if (unitLevel > maxUnitLevel) maxUnitLevel = unitLevel
                            }
                            return@let maxUnitLevel
                        }

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
            externalTerritories = when (externalTerritories.isEmpty()) {
                true -> null
                false -> {
                    val category = externalTerritories.first().categories
                        .first { it in externalCategories }

                    RelatedFlagGroup.Multiple(
                        flags = externalTerritories,
                        category = category.title,
                        categoryKey = category.name,
                    )
                }
            },
            associatedStates = when (associatedStates.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = associatedStates,
                    category = FREE_ASSOCIATION.title,
                    categoryKey = FREE_ASSOCIATION.name,
                )
            },
            internationalOrgs = when (internationalOrgs.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = internationalOrgs,
                    category = INTERNATIONAL_ORGANIZATION.title,
                    categoryKey = INTERNATIONAL_ORGANIZATION.name,
                )
            },
            internationalOrgMembers = null,
        )
    }
}

fun getChronologicalRelatedFlagsContentOrNull(
    flag: FlagView,
    application: Application,
): RelatedFlagsContent.Chronological? {
    return if (!flag.isChronologicalRelatedFlags) {
        null
    } else {
        val chronologicalDirectRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.chronologicalDirectRelatedFlagKeys)
        )
        val chronologicalIndirectRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.chronologicalIndirectRelatedFlagKeys)
        )

        val latestEntities = chronologicalDirectRelatedFlags.filter { directFlag ->
            directFlag.latestEntityKeys.isEmpty() && directFlag.previousFlagOfKey == null
        }

        val previousEntities = chronologicalDirectRelatedFlags.filter { directFlag ->
            directFlag.latestEntityKeys.isNotEmpty()
        }.sortedByDescending { it.fromYear }

        val historicalFlags = chronologicalDirectRelatedFlags.filter { directFlag ->
            directFlag.previousFlagOfKey != null
        }.sortedBy { it.id }

        val dependentsOfLatest = chronologicalIndirectRelatedFlags.filter { indirectFlag ->
            latestEntities.any {
                indirectFlag.sovereignStateKey == inverseFlagViewMap.getValue(it)
            }
        }

        val previousEntitiesOfSovereign = chronologicalIndirectRelatedFlags.filterNot {
            indirectFlag -> indirectFlag in dependentsOfLatest
        }

        RelatedFlagsContent.Chronological(
            latestEntities = when (latestEntities.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = latestEntities,
                    category = RelatedFlagsCategory.LATEST_ENTITIES.title,
                    categoryKey = RelatedFlagsCategory.LATEST_ENTITIES.name,
                )
            },
            previousEntities = when (previousEntities.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = previousEntities,
                    category = RelatedFlagsCategory.PREVIOUS_ENTITIES.title,
                    categoryKey = RelatedFlagsCategory.PREVIOUS_ENTITIES.name,
                )
            },
            historicalFlags = when (historicalFlags.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = historicalFlags,
                    category = RelatedFlagsCategory.HISTORICAL_FLAGS.title,
                    categoryKey = RelatedFlagsCategory.HISTORICAL_FLAGS.name
                )
            },
            previousEntitiesOfSovereign = when (previousEntitiesOfSovereign.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = previousEntitiesOfSovereign,
                    category = RelatedFlagsCategory.PREVIOUS_ENTITIES_OF_SOVEREIGN.title,
                    categoryKey = RelatedFlagsCategory.PREVIOUS_ENTITIES_OF_SOVEREIGN.name,
                )
            },
            dependentsOfLatest = when (dependentsOfLatest.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = dependentsOfLatest,
                    category = RelatedFlagsCategory.DEPENDENTS_OF_LATEST_ENTITY.title,
                    categoryKey = RelatedFlagsCategory.DEPENDENTS_OF_LATEST_ENTITY.name,
                )
            },
        )
    }
}