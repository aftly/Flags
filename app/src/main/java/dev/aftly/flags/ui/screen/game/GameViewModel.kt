package dev.aftly.flags.ui.screen.game

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.ScoreData
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var guessedFlags = mutableListOf<FlagResources>()
    private var skippedFlags = mutableListOf<FlagResources>()
    private var shownFlags = mutableListOf<FlagResources>()

    private var standardTimerJob: Job? = null
    private var timeTrialJob: Job? = null
    private var showAnswerButtonCountdownJob: Job? = null

    var userGuess by mutableStateOf(value = "")
        private set

    var userTimerInputMinutes by mutableStateOf(value = "")
        private set
    var userTimerInputSeconds by mutableStateOf(value = "")
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
    fun resetGame(timeTrial: Boolean = false) {
        if (timeTrial) {
            standardTimerJob?.cancel()
            timeTrialJob?.cancel()
        }

        guessedFlags = mutableListOf()
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
                standardTimer = 0,
                correctGuessCount = 0,
                shownAnswerCount = 0,
                isGuessWrong = false,
                nextFlagInSkipped = null,
                isTimeTrial = timeTrial,
                isTimerPaused = false,
                isGameOver = false,
                isShowAnswer = false,
            )
        }

        startTimer()
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

    fun updateUserMinutesInput(newString: String) {
        userTimerInputMinutes = newString
    }
    fun updateUserSecondsInput(newString: String) {
        userTimerInputSeconds = newString
    }


    fun checkUserGuess() {
        cancelConfirmShowAnswer()

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
                    isGuessCorrect = true,
                    isGuessCorrectEvent = !it.isGuessCorrectEvent,
                    isGuessWrong = false,
                )
            }
            updateCurrentFlag()

        } else {
            userGuess = ""
            _uiState.update {
                it.copy(
                    isGuessCorrect = false,
                    isGuessWrong = true,
                    isGuessWrongEvent = !it.isGuessWrongEvent,
                )
            }
        }
    }


    fun skipFlag() {
        /* If from shown answer, manage related timer and button states and jobs */
        if (uiState.value.isShowAnswer) {
            _uiState.update {
                it.copy(isTimerPaused = false, isShowAnswer = false)
            }

            /* Start timer, to "unpause" it from showAnswer() timer "pause" */
            when (uiState.value.isTimeTrial) {
                true ->
                    startTimeTrial(
                        startTime = uiState.value.timeTrialTimer,
                        resume = true,
                    )
                false -> startTimer()
            }
        } else {
            cancelConfirmShowAnswer()
        }

        updateCurrentFlag(isSkip = true)
    }


    fun showAnswer() {
        cancelConfirmShowAnswer()

        _uiState.update {
            it.copy(
                isShowAnswer = true,
                shownAnswerCount = it.shownAnswerCount.inc(),
                isTimerPaused = true,
            )
        }
        shownFlags.add(uiState.value.currentFlag)

        /* Cancel timer, to "pause" it until flag skipped */
        when (uiState.value.isTimeTrial) {
            true -> timeTrialJob?.cancel()
            false -> standardTimerJob?.cancel()
        }
    }


    fun toggleTimeTrial() {
        _uiState.update { it.copy(isTimeTrialDialog = !it.isTimeTrialDialog) }
    }

    fun startTimeTrial(
        startTime: Int,
        resume: Boolean = false,
    ) {
        if (!resume) _uiState.update { it.copy(timeTrialStart = startTime) }
        _uiState.update { it.copy(timeTrialTimer = startTime) }

        timeTrialJob = viewModelScope.launch {
            while (uiState.value.timeTrialTimer > 0) {
                delay(timeMillis = 1000)
                _uiState.update { it.copy(timeTrialTimer = it.timeTrialTimer.dec()) }

                if (uiState.value.timeTrialTimer == 0) endGame()
            }
        }
    }


    fun confirmShowAnswer() {
        showAnswerButtonCountdownJob = viewModelScope.launch {
            _uiState.update {
                it.copy(showAnswerResetTimer = 5, isConfirmShowAnswer = true)
            }

            while (uiState.value.showAnswerResetTimer > 0) {
                delay(timeMillis = 1000)
                _uiState.update { it.copy(showAnswerResetTimer = it.showAnswerResetTimer.dec()) }
            }

            _uiState.update { it.copy(isConfirmShowAnswer = false) }
        }
    }


    fun endGame(isGameOver: Boolean = true) {
        when (isGameOver) {
            true -> {
                cancelConfirmShowAnswer()
                standardTimerJob?.cancel()
                timeTrialJob?.cancel()

                _uiState.update {
                    it.copy(
                        isGameOver = true,
                        scoreDetails = getScoreData(),
                    )
                }
            }
            false -> _uiState.update { it.copy(isGameOver = false) }
        }
    }


    fun toggleScoreDetails() {
        _uiState.update { it.copy(isScoreDetails = !it.isScoreDetails) }
    }


    /* For LaunchedEffect() when language configuration changes */
    fun setFlagStrings() {
        val currentFlag = uiState.value.currentFlag
        val currentFlagStrings = getStringsList(flag = currentFlag)
        _uiState.update { it.copy(currentFlagStrings = currentFlagStrings) }
    }


    private fun updateCurrentFlag(
        isSkip: Boolean = false,
    ) {
        val currentFlag: FlagResources = uiState.value.currentFlag

        if (isSkip) { /* If user skip & un-skipped flags remain, add flag to skip list */
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
            return if (newFlag == uiState.value.currentFlag || newFlag in guessedFlags ||
                newFlag in skippedFlags || newFlag in shownFlags) getRandomFlag() else newFlag
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


    private fun sortFlagsAlphabetically(flags: MutableList<FlagResources>): List<FlagResources> {
        val appResources = getApplication<Application>().applicationContext.resources

        return flags.sortedBy { flag ->
            normalizeString(string = appResources.getString(flag.flagOf))
        }
    }


    /* Reset and start standard timer */
    private fun startTimer() {
        standardTimerJob = viewModelScope.launch {
            while (true) {
                delay(timeMillis = 1000)
                _uiState.update { it.copy(standardTimer = it.standardTimer.inc()) }
            }
        }
    }


    private fun cancelConfirmShowAnswer() {
        showAnswerButtonCountdownJob?.cancel()
        _uiState.update { it.copy(isConfirmShowAnswer = false) }
    }


    /* Return ScoreData from current game state */
    private fun getScoreData(): ScoreData {
        return ScoreData(
            gameFlags = uiState.value.currentFlags,
            guessedFlags = guessedFlags.toList(),
            guessedFlagsSorted = sortFlagsAlphabetically(guessedFlags),
            skippedFlags = skippedFlags.toList(),
            skippedFlagsSorted = sortFlagsAlphabetically(skippedFlags),
            shownFlags = shownFlags.toList(),
            shownFlagsSorted = sortFlagsAlphabetically(shownFlags),
            isTimeTrial = uiState.value.isTimeTrial,
            timeTrialStart = when (uiState.value.isTimeTrial) {
                true -> uiState.value.timeTrialStart
                else -> null
            },
            timerTime = when (uiState.value.isTimeTrial) {
                true -> uiState.value.timeTrialTimer
                false -> uiState.value.standardTimer
            },
            timeStamp = 0, // TODO
        )
    }
}