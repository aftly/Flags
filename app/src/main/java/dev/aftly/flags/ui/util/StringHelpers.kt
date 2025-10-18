package dev.aftly.flags.ui.util

import android.icu.text.Normalizer2
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagView
import java.util.Calendar

/* Normalize special characters and non-alphanumeric */
fun normalizeString(string: String): String {
    val normalizer = Normalizer2.getNFDInstance()
    val decomposed = normalizer.normalize(string)
    return decomposed.filter { it.isLetterOrDigit() }
}

fun normalizeLower(string: String): String = normalizeString(string).lowercase()


@Composable
fun flagDatesString(
    flag: FlagView,
    isGameDatesMode: Boolean = false,
    isBrackets: Boolean = true,
) = buildString {
    if (flag.isDated) {
        if (isBrackets) append(stringResource(R.string.string_open_bracket))

        flag.fromYear?.let {
            append(it.toString())
            /*
            if (flag.fromYearCirca) {

            } else {
                append(it.toString())
            }
             */
        }

        if (flag.fromYear != null && flag.toYear != null) {
            append(stringResource(R.string.string_dash))
        }

        flag.toYear?.let { year ->
            when (year to isGameDatesMode) {
                0 to false -> append(stringResource(R.string.string_present))
                0 to true -> append(Calendar.getInstance().get(Calendar.YEAR).toString())
                else -> append(year.toString())
            }
        }

        if (isBrackets) append(stringResource(R.string.string_close_bracket))
    }
}