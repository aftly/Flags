package dev.aftly.flags.ui.screen.game

import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.ScoreData

data class GameUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),

    val totalFlagCount: Int = 0,
    val currentFlag: FlagResources = nullFlag,
    val currentFlagStrings: List<String> = emptyList(),

    val isTimeTrialDialog: Boolean = false,
    val isTimeTrial: Boolean = false,
    val isTimerPaused: Boolean = false,
    val timerStandard: Int = 0,
    val timerTimeTrial: Int = 0,
    val timeTrialStartTime: Int = 0,

    val correctGuessCount: Int = 0,
    val shownAnswerCount: Int = 0,
    val isGuessCorrect: Boolean = false,
    val isGuessCorrectEvent: Boolean = false,
    val isGuessWrong: Boolean = false,
    val isGuessWrongEvent: Boolean = false,
    val nextFlagInSkipped: FlagResources? = null,

    val timerShowAnswerReset: Int = 5,
    val isConfirmShowAnswer: Boolean = false,
    val isShowAnswer: Boolean = false,

    val isGame: Boolean = true,
    val isGameOver: Boolean = false,
    val isGameOverDialog: Boolean = false,
    val isSaveScoreInit: Boolean = false,
    val isScoreDetails: Boolean = false,
    val scoreDetails: ScoreData? = null,
)