package dev.aftly.flags.model


/* ------------ Game score super class ------------ */
class ScoreData(
    gameFlags: List<FlagResources>,
    guessedFlags: List<FlagResources>,
    guessedFlagsSorted: List<FlagResources>,
    skippedFlags: List<FlagResources>,
    skippedFlagsSorted: List<FlagResources>,
    shownFlags: List<FlagResources>,
    shownFlagsSorted: List<FlagResources>,
    isTimeTrial: Boolean,
    timeTrialStart: Int?, /* In seconds */
    timerTime: Int, /* In seconds */
    val timeStamp: Int, // TODO
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
            time = timerTime,
        )
    )

    val allScores: List<TitledList> = listOf(
        GuessedFlags(
            list = guessedFlags,
            sortedList = guessedFlagsSorted,
        ),
        SkippedFlags(
            list = skippedFlags,
            sortedList = skippedFlagsSorted,
        ),
        ShownFlags(
            list = shownFlags,
            sortedList = shownFlagsSorted,
        ),
        RemainderFlags(
            list = remainderFlags,
            sortedList = remainderFlags,
        )
    )
}


/* ------------ Score overview classes ------------ */
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


/* ------------ Score details classes ------------ */
abstract class TitledList(
    val list: List<FlagResources>,
    val sortedList: List<FlagResources>,
)

class GuessedFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
) : TitledList(list, sortedList)

class SkippedFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
) : TitledList(list, sortedList)

class ShownFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
) : TitledList(list, sortedList)

class RemainderFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
) : TitledList(list, sortedList)