package dev.aftly.flags.ui.screen.game

import androidx.annotation.StringRes
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory

data class GameUiState(
    val allFlags: List<FlagResources> = DataSource.allFlagsList,
    val currentFlags: List<FlagResources> = allFlags,
    val currentSuperCategory: FlagSuperCategory = FlagSuperCategory.All,
    @StringRes val currentCategoryTitle: Int = currentSuperCategory.title,

    val totalFlagCount: Int = 0,
    val currentFlag: FlagResources = nullFlag, // TODO: Change to flagResource with actual resources?
    val currentFlagStrings: List<String> = emptyList(),

    val correctGuessCount: Int = 0,
    val isGuessedFlagCorrect: Boolean = false,
    val isGuessedFlagCorrectEvent: Boolean = false,
    val isGuessedFlagWrong: Boolean = false,
    val isGuessedFlagWrongEvent: Boolean = false,
    val nextFlagInSkipped: FlagResources? = null,
    val isGameOver: Boolean = false,
)