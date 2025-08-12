package dev.aftly.flags.model

import android.content.res.Resources

sealed interface SearchFlow {
    data class SearchQuery(val value: String) : SearchFlow
    data class CurrentFlags(val value: List<FlagView>) : SearchFlow
    data class SavedFlags(val value: List<FlagView>) : SearchFlow
    data class IsSavedFlags(val value: Boolean) : SearchFlow
    data class AppResources(val value: Resources) : SearchFlow
    data class TheString(val value: String) : SearchFlow
    data class FirstItem(val value: FlagView?) : SearchFlow
    data class RelatedFlags(val value: List<FlagView>) : SearchFlow
}