package dev.aftly.flags.model

import androidx.annotation.StringRes


/* ------------ SCORE DATA CLASS FOR UI ------------ */
class ScoreData(
    gameFlags: List<FlagResources>,
    guessedFlags: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    shownFlags: List<FlagResources>,
    @StringRes guessedFlagsTitle: Int,
    @StringRes skippedFlagsTitle: Int,
    @StringRes shownFlagsTitle: Int,
    @StringRes remainderFlagsTitle: Int,
    isTimeTrial: Boolean,
    timeTrialTime: Int = 0, /* In seconds */
    timeElapsed: Int, /* In seconds */
) {
    private val remainderFlags = gameFlags.filterNot { it in guessedFlags }
        .filterNot { it in skippedFlags }.filterNot { it in shownFlags }

    private val correctAnswers = guessedFlags.count()
    private val scorePercent = (correctAnswers.toFloat() / gameFlags.size.toFloat()) * 100f

    val scoreOverview = ScoreOverview(
        totalsOverview = TotalsOverview(
            correctAnswers = correctAnswers,
            outOfCount = gameFlags.size,
            scorePercent = scorePercent,
        ),
        timeOverview = TimeOverview(
            isTimeTrial = isTimeTrial,
            timeTrialTime = timeTrialTime,
            timeElapsed = timeElapsed,
        )
    )

    val allScores: List<TitledList> = listOf(
        GuessedFlags(
            list = guessedFlags,
            titleResId = guessedFlagsTitle,
        ),
        SkippedFlags(
            list = skippedFlags,
            titleResId = skippedFlagsTitle,
        ),
        ShownFlags(
            list = shownFlags,
            titleResId = shownFlagsTitle,
        ),
        RemainderFlags(
            list = remainderFlags,
            titleResId = remainderFlagsTitle,
        )
    )
}


/* ------------ SCORE OVERVIEW CLASSES ------------ */
data class ScoreOverview(
    val totalsOverview: TotalsOverview,
    val timeOverview: TimeOverview,
)

data class TotalsOverview(
    val correctAnswers: Int,
    val outOfCount: Int,
    val scorePercent: Float,
)

data class TimeOverview(
    val isTimeTrial: Boolean,
    val timeTrialTime: Int,
    val timeElapsed: Int,
)


/* ------------ SCORE DETAIL CLASSES ------------ */
abstract class TitledList(
    val list: List<FlagResources>,
    val titleResId: Int,
)

private class GuessedFlags(
    list: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, titleResId)

private class SkippedFlags(
    list: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, titleResId)

private class ShownFlags(
    list: List<FlagResources>,
    titleResId: Int
) : TitledList(list, titleResId)

private class RemainderFlags(
    list: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, titleResId)