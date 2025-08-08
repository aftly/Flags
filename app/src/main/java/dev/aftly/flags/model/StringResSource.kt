package dev.aftly.flags.model

import androidx.annotation.StringRes

/* For use in DataSource to handle compile time enforcement for downstream usage of the data */
sealed interface StringResSource {
    data class Explicit(@param:StringRes val resId: Int) : StringResSource
    object Inherit : StringResSource
}