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
fun getChronologicalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val relatedKeys = mutableListOf<String>()

    flagRes.previousFlagOf?.let { flagOfKey ->
        flagResMap.values.filter { flag ->
            flag.previousFlagOf == flagOfKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { siblingKeys ->
            relatedKeys.add(flagOfKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagRes.latestEntity?.let { latestKey ->
        flagResMap.values.filter { flag ->
            flag.latestEntity == latestKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { siblingKeys ->
            relatedKeys.add(latestKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResMap.values.filter { flag ->
        flag.previousFlagOf == flagKey || flag.latestEntity == flagKey
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.let { childKeys ->
        relatedKeys.addAll(childKeys)
    }

    return relatedKeys.distinct().filterNot { it == flagKey }
}

fun getPoliticalRelatedFlagKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val relatedKeys = mutableListOf<String>()

    flagRes.sovereignState?.let { sovereignKey ->
        flagResMap.values.filter { flag ->
            flag.sovereignState == sovereignKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { siblingKeys ->
            relatedKeys.add(sovereignKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagRes.previousFlagOf?.let { flagOfKey ->
        flagResMap.values.filter { flag ->
            flag.sovereignState == flagOfKey || flag.associatedState == flagOfKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { siblingKeys ->
            relatedKeys.add(flagOfKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagRes.associatedState?.let { associatedKey ->
        flagResMap.values.filter { flag ->
            flag.associatedState == associatedKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { siblingKeys ->
            relatedKeys.add(associatedKey)
            relatedKeys.addAll(siblingKeys)
        }
    }

    flagResMap.values.filter { flag ->
        flag.sovereignState == flagKey || flag.associatedState == flagKey
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.let { childKeys ->
        relatedKeys.addAll(childKeys)
    }

    return relatedKeys.distinct().filterNot { it == flagKey }
}

fun getChronologicalAdminsOfParentKeys(
    flagKey: String,
    flagRes: FlagResources,
): List<String> {
    val otherLocaleAdmins = mutableListOf<String>()

    /* All (direct & primary) historical admin flags of the sovereign */
    flagRes.sovereignState?.let { sovereignKey ->
        flagResMap.values.filter { flag ->
            flag.latestEntity == sovereignKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { preAdminKeys ->
            otherLocaleAdmins.addAll(preAdminKeys)
        }

    }

    /* All dependents of latest entity */
    flagRes.latestEntity?.let { latestKey ->
        flagResMap.values.filter { flag ->
            flag.sovereignState == latestKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { postAdminKeys ->
            otherLocaleAdmins.addAll(postAdminKeys)
        }
    }

    /* Pre-admin keys of parent entity (eg. primary USA for previous USA flags) */
    flagRes.previousFlagOf?.let { flagOfKey ->
        flagResMap.values.filter { flag ->
            flag.latestEntity == flagOfKey
        }.map { flag ->
            inverseFlagResMap.getValue(flag)
        }.let { flagOfPreAdminKeys ->
            otherLocaleAdmins.addAll(flagOfPreAdminKeys)
        }
    }

    /* Post-admin keys of parent entity (eg. primary USA for previous USA flags) */
    flagRes.previousFlagOf?.let { flagOfKey ->
        flagResMap.getValue(flagOfKey).latestEntity?.let { flagOfLatest ->
            flagResMap.values.filter { flag ->
                flag.sovereignState == flagOfLatest
            }.map { flag ->
                inverseFlagResMap.getValue(flag)
            }.let { flagOfPostAdminKeys ->
                otherLocaleAdmins.addAll(flagOfPostAdminKeys)
            }
        }
    }

    return otherLocaleAdmins.distinct().filterNot { it == flagKey }
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

fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagView>,
): List<FlagView> {
    val appResources = application.applicationContext.resources

    return flags.sortedBy { flag ->
        normalizeString(string = appResources.getString(flag.flagOf))
    }
}