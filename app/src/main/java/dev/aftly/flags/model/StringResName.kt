package dev.aftly.flags.model

import android.content.Context
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StringResName(val name: String) {
    fun resId(context: Context): Int =
        context.resources.getIdentifier(name, "string", context.packageName)
}