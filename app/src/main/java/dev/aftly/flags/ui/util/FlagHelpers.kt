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
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.RelatedFlagsContent
import dev.aftly.flags.model.StringResSource


fun getFlagKey(flag: FlagView): String = inverseFlagViewMap.getValue(key = flag)

fun getFlagKeys(flags: List<FlagView>): List<String> =
    flags.map { inverseFlagViewMap.getValue(key = it) }

fun getFlagsFromKeys(flagKeys: List<String>): List<FlagView> =
    flagKeys.map { flagViewMap.getValue(key = it) }


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

fun getPoliticalDirectRelatedFlagKeys(
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
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct().filterNot { key ->
        key == flagKey
    }
}

fun getPoliticalAssociatedRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    return buildList {
        flagRes.associatedState?.let { add(it) }

        addAll(elements =
            flagResMap.values.filter { flag ->
                flag.associatedState == flagKey
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


/* ------------------------------------- */

fun getPoliticalRelatedFlagsContent(
    flag: FlagView,
    application: Application,
): RelatedFlagsContent.Political {
    val flagKey = inverseFlagViewMap.getValue(flag)
    val politicalDirectRelatedFlags = sortFlagsAlphabetically(
        application = application,
        flags = getFlagsFromKeys(flag.politicalDirectRelatedFlagKeys)
    )

    val sov = politicalDirectRelatedFlags.filter { relatedFlag ->
        relatedFlag.categories.contains(FlagCategory.SOVEREIGN_STATE)
    }

    val firstLevel = politicalDirectRelatedFlags.filter { relatedFlag ->
        !relatedFlag.categories.contains(FlagCategory.SOVEREIGN_STATE)
    }

    val associated = sortFlagsAlphabetically(
        application = application,
        flags = getFlagsFromKeys(flag.politicalAssociatedRelatedFlagKeys)
    )

    val sovereign =
        if (flag.categories.contains(FlagCategory.SOVEREIGN_STATE)) flag
        else if (flag.previousFlagOfKey != null) flagViewMap.getValue(flag.previousFlagOfKey)
        else if (flag.sovereignStateKey != null) flagViewMap.getValue(flag.sovereignStateKey)
        else null

    val firstLevelAdminUnits =
        if (flag.categories.contains(FlagCategory.SOVEREIGN_STATE)) {
            /* If flag is sovereign, filter it's immediate children */
            politicalDirectRelatedFlags.filter { relatedFlag ->
                relatedFlag.sovereignStateKey == flagKey && relatedFlag.parentUnitKey == null
            }
        } else if (flag.previousFlagOfKey != null) {
            /* When flag is a previous flag, if sovereign filter it's immediate children, else
            * filter it's first level siblings/cousins */
            val previousFlagOf = flagViewMap.getValue(flag.previousFlagOfKey)

            if (previousFlagOf.categories.contains(FlagCategory.SOVEREIGN_STATE)) {
                politicalDirectRelatedFlags.filter { relatedFlag ->
                    relatedFlag.sovereignStateKey == flag.previousFlagOfKey && relatedFlag
                        .parentUnitKey == null
                }
            } else {
                politicalDirectRelatedFlags.filter { relatedFlag ->
                    relatedFlag.sovereignStateKey == previousFlagOf.sovereignStateKey && relatedFlag
                        .parentUnitKey == null
                }
            }
        } else {
            /* Filter flags' first level siblings/cousins */
            politicalDirectRelatedFlags.filter { relatedFlag ->
                relatedFlag.sovereignStateKey == flag.sovereignStateKey && relatedFlag
                    .parentUnitKey == null
            }
        }
}

fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagView>,
): List<FlagView> {
    val appResources = application.applicationContext.resources

    return flags.sortedBy { flag ->
        normalizeString(string = appResources.getString(flag.flagOf))
    }
}