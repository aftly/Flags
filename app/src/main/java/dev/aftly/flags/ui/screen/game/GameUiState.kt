package dev.aftly.flags.ui.screen.game

import dev.aftly.flags.data.DataSource.allFlagsList
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.ScoreData
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode

data class GameUiState(
    val allFlags: List<FlagView> = allFlagsList,
    val currentFlags: List<FlagView> = allFlagsList,
    val currentSuperCategories: List<FlagSuperCategory> = listOf(FlagSuperCategory.All),
    val currentSubCategories: List<FlagCategory> = emptyList(),

    val totalFlagCount: Int = 0,
    val currentFlag: FlagView = nullFlag,
    val currentFlagStrings: List<String> = emptyList(),

    val isConfirmExitDialog: Boolean = false,
    val isConfirmResetDialog: Boolean = false,

    val isTimeTrialDialog: Boolean = false,
    val isTimeTrial: Boolean = false,
    val timerStandard: Int = 0,
    val timerTimeTrial: Int = 0,
    val timeTrialStartTime: Int = 0,

    val correctGuessCount: Int = 0,
    val shownAnswerCount: Int = 0,
    val failedAnswerCount: Int = 0,
    val isGuessCorrect: Boolean = false,
    val isGuessCorrectEvent: Boolean = false,
    val isGuessWrong: Boolean = false,
    val isGuessWrongEvent: Boolean = false,
    val nextFlagInSkipped: FlagView? = null,

    val timerShowAnswerReset: Int = 5,
    val isConfirmShowAnswer: Boolean = false,
    val isShowAnswer: Boolean = false,

    val isGameModesDialog: Boolean = false,
    val answerMode: AnswerMode = AnswerMode.NAMES,
    val difficultyMode: DifficultyMode = DifficultyMode.EASY,
    val answersRemaining: Int? = null,

    val isGame: Boolean = true,
    val isGamePaused: Boolean = false,
    val isGameOver: Boolean = false,
    val isGameOverDialog: Boolean = false,
    val isSaveScoreInit: Boolean = false,
    val isScoreDetails: Boolean = false,
    val scoreDetails: ScoreData? = null,
)