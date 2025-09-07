package dev.aftly.flags.model.serialization

import android.content.Context
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class DrawableResName(val name: String) {
    fun resId(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier(name, "drawable", context.packageName)

        return if (resourceId == 0) error("drawableResource \"$name\" not found")
        else resourceId
    }
}