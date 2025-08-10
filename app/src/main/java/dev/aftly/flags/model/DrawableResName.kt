package dev.aftly.flags.model

import android.content.Context
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class DrawableResName(val name: String) {
    fun resId(context: Context): Int =
        context.resources.getIdentifier(name, "drawable", context.packageName)
}