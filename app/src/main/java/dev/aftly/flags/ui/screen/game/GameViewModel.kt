package dev.aftly.flags.ui.screen.game

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.ui.util.getCategoryTitle
import dev.aftly.flags.ui.util.getFlagsByCategory
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getParentSuperCategory
import dev.aftly.flags.ui.util.getSubCategories
import dev.aftly.flags.ui.util.getSuperCategories
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeString
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var guessedFlags = mutableSetOf<FlagResources>()
    private var skippedFlags = mutableListOf<FlagResources>()
    private var shownFlags = mutableListOf<FlagResources>()

    var userGuess by mutableStateOf(value = "")
        private set

    /* Initialise ViewModel & State with category and other game related info */
    init {
        /* updateCurrentCategory also calls resetGame() */
        updateCurrentCategory(
            newSuperCategory = FlagSuperCategory.SovereignCountry,
            newSubCategory = null,
        )
    }


    /* Reset game state with a new flag and necessary starting values */
    fun resetGame() {
        guessedFlags = mutableSetOf()
        skippedFlags = mutableListOf()
        shownFlags = mutableListOf()
        userGuess = ""

        val newFlag = getRandomFlag()
        val newFlagStrings = when (newFlag) {
            nullFlag -> emptyList()
            else -> getStringsList(flag = newFlag)
        }

        _uiState.update {
            it.copy(
                totalFlagCount = it.currentFlags.size,
                currentFlag = newFlag,
                currentFlagStrings = newFlagStrings,
                correctGuessCount = 0,
                shownAnswerCount = 0,
                isGuessedFlagWrong = false,
                nextFlagInSkipped = null,
                isGameOver = false,
                isShowAnswer = false,
            )
        }
    }


    fun updateCurrentCategory(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* If the new category is the All super category set state with default values (which
         * includes allFlagsList), else dynamically generate flags list from category info */
        if (newSuperCategory == FlagSuperCategory.All) {
            _uiState.value = GameUiState()
            resetGame()

        } else {
            @StringRes val categoryTitle = getCategoryTitle(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Determine the relevant parent superCategory */
            val parentSuperCategory = getParentSuperCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
            )

            /* Get new flags list from function arguments and parent superCategory */
            val newFlags = getFlagsByCategory(
                superCategory = newSuperCategory,
                subCategory = newSubCategory,
                allFlags = uiState.value.allFlags,
                parentCategory = parentSuperCategory,
            )

            _uiState.value = GameUiState(
                currentFlags = newFlags,
                currentSuperCategory = parentSuperCategory,
                currentCategoryTitle = categoryTitle,
            )
            resetGame()
        }
    }


    fun updateCurrentCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        var isDeselect = false /* Controls whether flags are updated from current or all flags */

        val superCategories = getSuperCategories(
            superCategories = uiState.value.currentSuperCategories,
            currentSuperCategory = uiState.value.currentSuperCategory,
        )
        val subCategories = getSubCategories(
            subCategories = uiState.value.currentSubCategories,
            currentSuperCategory = uiState.value.currentSuperCategory,
        )

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        newSuperCategory?.let { superCategory ->
            if (isSuperCategoryExit(superCategory, superCategories, subCategories)) return

            isDeselect = updateCategoriesFromSuper(
                superCategory = superCategory,
                superCategories = superCategories,
                subCategories = subCategories,
            )
            /* Exit after All is deselected since (how) deselection is state inconsequential */
            if (!isDeselect && superCategory == All) return
        }
        newSubCategory?.let { subCategory ->
            if (isSubCategoryExit(subCategory, subCategories, superCategories)) return

            isDeselect = updateCategoriesFromSub(
                subCategory = subCategory,
                subCategories = subCategories,
            )
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselect) {
            if (subCategories.isEmpty()) {
                if (superCategories.isEmpty()) {
                    return updateCurrentCategory(newSuperCategory = All, newSubCategory = null)

                } else if (superCategories.size == 1) {
                    return updateCurrentCategory(
                        newSuperCategory = superCategories.first(), newSubCategory = null
                    )
                }
            } else if (subCategories.size == 1 && superCategories.size == 1 &&
                superCategories.first().subCategories.size == 1 &&
                subCategories.first() == superCategories.first().subCategories.first()) {
                return updateCurrentCategory(
                    newSuperCategory = superCategories.first(), newSubCategory = null
                )
            }
        }


        /* Get new flags list from categories list and currentFlags or allFlags (depending on
         * processing needs) */
        val newFlags = getFlagsFromCategories(
            allFlags = uiState.value.allFlags,
            currentFlags = uiState.value.currentFlags,
            isDeselect = isDeselect,
            newSuperCategory = newSuperCategory,
            superCategories = superCategories,
            subCategories = subCategories,
        )


        /* Update state with new categories lists and currentFlags list */
        _uiState.value = GameUiState(
            currentFlags = newFlags,
            currentSuperCategories = superCategories,
            currentSubCategories = subCategories,
        )
        resetGame()
    }


    fun updateUserGuess(newString: String) {
        userGuess = newString
    }


    fun checkUserGuess() {
        val normalizedUserGuess = getNormalizedString(string = userGuess)
        val answers = uiState.value.currentFlagStrings
        val normalizedAnswers = mutableListOf<String>()

        answers.forEach { answer ->
            normalizedAnswers.add(getNormalizedString(string = answer))
        }

        if (normalizedUserGuess in normalizedAnswers) {
            userGuess = ""
            _uiState.update {
                it.copy(
                    correctGuessCount = it.correctGuessCount.inc(),
                    isGuessedFlagCorrect = true,
                    isGuessedFlagCorrectEvent = !it.isGuessedFlagCorrectEvent,
                    isGuessedFlagWrong = false,
                )
            }
            updateCurrentFlag()

        } else {
            userGuess = ""
            _uiState.update {
                it.copy(
                    isGuessedFlagCorrect = false,
                    isGuessedFlagWrong = true,
                    isGuessedFlagWrongEvent = !it.isGuessedFlagWrongEvent,
                )
            }
        }
    }


    fun skipFlag(isAnswerShown: Boolean) {
        updateCurrentFlag(
            isSkip = true,
            isAnswerShown = isAnswerShown,
        )
    }


    fun showAnswer() {
        _uiState.update { it.copy(isShowAnswer = true) }
    }


    fun endGame(isGameOver: Boolean = true) {
        when (isGameOver) {
            true -> _uiState.update { it.copy(isGameOver = true) }
            false -> _uiState.update { it.copy(isGameOver = false) }
        }
    }


    /* For LaunchedEffect() when language configuration changes */
    fun setFlagStrings() {
        val currentFlag = uiState.value.currentFlag
        val currentFlagStrings = getStringsList(flag = currentFlag)
        _uiState.update { it.copy(currentFlagStrings = currentFlagStrings) }
    }


    private fun updateCurrentFlag(
        isSkip: Boolean = false,
        isAnswerShown: Boolean = false,
    ) {
        val currentFlag: FlagResources = uiState.value.currentFlag

        /* Manage shown, skipped and guessed flags lists depending on args & game conditions */
        if (isAnswerShown) {
            shownFlags.add(currentFlag)
            _uiState.update {
                it.copy(
                    shownAnswerCount = it.shownAnswerCount.inc(),
                    isShowAnswer = false,
                )
            }
        } else if (isSkip) { /* If user skip & un-skipped flags remain, add flag to skip list */
            if (!isSkipMax()) skippedFlags.add(currentFlag)

        } else { /* If user guess, add flag to guessed set */
            guessedFlags.add(currentFlag)

            /* If flag in skippedList, remove it */
            if (currentFlag in skippedFlags) skippedFlags.remove(currentFlag)
        }

        /* If all flags guessed, end the game */
        if (shownFlags.size + guessedFlags.size == uiState.value.currentFlags.size) return endGame()

        /* If no un-skipped flags remain get skipped flag, else get random flag */
        val newFlag = if (isSkipMax()) getSkippedFlag() else getRandomFlag()
        val newFlagStrings = getStringsList(newFlag)

        _uiState.update {
            it.copy(
                currentFlag = newFlag,
                currentFlagStrings = newFlagStrings,
            )
        }
    }


    private fun isSkipMax(): Boolean {
        return shownFlags.size + skippedFlags.size + guessedFlags.size ==
                uiState.value.currentFlags.size
    }


    private fun getRandomFlag(): FlagResources {
        val currentFlags = uiState.value.currentFlags

        if (currentFlags.isNotEmpty()) {
            val newFlag = currentFlags.random()

            /* Recurs until (random) newFlag not currentFlag or in guessedFlags or in skippedFlags */
            return if (newFlag == uiState.value.currentFlag ||
                newFlag in guessedFlags || newFlag in skippedFlags) getRandomFlag() else newFlag
        } else {
            return nullFlag
        }
    }


    /* Function responsible for updating the nextFlagInSkipped state and returning the pre-update value.
     * If nextFlagInSkipped is @ end of skippedFlags list, state is updated to skippedFlags[0].
     * If nextFlagInSkipped is null, (ie. on first time call,) returns skippedFlags[0].
     * If nextFlagInSkipped is null, is set to skippedFlags[0] if size of 1, else [1]. */
    private fun getSkippedFlag(): FlagResources {
        val nextFlagInSkipped = uiState.value.nextFlagInSkipped

        if (nextFlagInSkipped != null) { // Ie. After first time call to getSkippedFlag()
            val nextFlagIndex = skippedFlags.indexOf(nextFlagInSkipped)
            val isListEnd = nextFlagIndex == skippedFlags.size - 1

            if (isListEnd) {
                _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[0]) }
            } else {
                _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[nextFlagIndex + 1]) }
            }

            return nextFlagInSkipped

        } else { // On first time call to getSkippedFlag()
            val nextIndex = if (skippedFlags.size == 1) 0 else 1
            _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[nextIndex]) }

            return skippedFlags[0]
        }
    }

    private fun getStringsList(flag: FlagResources): List<String> {
        val appResources = getApplication<Application>().applicationContext.resources
        val mutableList: MutableList<String> = mutableListOf()

        mutableList.add(appResources.getString(flag.flagOf))
        mutableList.add(appResources.getString(flag.flagOfOfficial))

        if (flag.flagOfAlternate != null) {
            flag.flagOfAlternate.forEach { resId ->
                mutableList.add(appResources.getString(resId))
            }
        }
        return mutableList.toList()
    }

    /* For use in normalizing both userGuess and flag names for comparisons in checkUserGuess()
     * Deletes all usages of "the" and non-(standard-)alphabetic characters */
    private fun getNormalizedString(string: String): String {
        /* normalizeString() normalizes special alphabetic characters */
        return normalizeString(string = string).lowercase()
            /* Replaces all non-alphabetic characters with a whitespace */
            .replace(regex = Regex(pattern = "[^a-z]"), replacement = " ")
            /* Previous replace() allows any usages of the word "the" to be recognised & deleted */
            .replace(regex = Regex(pattern = "\\bthe\\b"), replacement = "")
            /* Deletes all whitespace-like characters */
            .replace(regex = Regex(pattern = "\\s"), replacement = "")
    }
}