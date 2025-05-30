package dev.aftly.flags.model

import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.allFlagsList

class ScoreData(
    guessedFlags: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    shownFlags: List<FlagResources>,
) {
    private val remainderFlags = allFlagsList.filterNot { it in guessedFlags }
        .filterNot { it in skippedFlags }.filterNot { it in shownFlags }

    val allScores: List<TitledList> = listOf(
        GuessedFlags(list = guessedFlags),
        SkippedFlags(list = skippedFlags),
        ShownFlags(list = shownFlags),
        RemainderFlags(list = remainderFlags)
    )
}

/* Polymorphic class with overridable title for all score types */
abstract class TitledList(val list: List<FlagResources>) {
    abstract val titleResId: Int
}


/* Class for each score type, overriding TitledList with individual @StringResIds */
class GuessedFlags(list: List<FlagResources>) : TitledList(list = list) {
    override val titleResId = R.string.game_score_details_guessed
    val isExpanded: Boolean = false // TODO: check if useful
}

class SkippedFlags(list: List<FlagResources>) : TitledList(list = list) {
    override val titleResId = R.string.game_score_details_skipped
    val isExpanded: Boolean = false // TODO: check if useful
}

class ShownFlags(list: List<FlagResources>) : TitledList(list = list) {
    override val titleResId = R.string.game_score_details_shown
    val isExpanded: Boolean = false // TODO: check if useful
}

class RemainderFlags(list: List<FlagResources>) : TitledList(list = list) {
    override val titleResId = R.string.game_score_details_remainder
    val isExpanded: Boolean = false // TODO: check if useful
}

