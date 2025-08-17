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
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.RelatedFlagGroup
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
enum class ExternalCategoryExceptions(val key: String) {
    /* For flags with any externalCategories as internal units */
    ITALY (key = "italy")
}
val extCatExceptions = ExternalCategoryExceptions.entries.map { it.key }
val externalCategories = listOf(FlagCategory.TERRITORY, FlagCategory.REGION)

fun getChronologicalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val previousFilterValues = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
    }
    val latestFilterValues = buildList {
        add(flagKey)
        flagRes.latestEntity?.let { add(it) }
    }

    return flagResMap.values.filter { flag ->
        flag.previousFlagOf in previousFilterValues || flag.latestEntity in latestFilterValues
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct().filterNot { key ->
        key == flagKey
    }
}

fun getPoliticalInternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val filterValues = buildList {
        add(flagKey)
        flagRes.sovereignState?.let { add(it) }
        flagRes.previousFlagOf?.let { add(it) }
    }

    return flagResMap.values.filter { flag ->
        flag.sovereignState in filterValues
    }.filterNot { flag ->
        externalCategories.any { it in flag.categories } && flag.sovereignState !in extCatExceptions
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct().filterNot { key ->
        key == flagKey
    }
}

fun getPoliticalExternalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    return buildList {
        flagRes.associatedState?.let { add(it) }

        addAll(elements =
            flagResMap.values.filter { flag ->
                flag.associatedState == flagKey || (externalCategories.any {
                    it in flag.categories } && flag.sovereignState !in extCatExceptions)
            }.map { flag ->
                inverseFlagResMap.getValue(flag)
            }
        )
    }
}

fun getChronologicalAdminsOfParentKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val latestFilterValues = buildList {
        /* For all (direct & primary) historical admin flags of the sovereign */
        flagRes.sovereignState?.let { add(it) }
        /* For pre-admin keys of parent entity (eg. primary USA for previous USA flags) */
        flagRes.previousFlagOf?.let { add(it) }
    }

    val sovereignFilterValues = buildList {
        /* For all dependents of latest entity */
        flagRes.latestEntity?.let { add(it) }
        /* For post-admin keys of parent entity (eg. primary USA for previous USA flags) */
        flagRes.previousFlagOf?.let {
            flagResMap.getValue(it).latestEntity?.let { flagOfLatestEntity ->
                add(flagOfLatestEntity)
            }
        }
    }

    return flagResMap.values.filter { flag ->
        flag.latestEntity in latestFilterValues || flag.sovereignState in sovereignFilterValues
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct().filterNot { key ->
        key == flagKey
    }
}

fun getFlagNameResIds(
    flagKey: String,
    flagRes: FlagResources,
    context: Context,
): List<Int> {
    val flagOf = when (flagRes.flagOf) {
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
    }
    val flagOfOfficial = when (flagRes.flagOfOfficial) {
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
    }

    val primaryNames = listOf(flagOf, flagOfOfficial)

    /* Transform expression */
    return flagRes.flagOfAlternate?.map { it.resId(context) }?.let { secondaryNames ->
        primaryNames + secondaryNames
    } ?: primaryNames
}


/* ------------------------------------------ */


/* ---------- For RelatedFlagsMenu ---------- */
fun getPoliticalRelatedFlagsContent(
    flag: FlagView,
    application: Application,
): RelatedFlagsContent.Political {
    val flagKey = inverseFlagViewMap.getValue(flag)
    val politicalInternalRelatedFlags = sortFlagsAlphabetically(
        application = application,
        flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
    )
    val politicalExternalRelatedFlags = sortFlagsAlphabetically(
        application = application,
        flags = getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)
    )

    val externalTerritories = politicalInternalRelatedFlags.filter { relatedFlag ->
        relatedFlag.categories.contains(FlagCategory.TERRITORY) || (relatedFlag.categories
            .contains(FlagCategory.REGION) && relatedFlag.sovereignStateKey != "italy")
    }.sortedWith { p1, p2 ->
        when {
            FlagCategory.AUTONOMOUS_REGION in p1.categories && FlagCategory
                .AUTONOMOUS_REGION !in p2.categories -> -1
            FlagCategory.AUTONOMOUS_REGION !in p1.categories && FlagCategory
                .AUTONOMOUS_REGION in p2.categories -> 1
            else -> 0
        }
    }

    val firstLevelAdminUnits = politicalInternalRelatedFlags.filter { relatedFlag ->
        relatedFlag.sovereignStateKey != null && relatedFlag.parentUnitKey == null &&
                relatedFlag !in externalTerritories
    }

    val secondLevelAdminUnits = politicalInternalRelatedFlags.filter { relatedFlag ->
        relatedFlag.sovereignStateKey != null && relatedFlag.parentUnitKey != null && flagViewMap
            .getValue(relatedFlag.parentUnitKey) !in externalTerritories
    }

    val associatedStates = politicalExternalRelatedFlags.filter { relatedFlag ->
        flag.associatedStateKey?.let { flagViewMap.getValue(it) } == relatedFlag ||
                relatedFlag.associatedStateKey == flagKey
    }

    val sovereign = (politicalInternalRelatedFlags + flag).firstOrNull { internalFlag ->
        internalFlag.categories.contains(FlagCategory.SOVEREIGN_STATE) &&
                internalFlag !in associatedStates
    }

    return RelatedFlagsContent.Political(
        sovereign = sovereign?.let {
            RelatedFlagGroup.Single(
                flag = sovereign,
                category = FlagCategory.SOVEREIGN_STATE.title
            )
        },
        firstLevelAdminUnits = when (firstLevelAdminUnits.isEmpty()) {
            true -> null
            false -> buildList {
                val firstLevelDivisions = firstLevelAdminUnits.flatMap { it.categories }
                    .filter { it in FlagSuperCategory.Regional.enums() }
                    .distinct()
                firstLevelDivisions.forEach { division ->
                    val divisionUnits = firstLevelAdminUnits.filter { flag ->
                        division in flag.categories
                    }
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = divisionUnits,
                            category = division.title,
                        )
                    )
                }
            }
        },
        externalTerritories = when (externalTerritories.isEmpty()) {
            true -> null
            false -> RelatedFlagGroup.Multiple(
                flags = externalTerritories,
                category = externalTerritories.first().categories
                    .first { it in FlagSuperCategory.Regional.enums() }.title,
            )
        },
        associatedStates = when (associatedStates.isEmpty()) {
            true -> null
            false -> RelatedFlagGroup.Multiple(
                flags = associatedStates,
                category = FlagCategory.FREE_ASSOCIATION.title
            )
        },
        internationalOrgs = null,
        secondLevelAdminUnits = when (secondLevelAdminUnits.isEmpty()) {
            true -> null
            false -> buildList {
                val secondLevelDivisions = secondLevelAdminUnits.flatMap { it.categories }
                    .filter { it in FlagSuperCategory.Regional.enums() }
                    .distinct()
                secondLevelDivisions.forEach { division ->
                    val divisionUnits = secondLevelAdminUnits.filter { flag ->
                        division in flag.categories
                    }
                    add(
                        RelatedFlagGroup.Multiple(
                            flags = divisionUnits,
                            category = division.title,
                        )
                    )
                }
            }
        }
    )
}