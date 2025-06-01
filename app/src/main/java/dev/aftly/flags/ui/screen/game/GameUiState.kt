package dev.aftly.flags.ui.screen.game

import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class GameUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All,
    val currentSuperCategories: List<FlagSuperCategory>? = null,
    val currentSubCategories: List<FlagCategory>? = null,
    @StringRes val currentCategoryTitle: Int = currentSuperCategory.title,

    val totalFlagCount: Int = 0,
    val currentFlag: FlagResources = nullFlag,
    val currentFlagStrings: List<String> = emptyList(),

    val standardTimer: Int = 0,
    val timeTrialTimer: Int = 0,

    val correctGuessCount: Int = 0,
    val shownAnswerCount: Int = 0,
    val isGuessedFlagCorrect: Boolean = false,
    val isGuessedFlagCorrectEvent: Boolean = false,
    val isGuessedFlagWrong: Boolean = false,
    val isGuessedFlagWrongEvent: Boolean = false,
    val nextFlagInSkipped: FlagResources? = null,
    val isShowAnswer: Boolean = false,

    val isTimeTrialDialog: Boolean = false,
    val isTimeTrial: Boolean = false,
    val isGameOver: Boolean = false,
    val endGameGuessedFlags: List<FlagResources> = emptyList(),
    val endGameSkippedFlags: List<FlagResources> = emptyList(),
    val endGameShownFlags: List<FlagResources> = emptyList(),
    val isScoreDetails: Boolean = false,
)