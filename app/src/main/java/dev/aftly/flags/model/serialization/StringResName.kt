package dev.aftly.flags.model.serialization

import android.content.Context
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StringResName(val name: String) {
    fun resId(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier(name, "string", context.packageName)

        return if (resourceId == 0) error("stringResource \"$name\" not found")
        else resourceId
    }
}