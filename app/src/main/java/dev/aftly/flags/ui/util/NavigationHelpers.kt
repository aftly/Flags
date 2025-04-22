package dev.aftly.flags.ui.util

import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.model.FlagResources

/* Return flagsMap key of FlagResource as a string for navigation argument */
fun getFlagNavArgument(flag: FlagResources): String {
    return flagsMap.entries.find { it.value == flag }?.key
        ?: "unitedKingdom" // TODO: Make special flag page for errors?
}


/* Encapsulate a call to onNavigateDetails() from within selectFlag(), after converting
 * the FlagResources instantiation to it's corresponding key string from flagsMap */
/*
fun callOnNavigateDetailsFromOnFlagSelect(
    onNavigateDetails: (String) -> Unit,
): (FlagResources) -> Unit {
    val onFlagSelect: (FlagResources) -> Unit = { flagResource ->
        val flagNavArgument = returnFlagArgument(flagResource)
        onNavigateDetails(flagNavArgument)
    }
    return onFlagSelect
}
 */