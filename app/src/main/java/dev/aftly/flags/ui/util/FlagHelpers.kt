package dev.aftly.flags.ui.util

import android.app.Application
import dev.aftly.flags.data.DataSource.NAV_SEPARATOR
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.flagsMapId
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagResources


fun getFlagKey(flag: FlagResources): String = reverseFlagsMap.getValue(key = flag)

fun getFlagKeys(flags: List<FlagResources>): List<String> =
    flags.map { reverseFlagsMap.getValue(key = it) }

fun getFlagsFromKeys(flagKeys: List<String>): List<FlagResources> =
    flagKeys.map { flagsMap.getValue(key = it) }


fun SavedFlag.getFlagResource(): FlagResources = flagsMap.getValue(key = this.flagKey)


fun getFlagIds(flags: List<FlagResources>): List<Int> = flags.map { it.id }

fun getFlagIdsString(flagIds: List<Int>): String = flagIds.joinToString(separator = NAV_SEPARATOR)

fun getFlagIdsFromString(string: String): List<Int> =
    string.split(NAV_SEPARATOR).mapNotNull { it.toIntOrNull() }

fun getFlagFromId(id: Int): FlagResources = flagsMapId.getValue(key = id)


fun getRelatedFlags(
    flag: FlagResources,
    application: Application,
): List<FlagResources> {
    val appResources = application.applicationContext.resources
    val list = mutableListOf(flag)

    flag.sovereignState?.let { sovereignState ->
        val siblings = flagsMap.values.filter {
            it.sovereignState == sovereignState
        }
        list.addAll(siblings)
        list.add(flagsMap.getValue(sovereignState))
    }

    flag.associatedState?.let { associatedState ->
        val siblings = flagsMap.values.filter {
            it.associatedState == associatedState
        }
        list.addAll(siblings)
        list.add(flagsMap.getValue(associatedState))
    }

    val flagKey = reverseFlagsMap.getValue(flag)
    val children = flagsMap.values.filter {
        it.sovereignState == flagKey || it.associatedState == flagKey
    }
    list.addAll(children)

    return list.distinct().sortedBy { normalizeString(appResources.getString(it.flagOf)) }
}