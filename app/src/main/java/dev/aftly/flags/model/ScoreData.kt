package dev.aftly.flags.model

import androidx.annotation.StringRes


/* ------------ SCORE DATA CLASS FOR UI ------------ */
class ScoreData(
    gameFlags: List<FlagResources>,
    guessedFlags: List<FlagResources>,
    guessedFlagsSorted: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    skippedFlagsSorted: List<FlagResources>,
    shownFlags: List<FlagResources>,
    shownFlagsSorted: List<FlagResources>,
    @StringRes guessedFlagsTitle: Int,
    @StringRes skippedFlagsTitle: Int,
    @StringRes shownFlagsTitle: Int,
    @StringRes remainderFlagsTitle: Int,
    isTimeTrial: Boolean,
    timeTrialStart: Int?, /* In seconds */
    time: Int, /* In seconds */
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
            timeTrialStart = timeTrialStart,
            time = time,
        )
    )

    val allScores: List<TitledList> = listOf(
        GuessedFlags(
            list = guessedFlags,
            sortedList = guessedFlagsSorted,
            titleResId = guessedFlagsTitle,
        ),
        SkippedFlags(
            list = skippedFlags,
            sortedList = skippedFlagsSorted,
            titleResId = skippedFlagsTitle,
        ),
        ShownFlags(
            list = shownFlags,
            sortedList = shownFlagsSorted,
            titleResId = shownFlagsTitle,
        ),
        RemainderFlags(
            list = remainderFlags,
            sortedList = remainderFlags,
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
    val timeTrialStart: Int?,
    val time: Int,
)


/* ------------ SCORE DETAIL CLASSES ------------ */
abstract class TitledList(
    val list: List<FlagResources>,
    val sortedList: List<FlagResources>,
    val titleResId: Int,
)

private class GuessedFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, sortedList, titleResId)

private class SkippedFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, sortedList, titleResId)

private class ShownFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
    titleResId: Int
) : TitledList(list, sortedList, titleResId)

private class RemainderFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
    titleResId: Int,
) : TitledList(list, sortedList, titleResId)