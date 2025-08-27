package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import dev.aftly.flags.data.room.scorehistory.ScoreItem
import dev.aftly.flags.ui.util.getFlagKeys
import dev.aftly.flags.ui.util.getFlagsFromKeys
import dev.aftly.flags.ui.util.superCategories
import kotlinx.serialization.Serializable


@Serializable
enum class TimeMode(@param:StringRes val title: Int) {
    STANDARD (title = R.string.time_mode_standard),
    TIME_TRIAL (title = R.string.time_mode_time_trial)
}


/* ------------ Game score top level class ------------ */
class ScoreData(
    val id: Int = 0,
    val timestamp: Long,
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
) {
    private val remainderFlags = flagsAll.filterNot { it in flagsGuessed }
        .filterNot { it in flagsSkipped }.filterNot { it in flagsShown }

    private val correctAnswers = flagsGuessed.size
    private val scorePercent = (correctAnswers.toFloat() / flagsAll.size.toFloat()) * 100f

    val scoreOverview = ScoreOverview(
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
    )

    fun isScoresEmpty() = flagsGuessed.isEmpty() && flagsSkipped.isEmpty() && flagsShown.isEmpty()
}


/* Extension function to return ScoreData from ScoreItem instance (Room Dao class)
 * (needs string sorting, so eg. call from ViewModel) */
fun ScoreItem.toScoreData(
    flagsGuessedSorted: List<FlagView>,
    flagsSkippedGuessedSorted: List<FlagView>,
    flagsSkippedSorted: List<FlagView>,
    flagsShownSorted: List<FlagView>,
): ScoreData = ScoreData(
    id = id,
    timestamp = timestamp,
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
)


/* ------------ Score overview classes ------------ */
data class ScoreOverview(
    val totalsOverview: TotalsOverview,
    val categoriesOverview: CategoriesOverview,
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

class RemainderFlags(
    list: List<FlagView>,
    sortedList: List<FlagView>,
) : TitledList(list, sortedList)