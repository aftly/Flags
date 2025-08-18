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
import dev.aftly.flags.R

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
    val previousFilterValues = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
    }
    val latestFilterValues = buildList {
        add(flagKey)
        flagRes.previousFlagOf?.let { add(it) }
        flagRes.latestEntity?.let { add(it) }
    }

    return flagResMap.values.filter { flag ->
        flag.previousFlagOf in previousFilterValues || flag.latestEntity in latestFilterValues
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct()
}

fun getChronologicalIndirectRelatedFlagKeys(
    flagRes: FlagResources,
): List<String> {
    val latestFilterValues = buildList {
        /* For all (direct & primary) historical admin flags of the sovereign */
        flagRes.sovereignState?.let { add(it) }
        /* For pre-admin keys of parent's sovereign (eg. USA for historical flag of a state) */
        flagRes.previousFlagOf?.let { flagOfKey ->
            flagResMap.getValue(flagOfKey)
                .sovereignState?.let { add(it) }
        }
    }

    val sovereignFilterValues = buildList {
        /* For all dependents of latest entity */
        flagRes.latestEntity?.let { add(it) }
        /* For post-admin keys of parent entity (eg. primary USA for historical USA flags) */
        flagRes.previousFlagOf?.let { flagOfKey ->
            flagResMap.getValue(flagOfKey)
                .latestEntity?.let { add(it) }
        }
    }

    return flagResMap.values.filter { flag ->
        flag.latestEntity in latestFilterValues || flag.sovereignState in sovereignFilterValues
    }.map { flag ->
        inverseFlagResMap.getValue(flag)
    }.distinct()
}

enum class ExternalCategoryExceptions(val key: String) {
    /* For flags with any externalCategories as internal units */
    ITALY (key = "italy")
}
val extCatExceptions = ExternalCategoryExceptions.entries.map { it.key }
val externalCategories = listOf(FlagCategory.TERRITORY, FlagCategory.REGION)

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
    }.distinct()
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
fun getPoliticalRelatedFlagsContentOrNull(
    flag: FlagView,
    application: Application,
): RelatedFlagsContent.Political? {
    return if (!flag.isPoliticalRelatedFlags) {
        null
    } else {
        val flagKey = inverseFlagViewMap.getValue(flag)
        val politicalInternalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalInternalRelatedFlagKeys)
        )
        val politicalExternalRelatedFlags = sortFlagsAlphabetically(
            application = application,
            flags = getFlagsFromKeys(flag.politicalExternalRelatedFlagKeys)
        )

        val sovereign = politicalInternalRelatedFlags.firstOrNull { internalFlag ->
            internalFlag.categories.contains(FlagCategory.SOVEREIGN_STATE)
        }

        val firstLevelAdminUnits = politicalInternalRelatedFlags.filter { relatedFlag ->
            relatedFlag.sovereignStateKey != null && relatedFlag.parentUnitKey == null
        }

        val secondLevelAdminUnits = politicalInternalRelatedFlags.filter { relatedFlag ->
            relatedFlag.sovereignStateKey != null && relatedFlag.parentUnitKey != null
        }

        val associatedStates = politicalExternalRelatedFlags.filter { relatedFlag ->
            flag.associatedStateKey?.let { flagViewMap.getValue(it) } == relatedFlag ||
                    relatedFlag.associatedStateKey == flagKey
        }

        val externalTerritories = politicalExternalRelatedFlags.filterNot { relatedFlag ->
            relatedFlag in associatedStates
        }.sortedWith { p1, p2 ->
            when {
                FlagCategory.AUTONOMOUS_REGION in p1.categories && FlagCategory
                    .AUTONOMOUS_REGION !in p2.categories -> -1
                FlagCategory.AUTONOMOUS_REGION !in p1.categories && FlagCategory
                    .AUTONOMOUS_REGION in p2.categories -> 1
                else -> 0
            }
        }

        RelatedFlagsContent.Political(
            sovereign = sovereign?.let { sovereign ->
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
            },
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

        val latestEntity = chronologicalDirectRelatedFlags.firstOrNull { directFlag ->
            directFlag.latestEntityKey == null && directFlag.previousFlagOfKey == null
        }

        val previousEntities = chronologicalDirectRelatedFlags.filter { directFlag ->
            directFlag.latestEntityKey != null
        }

        val dependentsOfLatest = chronologicalIndirectRelatedFlags.filter { indirectFlag ->
            latestEntity?.let {
                indirectFlag.sovereignStateKey == inverseFlagViewMap.getValue(it)
            } ?: false
        }

        val previousEntitiesOfSovereign = chronologicalIndirectRelatedFlags.filterNot {
            indirectFlag -> indirectFlag in dependentsOfLatest
        }

        RelatedFlagsContent.Chronological(
            latestEntity = latestEntity?.let { latest ->
                RelatedFlagGroup.Single(
                    flag = latest,
                    category = R.string.menu_related_flags_chronological_latest,
                )
            },
            previousEntities = when (previousEntities.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = previousEntities,
                    category = R.string.menu_related_flags_chronological_previous,
                )
            },
            previousEntitiesOfSovereign = when (previousEntitiesOfSovereign.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = previousEntitiesOfSovereign,
                    category = R.string.menu_related_flags_chronological_previous_of_sovereign,
                )
            },
            dependentsOfLatest = when (dependentsOfLatest.isEmpty()) {
                true -> null
                false -> RelatedFlagGroup.Multiple(
                    flags = dependentsOfLatest,
                    category = R.string.menu_related_flags_chronological_dependents_of_latest,
                )
            },
        )
    }
}