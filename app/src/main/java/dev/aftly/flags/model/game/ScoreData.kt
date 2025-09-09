package dev.aftly.flags.model.game

import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.ui.util.getFlagKeys
import dev.aftly.flags.ui.util.getFlagsFromKeys
import dev.aftly.flags.ui.util.superCategories


/* ------------ Game score top level class ------------ */
class ScoreData(
    val id: Int = 0,
    val timestamp: Long,
    val answerMode: AnswerMode,
    val difficultyMode: DifficultyMode,
    val timeMode: TimeMode,
    val timerStart: Int?, /* In seconds */
    val timerEnd: Int, /* In seconds */
    val gameSuperCategories: List<FlagSuperCategory>,
    val gameSubCategories: List<FlagCategory>,
    val flagsAll: List<FlagView>,
    val flagsGuessed: List<FlagView>,
    flagsGuessedSorted: List<FlagView>,
    val flagsSkippedGuessed: List<FlagView>,
    flagsSkippedGuessedSorted: List<FlagView>,
    val flagsSkipped: List<FlagView>,
    flagsSkippedSorted: List<FlagView>,
    val flagsShown: List<FlagView>,
    flagsShownSorted: List<FlagView>,
    val flagsFailed: List<FlagView>,
    flagsFailedSorted: List<FlagView>,
) {
    private val remainderFlags = flagsAll.filterNot { flag ->
        flag in (flagsGuessed + flagsSkipped + flagsShown + flagsFailed)
    }
    private val correctAnswers = flagsGuessed.size
    private val scorePercent = (correctAnswers.toFloat() / flagsAll.size.toFloat()) * 100f

    val scoreOverview = ScoreOverview(
        answerMode = answerMode,
        difficultyMode = difficultyMode,
        totalsOverview = TotalsOverview(
            correctAnswers = correctAnswers,
            outOfCount = flagsAll.size,
            scorePercent = scorePercent,
        ),
        categoriesOverview = CategoriesOverview(
            superCategories = gameSuperCategories,
            subCategories = gameSubCategories,
        ),
        timeOverview = TimeOverview(
            timeMode = timeMode,
            timeTrialStart = timerStart,
            time = timerEnd,
        )
    )

    val allScores: List<TitledList> = listOf(
        GuessedFlags(
            list = flagsGuessed,
            sortedList = flagsGuessedSorted,
        ),
        SkippedGuessedFlags(
            list = flagsSkippedGuessed,
            sortedList = flagsSkippedGuessedSorted,
        ),
        SkippedFlags(
            list = flagsSkipped,
            sortedList = flagsSkippedSorted,
        ),
        ShownFlags(
            list = flagsShown,
            sortedList = flagsShownSorted,
        ),
        FailedFlags(
            list = flagsFailed,
            sortedList = flagsFailedSorted,
        ),
        RemainderFlags(
            list = remainderFlags,
            sortedList = remainderFlags,
        )
    )

    /* Return ScoreItem from ScoreData instance for Room Dao class */
    fun toScoreItem(): ScoreItem = ScoreItem(
        id = id,
        timestamp = timestamp,
        score = correctAnswers,
        outOf = flagsAll.size,
        answerMode = answerMode,
        difficultyMode = difficultyMode,
        timeMode = timeMode,
        timerStart = timerStart,
        timerEnd = timerEnd,
        gameSuperCategories = gameSuperCategories as List<FlagCategoryBase>,
        gameSubCategories = gameSubCategories,
        flagsAll = getFlagKeys(flags = flagsAll),
        flagsGuessed = getFlagKeys(flags = flagsGuessed),
        flagsSkippedGuessed = getFlagKeys(flags = flagsSkippedGuessed),
        flagsSkipped = getFlagKeys(flags = flagsSkipped),
        flagsShown = getFlagKeys(flags = flagsShown),
        flagsFailed = getFlagKeys(flags = flagsFailed),
    )

    fun isScoresEmpty() = flagsGuessed.isEmpty() && flagsSkipped.isEmpty() &&
            flagsShown.isEmpty() && flagsFailed.isEmpty()
}


/* Extension function to return ScoreData from ScoreItem instance (Room Dao class)
 * (needs string sorting, so eg. call from ViewModel) */
fun ScoreItem.toScoreData(
    flagsGuessedSorted: List<FlagView>,
    flagsSkippedGuessedSorted: List<FlagView>,
    flagsSkippedSorted: List<FlagView>,
    flagsShownSorted: List<FlagView>,
    flagsFailedSorted: List<FlagView>,
): ScoreData = ScoreData(
    id = id,
    timestamp = timestamp,
    answerMode = answerMode,
    difficultyMode = difficultyMode,
    timeMode = timeMode,
    timerStart = timerStart,
    timerEnd = timerEnd,
    gameSuperCategories = superCategories(),
    gameSubCategories = gameSubCategories,
    flagsAll = getFlagsFromKeys(flagKeys = flagsAll),
    flagsGuessed = getFlagsFromKeys(flagKeys = flagsGuessed),
    flagsGuessedSorted = flagsGuessedSorted,
    flagsSkippedGuessed = getFlagsFromKeys(flagKeys = flagsSkippedGuessed),
    flagsSkippedGuessedSorted = flagsSkippedGuessedSorted,
    flagsSkipped = getFlagsFromKeys(flagKeys = flagsSkipped),
    flagsSkippedSorted = flagsSkippedSorted,
    flagsShown = getFlagsFromKeys(flagKeys = flagsShown),
    flagsShownSorted = flagsShownSorted,
    flagsFailed = getFlagsFromKeys(flagKeys = flagsFailed),
    flagsFailedSorted = flagsFailedSorted,
)


/* ------------ Score overview classes ------------ */
data class ScoreOverview(
    val totalsOverview: TotalsOverview,
    val categoriesOverview: CategoriesOverview,
    val answerMode: AnswerMode,
    val difficultyMode: DifficultyMode,
    val timeOverview: TimeOverview,
)



data class TotalsOverview(
    val correctAnswers: Int,
    val outOfCount: Int,
    val scorePercent: Float,
)

data class CategoriesOverview(
    val superCategories: List<FlagSuperCategory>,
    val subCategories: List<FlagCategory>,
)

data class TimeOverview(
    val timeMode: TimeMode,
    val timeTrialStart: Int?,
    val time: Int,
)


/* ------------ Score details classes ------------ */
abstract class TitledList(
    val list: List<FlagView>,
    val sortedList: List<FlagView>,
)

class GuessedFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)

class SkippedGuessedFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)

class SkippedFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)

class ShownFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)

class FailedFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)

class RemainderFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)