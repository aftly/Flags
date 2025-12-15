package dev.aftly.flags.ui.util

import android.icu.text.Normalizer2
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.ibm.icu.text.RuleBasedNumberFormat
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagView
import java.util.Calendar
import java.util.Locale

/* Normalize special characters and non-alphanumeric */
fun normalizeString(string: String): String {
    val normalizer = Normalizer2.getNFDInstance()
    val decomposed = normalizer.normalize(string)
    return decomposed.filter { it.isLetterOrDigit() }
}

fun normalizeStringLower(string: String): String = normalizeString(string).lowercase()


@Composable
fun flagDatesString(
    flag: FlagView,
    isGameDatesMode: Boolean = false,
    isBrackets: Boolean = true,
) = buildString {
    if (flag.isDated) {
        val config = LocalConfiguration.current
        val languageTag = config.locales[0].toLanguageTag()
        val formatter = RuleBasedNumberFormat(
            Locale.forLanguageTag(languageTag), RuleBasedNumberFormat.ORDINAL
        )

        if (isBrackets) append(stringResource(R.string.string_open_bracket))

        flag.fromYear?.let { year ->
            if (flag.fromYearCirca == true) append(
                stringResource(
                    id = R.string.string_date_circa,
                    formatter.format((year / 100).inc())
                )
            ) else append(year.toString())
        }

        if (flag.fromYear != null && flag.toYear != null) {
            append(stringResource(R.string.string_dash))
        }

        flag.toYear?.let { year ->
            when (year to isGameDatesMode) {
                0 to false -> append(stringResource(R.string.string_present))
                0 to true -> append(Calendar.getInstance().get(Calendar.YEAR).toString())
                else -> {
                    if (flag.toYearCirca == true) append(
                        stringResource(
                            id = R.string.string_date_circa,
                            formatter.format((year / 100).inc())
                        )
                    ) else append(year.toString())
                }
            }
        }

        if (isBrackets) append(stringResource(R.string.string_close_bracket))
    }
}

fun removeLastWhitespaceCommas(resIds: MutableList<Int>) {
    val whitespaceCommaResIds = listOf(R.string.string_whitespace, R.string.string_comma_whitespace)

    if (resIds.lastOrNull() in whitespaceCommaResIds) {
        resIds.removeLastOrNull()
        removeLastWhitespaceCommas(resIds)
    }
}