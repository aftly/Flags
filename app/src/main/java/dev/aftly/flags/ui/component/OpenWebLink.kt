package dev.aftly.flags.ui.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/* Start implicit activity with a web URL */
fun openWebLink(
    context: Context,
    linkToView: String,
) {
    val intent = Intent(Intent.ACTION_VIEW, linkToView.toUri())
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {} // TODO: param "e" when catch Content
    // TODO: Pop up dialog indicating failure
}