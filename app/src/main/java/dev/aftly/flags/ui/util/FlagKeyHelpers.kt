package dev.aftly.flags.ui.util

import dev.aftly.flags.data.DataSource.flagsMap
import dev.aftly.flags.data.DataSource.reverseFlagsMap
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagResources

fun getFlagKeys(flags: List<FlagResources>): List<String> =
    flags.map { reverseFlagsMap.getValue(key = it) }

fun getFlagResources(flagKeys: List<String>): List<FlagResources> =
    flagKeys.map { flagsMap.getValue(key = it) }

fun SavedFlag.getFlagResource(): FlagResources = flagsMap.getValue(key = this.flagKey)