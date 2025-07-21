package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import dev.aftly.flags.data.room.ScoreItem
import kotlinx.serialization.Serializable


@Serializable
enum class TimeMode(@StringRes val title: Int) {
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
    val flagsAll: List<FlagResources>,
    val flagsGuessed: List<FlagResources>,
    flagsGuessedSorted: List<FlagResources>,
    val flagsSkippedGuessed: List<FlagResources>,
    flagsSkippedGuessedSorted: List<FlagResources>,
    val flagsSkipped: List<FlagResources>,
    flagsSkippedSorted: List<FlagResources>,
    val flagsShown: List<FlagResources>,
    flagsShownSorted: List<FlagResources>,
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
        flagsAll = flagsAll,
        flagsGuessed = flagsGuessed,
        flagsSkippedGuessed = flagsSkippedGuessed,
        flagsSkipped = flagsSkipped,
        flagsShown = flagsShown,
    )

    fun isScoresEmpty() = flagsGuessed.isEmpty() && flagsSkippedGuessed.isEmpty() &&
            flagsSkipped.isEmpty() && flagsShown.isEmpty()
}


/* Extension function to return ScoreData from ScoreItem instance (Room Dao class)
 * (needs string sorting, so eg. call from ViewModel) */
fun ScoreItem.toScoreData(
    flagsGuessedSorted: List<FlagResources>,
    flagsSkippedGuessedSorted: List<FlagResources>,
    flagsSkippedSorted: List<FlagResources>,
    flagsShownSorted: List<FlagResources>,
): ScoreData = ScoreData(
    id = id,
    timestamp = timestamp,
    timeMode = timeMode,
    timerStart = timerStart,
    timerEnd = timerEnd,
    gameSuperCategories = gameSuperCategories.filterIsInstance<FlagSuperCategory>(),
    gameSubCategories = gameSubCategories,
    flagsAll = flagsAll,
    flagsGuessed = flagsGuessed,
    flagsGuessedSorted = flagsGuessedSorted,
    flagsSkippedGuessed = flagsSkippedGuessed,
    flagsSkippedGuessedSorted = flagsSkippedGuessedSorted,
    flagsSkipped = flagsSkipped,
    flagsSkippedSorted = flagsSkippedSorted,
    flagsShown = flagsShown,
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
    val list: List<FlagResources>,
    val sortedList: List<FlagResources>,
)

class GuessedFlags(
    list: List<FlagResources>,
    sortedList: List<FlagResources>,
) : TitledList(list, sortedList)

class SkippedGuessedFlags(
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