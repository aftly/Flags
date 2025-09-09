package dev.aftly.flags.model

import android.content.res.Resources

sealed interface SearchFlow {
    data class Str(val value: String) : SearchFlow
    data class FlagsList(val value: List<FlagView>) : SearchFlow
    data class Bool(val value: Boolean) : SearchFlow
    data class AppResources(val value: Resources) : SearchFlow
}