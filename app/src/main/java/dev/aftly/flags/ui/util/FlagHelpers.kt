package dev.aftly.flags.ui.util

import android.app.Application
import dev.aftly.flags.data.DataSource.NAV_SEPARATOR
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.flagViewMapId
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.data.DataSource.inverseFlagsMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagView


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
        flagsMap.values.filter { flagRes ->
            flagRes.previousFlagOf == parentKey
        }.map { flagRes ->
            inverseFlagsMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(parentKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResources.latestEntity?.let { latestEntityKey ->
        flagsMap.values.filter { flagRes ->
            flagRes.latestEntity == latestEntityKey
        }.map { flagRes ->
            inverseFlagsMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(latestEntityKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagsMap.values.filter { flagRes ->
        flagRes.previousFlagOf == flagKey || flagRes.latestEntity == flagKey
    }.map { flagRes ->
        inverseFlagsMap.getValue(flagRes)
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
        flagsMap.values.filter { flagRes ->
            flagRes.sovereignState == sovereignStateKey
        }.map { flagRes ->
            inverseFlagsMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(sovereignStateKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResources.associatedState?.let { associatedStateKey ->
        flagsMap.values.filter { flagRes ->
            flagRes.associatedState == associatedStateKey
        }.map { flagRes ->
            inverseFlagsMap.getValue(flagRes)
        }.let { siblingKeys ->
            relatedKeys.add(associatedStateKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagsMap.values.filter { flagRes ->
        flagRes.sovereignState == flagKey || flagRes.associatedState == flagKey
    }.map { flagRes ->
        inverseFlagsMap.getValue(flagRes)
    }.let { childKeys ->
        relatedKeys.addAll(childKeys)
    }

    relatedKeys.remove(flagKey)
    return relatedKeys.distinct()
}
/* ------------------------------------- */


fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagView>,
): List<FlagView> {
    val appResources = application.applicationContext.resources

    return flags.sortedBy { flag ->
        normalizeString(string = appResources.getString(flag.flagOf))
    }
}