package dev.aftly.flags.ui.util

import android.app.Application
import dev.aftly.flags.model.FlagResources

fun sortFlagsAlphabetically(
    application: Application,
    flags: List<FlagResources>,
): List<FlagResources> {
    val appResources = application.applicationContext.resources

    return flags.sortedBy { flag ->
        normalizeString(string = appResources.getString(flag.flagOf))
    }
}