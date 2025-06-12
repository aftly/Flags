package dev.aftly.flags.ui.util

import android.os.Build
import java.text.DateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        /* If Android 8+ */
        val instant = Instant.ofEpochMilli(timestamp)
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)

    } else {
        /* If Android 7 */
        val date = Date(timestamp)
        val formatter = DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT,
            Locale.getDefault()
        )
        return formatter.format(date)
    }
}