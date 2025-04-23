package dev.aftly.flags.ui.util

import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.model.FlagResources

/* Return flagsMap key of FlagResource as a string for navigation argument */
fun getFlagNavArg(flag: FlagResources): String {
    return flagsMap.entries.find { it.value == flag }?.key
        ?: "unitedKingdom"
}