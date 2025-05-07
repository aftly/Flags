package dev.aftly.flags.ui.util

import android.content.res.Resources
import androidx.annotation.StringRes

fun getStringFromResources(resources: Resources, @StringRes stringRes: Int): String {
    return resources.getString(stringRes)
}