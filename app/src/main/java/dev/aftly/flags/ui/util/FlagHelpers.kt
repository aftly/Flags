package dev.aftly.flags.ui.util

import android.app.Application
import android.content.Context
import dev.aftly.flags.data.DataSource.NAV_SEPARATOR
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.flagViewMapId
import dev.aftly.flags.data.DataSource.flagResMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.data.DataSource.inverseFlagResMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagView
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
fun getInternalRelatedFlagKeys(
    flagKey: String,
    flagResources: FlagResources,
): List<String> {
    val relatedKeys = mutableListOf<String>()

    flagResources.previousFlagOf?.let { parentKey ->
        flagResMap.values.filter { flagRes ->
            flagRes.previousFlagOf == parentKey
        }.map { flagRes ->
            inverseFlagResMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(parentKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResources.latestEntity?.let { latestEntityKey ->
        flagResMap.values.filter { flagRes ->
            flagRes.latestEntity == latestEntityKey
        }.map { flagRes ->
            inverseFlagResMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(latestEntityKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResMap.values.filter { flagRes ->
        flagRes.previousFlagOf == flagKey || flagRes.latestEntity == flagKey
    }.map { flagRes ->
        inverseFlagResMap.getValue(flagRes)
    }.let { childKeys ->
        relatedKeys.addAll(childKeys)
    }

    relatedKeys.remove(flagKey)
    return relatedKeys.distinct()
}

fun getExternalRelatedFlagKeys(
    flagKey: String,
    flagResources: FlagResources,
): List<String> {
    val relatedKeys = mutableListOf<String>()

    flagResources.sovereignState?.let { sovereignStateKey ->
        flagResMap.values.filter { flagRes ->
            flagRes.sovereignState == sovereignStateKey
        }.map { flagRes ->
            inverseFlagResMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(sovereignStateKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResources.associatedState?.let { associatedStateKey ->
        flagResMap.values.filter { flagRes ->
            flagRes.associatedState == associatedStateKey
        }.map { flagRes ->
            inverseFlagResMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(associatedStateKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResMap.values.filter { flagRes ->
        flagRes.sovereignState == flagKey || flagRes.associatedState == flagKey
    }.map { flagRes ->
        inverseFlagResMap.getValue(flagRes)
    }.let { childKeys ->
        relatedKeys.addAll(childKeys)
    }

    relatedKeys.remove(flagKey)
    return relatedKeys.distinct()
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

fun getExternalRelatedFlagsSorted(
    flag: FlagView,
    application: Application,
): List<FlagView> {
    val appResources = application.applicationContext.resources
    val list = mutableListOf<FlagView>()

    flag.sovereignStateKey?.let { sovereignState ->
        val siblings = flagViewMap.values.filter {
            it.sovereignStateKey == sovereignState
        }
        list.addAll(siblings)
        list.add(flagViewMap.getValue(sovereignState))
    }

    flag.associatedStateKey?.let { associatedState ->
        val siblings = flagViewMap.values.filter {
            it.associatedStateKey == associatedState
        }
        list.addAll(siblings)
        list.add(flagViewMap.getValue(associatedState))
    }

    val flagKey = inverseFlagViewMap.getValue(flag)
    val children = flagViewMap.values.filter {
        it.sovereignStateKey == flagKey || it.associatedStateKey == flagKey
    }
    list.addAll(children)

    return list.distinct().sortedBy { normalizeString(appResources.getString(it.flagOf)) }
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