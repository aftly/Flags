package dev.aftly.flags.ui.util

import android.icu.text.Normalizer2

/* Input string with special characters, output as string with non-special characters */
fun normalizeString(string: String): String {
    val normalizer = Normalizer2.getNFDInstance()
    val decomposed = normalizer.normalize(string)
    return decomposed.filter { it.isLetterOrDigit() }
}

/* normalizeString and make lowercase */
fun normalizeLower(string: String): String {
    val normalizer = Normalizer2.getNFDInstance()
    val decomposed = normalizer.normalize(string)
    return decomposed.filter { it.isLetterOrDigit() }.lowercase()
}