package dev.aftly.flags.ui.component

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes


// Create a ShareSheet text intent
fun shareText(
    context: Context,
    @StringRes subject: Int,
    textToShare: String,
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(subject))
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(subject),
        )
    )
}