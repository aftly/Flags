package dev.aftly.flags.ui.screen.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dev.aftly.flags.FlagsApplication
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.nullFlag
import dev.aftly.flags.data.room.savedflags.SavedFlag
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode
import dev.aftly.flags.model.game.ScoreData
import dev.aftly.flags.model.game.TimeMode
import dev.aftly.flags.ui.util.getFlagsFromCategories
import dev.aftly.flags.ui.util.getFlagsFromCategory
import dev.aftly.flags.ui.util.getSavedFlagView
import dev.aftly.flags.ui.util.isSubCategoryExit
import dev.aftly.flags.ui.util.isSuperCategoryExit
import dev.aftly.flags.ui.util.normalizeLower
import dev.aftly.flags.ui.util.normalizeString
import dev.aftly.flags.ui.util.sortFlagsAlphabetically
import dev.aftly.flags.ui.util.updateCategoriesFromSub
import dev.aftly.flags.ui.util.updateCategoriesFromSuper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar


class GameViewModel(app: Application) : AndroidViewModel(application = app) {
    private val scoreItemsRepository =
        (application as FlagsApplication).container.scoreItemsRepository
    private val savedFlagsRepository =
        (application as FlagsApplication).container.savedFlagsRepository

    private val _uiState = MutableStateFlow(value = GameUiState())
    val uiState = _uiState.asStateFlow()
    private val _savedFlagsState = MutableStateFlow(value = emptySet<SavedFlag>())
    val savedFlagsState = _savedFlagsState.asStateFlow()

    private var guessedFlags = mutableListOf<FlagView>()
    private var skippedGuessedFlags = mutableListOf<FlagView>()
    private var skippedFlags = mutableListOf<FlagView>()
    private var shownFlags = mutableListOf<FlagView>()
    private var failedFlags = mutableListOf<FlagView>()

    private var timerStandardJob: Job? = null
    private var timerTimeTrialJob: Job? = null
    private var timerShowAnswerJob: Job? = null

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

        /* Initialize savedFlags list */
        viewModelScope.launch {
            savedFlagsRepository.getAllFlagsStream().first().let { savedFlags ->
                _savedFlagsState.value = savedFlags.toSet()
            }
        }
    }


    /* Reset game state with a new flag and necessary starting values */
    fun resetGame(
        startGame: Boolean = true,
        isTimeTrial: Boolean = false,
    ) {
        guessedFlags = mutableListOf()
        skippedGuessedFlags = mutableListOf()
        skippedFlags = mutableListOf()
        shownFlags = mutableListOf()
        failedFlags = mutableListOf()
        userGuess = ""
        cancelTimers()
        cancelConfirmShowAnswer()


        val newFlag = getRandomFlag(isNewGame = true)
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
                isGuessWrong = false,
                nextFlagInSkipped = null,
                isTimerPaused = false,
                timerStandard = 0,
                answersRemaining = it.difficultyMode.guessLimit,
                isGame = if (it.currentFlags.isNotEmpty()) startGame else false,
                isGameOver = false,
                isGameOverDialog = false,
                isShowAnswer = false,
                isSaveScoreInit = false,
                scoreDetails = null,
            )
        }

        if (!isTimeTrial) {
            _uiState.update {
                it.copy(
                    isTimeTrial = false,
                    timerTimeTrial = 0,
                    timeTrialStartTime = 0
                )
            }

            if (uiState.value.isGame) {
                timerStandardJob = viewModelScope.launch { startTimerStandard() }
            }
        }
    }


    /* To enable buttons, show flag and start timer after resetGame() without startGame = true */
    fun startGame() {
        if (!uiState.value.isGame && uiState.value.currentFlags.isNotEmpty()) {
            val isTimeTrial = uiState.value.isTimeTrial

            _uiState.update { it.copy(isGame = true) }

            if (isTimeTrial && isJobInactive(timerTimeTrialJob)) {
                timerTimeTrialJob = viewModelScope.launch { startTimerTimeTrial() }

            } else if (!isTimeTrial && isJobInactive(timerStandardJob)) {
                timerStandardJob = viewModelScope.launch { startTimerStandard() }
            }
        }
    }


    fun updateGameModes(
        answerMode: AnswerMode,
        difficultyMode: DifficultyMode,
    ) {
        if (answerMode != uiState.value.answerMode) {
            /* Reset game with default categories */
            updateCurrentCategory(
                newSuperCategory = FlagSuperCategory.SovereignCountry,
                newSubCategory = null,
                answerModeNew = answerMode,
            )
        }

        if (difficultyMode != uiState.value.difficultyMode) {
            _uiState.update { it.copy(difficultyMode = difficultyMode) }

            /* Reset game depending on time trial */
            if (uiState.value.isTimeTrial) {
                setTimeTrial(startTime = uiState.value.timeTrialStartTime)
            } else {
                resetGame()
            }
        }
    }


    fun updateCurrentCategory(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
        answerModeNew: AnswerMode? = null,
    ) {
        val allFlags = uiState.value.allFlags
        val answerMode = answerModeNew ?: uiState.value.answerMode
        val difficultyMode = uiState.value.difficultyMode
        val isTimeTrial = uiState.value.isTimeTrial
        val timeTrialStartTime = uiState.value.timeTrialStartTime
        val isDatesMode = answerMode == AnswerMode.DATES

        /* If the new category is the All super category set state with default values (which
         * includes allFlagsList), else dynamically generate flags list from category info */
        if (newSuperCategory == All || isDatesMode) {
            _uiState.value = GameUiState(
                currentFlags = if (isDatesMode) getDatesFlags(allFlags) else allFlags,
                answerMode = answerMode,
                difficultyMode = difficultyMode,
            )

        } else {
            _uiState.value = GameUiState(
                currentFlags = getFlagsFromCategory(
                    superCategory = newSuperCategory,
                    subCategory = newSubCategory,
                    allFlags = allFlags,
                ),
                currentSuperCategories = buildList {
                    newSuperCategory?.let { add(it) }
                },
                currentSubCategories = buildList {
                    newSubCategory?.let { add(it) }
                },
                answerMode = answerMode,
                difficultyMode = difficultyMode,
            )
        }

        if (isTimeTrial) setTimeTrial(startTime = timeTrialStartTime)
        else resetGame()
    }


    fun updateCurrentCategories(
        newSuperCategory: FlagSuperCategory?,
        newSubCategory: FlagCategory?,
    ) {
        /* Controls whether flags are updated from current or all flags */
        var isDeselectSwitch = false to false

        val newSuperCategories = uiState.value.currentSuperCategories.toMutableList()
        val newSubCategories = uiState.value.currentSubCategories.toMutableList()

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        newSuperCategory?.let { superCategory ->
            if (isSuperCategoryExit(superCategory, newSuperCategories, newSubCategories)) return

            isDeselectSwitch = updateCategoriesFromSuper(
                superCategory = superCategory,
                superCategories = newSuperCategories,
                subCategories = newSubCategories,
            ) to false
        }
        newSubCategory?.let { subCategory ->
            if (isSubCategoryExit(subCategory, newSubCategories, newSuperCategories)) return

            isDeselectSwitch = updateCategoriesFromSub(
                subCategory = subCategory,
                subCategories = newSubCategories,
            )
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselectSwitch.first) {
            when (newSuperCategories.size to newSubCategories.size) {
                0 to 0 -> return updateCurrentCategory(
                    newSuperCategory = All, newSubCategory = null
                )
                1 to 0 -> return updateCurrentCategory(
                    newSuperCategory = newSuperCategories.first(), newSubCategory = null
                )
                1 to 1 -> {
                    val superCategory = newSuperCategories.first()
                    val subCategory = newSubCategories.first()

                    if (superCategory.subCategories.size == 1 &&
                        superCategory.firstCategoryEnumOrNull() == subCategory) {
                        return updateCurrentCategory(
                            newSuperCategory = superCategory, newSubCategory = null
                        )
                    }
                }
            }
        }

        /* Update state with new categories lists and currentFlags list */
        val allFlags = uiState.value.allFlags
        val currentFlags = uiState.value.currentFlags
        val answerMode = uiState.value.answerMode
        val difficultyMode = uiState.value.difficultyMode
        val isTimeTrial = uiState.value.isTimeTrial
        val timeTrialStartTime = uiState.value.timeTrialStartTime
        _uiState.value = GameUiState(
            /* Get new flags list from categories lists and either currentFlags or allFlags
             * (depending on select vs. deselect) */
            currentFlags = getFlagsFromCategories(
                allFlags = allFlags,
                currentFlags = currentFlags,
                isDeselectSwitch = isDeselectSwitch,
                superCategory = newSuperCategory,
                superCategories = newSuperCategories,
                subCategories = newSubCategories,
            ),
            currentSuperCategories = newSuperCategories,
            currentSubCategories = newSubCategories,
            answerMode = answerMode,
            difficultyMode = difficultyMode,
        )

        if (isTimeTrial) setTimeTrial(startTime = timeTrialStartTime, startGame = false)
        else resetGame(startGame = false)
    }


    fun selectSavedFlags() {
        _uiState.update {
            it.copy(
                currentFlags = getSavedFlagView(savedFlags = savedFlagsState.value),
                currentSuperCategories = emptyList(),
                currentSubCategories = emptyList(),
            )
        }
        resetGame()
    }


    fun isScoresEmpty(): Boolean =
        guessedFlags.isEmpty() && skippedFlags.isEmpty() && shownFlags.isEmpty()


    fun updateUserGuess(newString: String) {
        userGuess = newString
    }

    fun checkUserGuess() {
        cancelConfirmShowAnswer()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val the = application.resources.getString(R.string.string_the_whitespace)
        val normalizedUserGuess = normalizeString(
            string = userGuess.lowercase().removePrefix(the)
        )

        val normalizedAnswers = when (uiState.value.answerMode) {
            AnswerMode.NAMES ->
                uiState.value.currentFlagStrings.map {
                    normalizeLower(string = it)
                }
            AnswerMode.DATES ->
                uiState.value.currentFlags.flatMap { flag ->
                    buildList {
                        flag.fromYear?.let { add(it.toString()) }
                        flag.toYear?.let {
                            if (it == 0) add(currentYear.toString())
                            else add(it.toString())
                        }

                        if (flag.fromYear != null && flag.toYear != null) {
                            val toYear = if (flag.toYear == 0) currentYear else flag.toYear
                            val fromToString = "${flag.fromYear} - $toYear"

                            add(fromToString)
                        }
                    }
                }.map { normalizeLower(string = it) }
        }

        if (normalizedUserGuess in normalizedAnswers) {
            /* If correct answer */
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
            /* If incorrect answer */
            val answersRemaining = uiState.value.answersRemaining
            val isUpdateFlag = answersRemaining == 1
            val isSuddenDeath = answersRemaining == 0

            if (isUpdateFlag) {
                updateCurrentFlag(isFail = true)
            } else if (isSuddenDeath) {
                return endGame()
            }

            userGuess = ""
            _uiState.update {
                it.copy(
                    isGuessCorrect = false,
                    isGuessWrong = true,
                    isGuessWrongEvent = !it.isGuessWrongEvent,
                    answersRemaining =
                        if (isUpdateFlag) it.answersRemaining else answersRemaining?.dec(),
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

            /* Start relevant timer, to unpause it from showAnswer()'s pause */
            when (uiState.value.isTimeTrial) {
                true -> if (isJobInactive(timerTimeTrialJob)) {
                    timerTimeTrialJob = viewModelScope.launch { startTimerTimeTrial() }
                }
                false -> if (isJobInactive(timerStandardJob)) {
                    timerStandardJob = viewModelScope.launch { startTimerStandard() }
                }
            }

            updateCurrentFlag(isShowAnswer = true)

        } else {
            cancelConfirmShowAnswer()
            updateCurrentFlag(isSkip = true)
        }
    }


    fun showAnswer() {
        /* Cancel any/all timers */
        cancelTimers()
        cancelConfirmShowAnswer()

        shownFlags.add(uiState.value.currentFlag)
        _uiState.update {
            it.copy(
                isShowAnswer = true,
                shownAnswerCount = shownFlags.size,
                isTimerPaused = true,
            )
        }
    }

    fun confirmShowAnswer() {
        timerShowAnswerJob = viewModelScope.launch {
            _uiState.update {
                it.copy(timerShowAnswerReset = 5, isConfirmShowAnswer = true)
            }

            while (uiState.value.timerShowAnswerReset > 0) {
                delay(timeMillis = 1000)
                _uiState.update { it.copy(timerShowAnswerReset = it.timerShowAnswerReset.dec()) }
            }

            _uiState.update { it.copy(isConfirmShowAnswer = false) }
        }
    }


    fun endGame(isGameOver: Boolean = true) {
        if (isGameOver) {
            cancelTimers()
            cancelConfirmShowAnswer()
        }

        saveScoreInit()
        _uiState.update {
            it.copy(isGameOver = isGameOver, isGameOverDialog = isGameOver)
        }
    }


    fun setTimeTrial(
        startTime: Int,
        startGame: Boolean = true,
    ) {
        /* Cancel timers and reset game */
        cancelTimers()
        cancelConfirmShowAnswer()
        resetGame(startGame = startGame, isTimeTrial = true)

        _uiState.update {
            it.copy(
                isTimeTrial = true,
                timerTimeTrial = startTime,
                timeTrialStartTime = startTime,
            )
        }

        if (uiState.value.isGame) {
            timerTimeTrialJob = viewModelScope.launch { startTimerTimeTrial() }
        }
    }

    fun updateUserMinutesInput(newString: String) {
        /* Only update if 2 characters or less AND a number OR empty */
        if ((newString.toIntOrNull() != null || newString == "") && newString.length <= 2) {
            userTimerInputMinutes = newString
        }
    }
    fun updateUserSecondsInput(newString: String) {
        /* Only update if 2 characters or less AND empty OR a number of 60 or less */
        if ((newString.toIntOrNull()?.let { return@let it <= 60 } == true || newString == "") &&
            newString.length <= 2) {
            userTimerInputSeconds = newString
        }
    }


    /* Toggle game dialogs */
    fun toggleTimeTrialDialog(on: Boolean) {
        _uiState.update { it.copy(isTimeTrialDialog = on) }
    }

    fun toggleGameModesDialog(on: Boolean) {
        _uiState.update { it.copy(isGameModesDialog = on) }
    }

    fun toggleGameOverDialog(on: Boolean) {
        _uiState.update { it.copy(isGameOverDialog = on) }
    }

    fun toggleScoreDetails(on: Boolean) {
        _uiState.update { it.copy(isScoreDetails = on) }
    }

    fun toggleConfirmExitDialog(on: Boolean) {
        _uiState.update { it.copy(isConfirmExitDialog = on) }
    }


    /* For LaunchedEffect() when language configuration changes */
    fun setFlagStrings() {
        val currentFlag = uiState.value.currentFlag
        val currentFlagStrings = getStringsList(flag = currentFlag)
        _uiState.update { it.copy(currentFlagStrings = currentFlagStrings) }
    }


    /* Filter flags with non-null date parameters */
    private fun getDatesFlags(flags: List<FlagView>) = flags.filter { it.isDated }


    private fun updateCurrentFlag(
        isSkip: Boolean = false,
        isShowAnswer: Boolean = false,
        isFail: Boolean = false,
    ) {
        val currentFlag: FlagView = uiState.value.currentFlag

        if (isSkip) { /* If user skip & un-skipped flags remain, add flag to skip list */
            if (!isSkipMax()) skippedFlags.add(currentFlag)

        } else if (isFail) {
            failedFlags.add(currentFlag)
            _uiState.update { it.copy(failedAnswerCount = failedFlags.size) }

        } else { /* If user guess, add flag to guessed set */
            if (!isShowAnswer) guessedFlags.add(currentFlag)

            /* If flag in skipped list, move it into skippedGuessed list */
            if (currentFlag in skippedFlags) {
                skippedFlags.remove(currentFlag)
                if (!isShowAnswer) skippedGuessedFlags.add(currentFlag)
            }
        }

        /* If all flags guessed, end the game */
        if (guessedFlags.size + shownFlags.size + failedFlags.size >=
            uiState.value.currentFlags.size) return endGame()

        /* If no un-skipped flags remain get skipped flag, else get random flag */
        val newFlag = if (!isFail && isSkipMax()) getSkippedFlag() else getRandomFlag()
        val newFlagStrings = getStringsList(newFlag)

        _uiState.update {
            it.copy(
                currentFlag = newFlag,
                currentFlagStrings = newFlagStrings,
                answersRemaining = it.difficultyMode.guessLimit,
            )
        }
    }


    private fun isSkipMax(): Boolean {
        return shownFlags.size + skippedFlags.size + guessedFlags.size ==
                uiState.value.currentFlags.size
    }


    private fun getRandomFlag(isNewGame: Boolean = false): FlagView {
        val currentFlags = uiState.value.currentFlags

        /* To stop recursive loop app crash when current flag state carries across games and
         * currentFlags size of 1 (as newFlag needs to be != currentFlag) */
        if (isNewGame) {
            _uiState.update { it.copy(currentFlag = nullFlag) }
        }

        return if (currentFlags.isNotEmpty()) {
            val newFlag = currentFlags.random()
            val newFlagExclusions = buildList {
                add(uiState.value.currentFlag)
                addAll(elements =
                    listOf(guessedFlags, skippedFlags, shownFlags, failedFlags).flatten()
                )
            }
            /* Recurs until (random) newFlag not exclusive */
            if (newFlag in newFlagExclusions) getRandomFlag() else newFlag

        } else {
            nullFlag
        }
    }


    /* Function responsible for updating the nextFlagInSkipped state and returning the pre-update value.
     * If nextFlagInSkipped is @ end of skippedFlags list, state is updated to skippedFlags[0].
     * If nextFlagInSkipped is null, (ie. on first time call,) returns skippedFlags[0].
     * If nextFlagInSkipped is null, is set to skippedFlags[0] if size of 1, else [1]. */
    private fun getSkippedFlag(): FlagView {
        val nextFlagInSkipped = uiState.value.nextFlagInSkipped

        return if (nextFlagInSkipped != null) { // Ie. After first time call to getSkippedFlag()
            val nextFlagIndex = skippedFlags.indexOf(nextFlagInSkipped)
            val isListEnd = nextFlagIndex == skippedFlags.size - 1

            if (isListEnd) {
                _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[0]) }
            } else {
                _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[nextFlagIndex + 1]) }
            }

            nextFlagInSkipped

        } else { // On first time call to getSkippedFlag()
            val nextIndex = if (skippedFlags.size == 1) 0 else 1
            _uiState.update { it.copy(nextFlagInSkipped = skippedFlags[nextIndex]) }

            skippedFlags[0]
        }
    }


    private fun getStringsList(flag: FlagView): List<String> {
        val appRes = application.resources
        val mutableList: MutableList<String> = mutableListOf()

        mutableList.add(appRes.getString(flag.flagOf))
        mutableList.add(appRes.getString(flag.flagOfOfficial))

        flag.flagOfAlternate.forEach { resId ->
            mutableList.add(appRes.getString(resId))
        }
        return mutableList.toList()
    }


    private fun sortFlags(flags: MutableList<FlagView>): List<FlagView> =
        sortFlagsAlphabetically(application, flags)


    private fun cancelTimers() {
        timerStandardJob?.cancel()
        timerTimeTrialJob?.cancel()
    }

    private fun cancelConfirmShowAnswer() {
        timerShowAnswerJob?.cancel()
        _uiState.update { it.copy(isConfirmShowAnswer = false) }
    }


    /* Return ScoreData from current game state */
    private fun getScoreData(): ScoreData {
        return ScoreData(
            timestamp = System.currentTimeMillis(),
            answerMode = uiState.value.answerMode,
            difficultyMode = uiState.value.difficultyMode,
            timeMode = when (uiState.value.isTimeTrial) {
                true -> TimeMode.TIME_TRIAL
                false -> TimeMode.STANDARD
            },
            timerStart = when (uiState.value.isTimeTrial) {
                true -> uiState.value.timeTrialStartTime
                else -> null
            },
            timerEnd = when (uiState.value.isTimeTrial) {
                true -> uiState.value.timerTimeTrial
                false -> uiState.value.timerStandard
            },
            gameSuperCategories = getScoreDataSupers(),
            gameSubCategories = getScoreDataSubs(),
            flagsAll = uiState.value.currentFlags,
            flagsGuessed = guessedFlags.toList(),
            flagsGuessedSorted = sortFlags(guessedFlags),
            flagsSkippedGuessed = skippedGuessedFlags.toList(),
            flagsSkippedGuessedSorted = sortFlags(skippedGuessedFlags),
            flagsSkipped = skippedFlags.toList(),
            flagsSkippedSorted = sortFlags(skippedFlags),
            flagsShown = shownFlags.toList(),
            flagsShownSorted = sortFlags(shownFlags),
            flagsFailed = failedFlags.toList(),
            flagsFailedSorted = sortFlags(failedFlags),
        )
    }


    /* Remove redundant super categories for ScoreData */
    private fun getScoreDataSupers(): List<FlagSuperCategory> {
        val superCategories = uiState.value.currentSuperCategories
        val subCategories = uiState.value.currentSubCategories
        val isNonAllCategories =
            superCategories.size > 1 || superCategories.isNotEmpty() && subCategories.isNotEmpty()

        return superCategories.filterNot { superCategory ->
            superCategory.enums().any { it in subCategories } &&
                    (superCategory != All || isNonAllCategories)
        }
    }

    /* Remove redundant sub categories for ScoreData */
    private fun getScoreDataSubs(): List<FlagCategory> {
        return uiState.value.currentSubCategories
        // TODO
        /*
        return uiState.value.currentSubCategories?.let { currentSubCategories ->
            val subCategories = currentSubCategories.toMutableList()

            /* Get list of subcategory lists from supercategories, filter out lists of size > 1,
             * remove any results from game subcategories results */
            uiState.value.currentSuperCategories
                ?.map { it.enums() }
                ?.filterNot { it.size > 1 }
                ?.map { it.first() }
                ?.forEach { subCategories.remove(it) }

            return@let subCategories

        } ?: emptyList()
         */
    }


    /* Called only with initial call to end game to ensure saved to db only once */
    private fun saveScoreInit() {
        if (!uiState.value.isSaveScoreInit) {
            viewModelScope.launch {
                _uiState.update { it.copy(scoreDetails = getScoreData(), isSaveScoreInit = true) }
                saveScoreToDb()
            }
        }
    }


    /* Save a ScoreItem to scoreItemsRepository from ScoreData instance */
    private suspend fun saveScoreToDb() {
        uiState.value.scoreDetails?.let { scoreData ->
            if (!scoreData.isScoresEmpty()) {
                scoreItemsRepository.insertItem(scoreData.toScoreItem())
            }
        }
    }


    /* Get if job is not active or is null */
    private fun isJobInactive(job: Job?): Boolean = job == null || !job.isActive

    /* Start/resume Standard timer */
    private suspend fun startTimerStandard() {
        while (true) {
            delay(timeMillis = 1000)
            _uiState.update { it.copy(timerStandard = it.timerStandard.inc()) }
        }
    }

    /* Start/resume Time Trial timer */
    private suspend fun startTimerTimeTrial() {
        while (uiState.value.timerTimeTrial > 0) {
            delay(timeMillis = 1000)
            _uiState.update { it.copy(timerTimeTrial = it.timerTimeTrial.dec()) }
        }
        endGame()
    }
}