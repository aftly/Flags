package dev.aftly.flags.ui.util

import android.app.Application
import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.model.FlagResources

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