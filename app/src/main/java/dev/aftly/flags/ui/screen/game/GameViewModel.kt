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
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagCategoryWrapper
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagSuperCategory.All
import dev.aftly.flags.model.FlagSuperCategory.SovereignCountry
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode
import dev.aftly.flags.model.game.ScoreData
import dev.aftly.flags.model.game.TimeMode
import dev.aftly.flags.ui.util.filterRedundantSuperCategories
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

    private val _guessedFlags = mutableListOf<FlagView>()
    val guessedFlags: List<FlagView> get() = _guessedFlags
    private val _skippedGuessedFlags = mutableListOf<FlagView>()
    private val _skippedFlags = mutableListOf<FlagView>()
    private val _shownFlags = mutableListOf<FlagView>()
    val shownFlags: List<FlagView> get() = _shownFlags
    private val _failedFlags = mutableListOf<FlagView>()
    val failedFlags: List<FlagView> get() = _failedFlags

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
        resetScreen()

        /* Initialize savedFlags list */
        viewModelScope.launch {
            savedFlagsRepository.getAllFlagsStream().first().let { savedFlags ->
                _savedFlagsState.value = savedFlags.toSet()
            }
        }
    }


    fun resetScreen() {
        /* updateCurrentCategory also calls resetGame() */
        updateCurrentCategory(
            category = SovereignCountry,
            answerModeNew = AnswerMode.NAMES,
            difficultyModeNew = DifficultyMode.EASY,
            timeModeNew = TimeMode.STANDARD,
        )
    }

    /* Reset game state with a new flag and necessary starting values */
    fun resetGame(
        startGame: Boolean = true,
        answerMode: AnswerMode = uiState.value.answerMode,
        difficultyMode: DifficultyMode = uiState.value.difficultyMode,
        timeMode: TimeMode = uiState.value.timeMode,
        startTime: Int = uiState.value.timeTrialStartTime,
    ) {
        _guessedFlags.clear()
        _skippedGuessedFlags.clear()
        _skippedFlags.clear()
        _shownFlags.clear()
        _failedFlags.clear()
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
                currentFlag = newFlag,
                currentFlagStrings = newFlagStrings,
                isGuessWrong = false,
                nextFlagInSkipped = null,
                answerMode = answerMode,
                difficultyMode = difficultyMode,
                isGamePaused = false,
                answersRemaining = it.difficultyMode.guessLimit,
                isGame = if (it.currentFlags.isNotEmpty()) startGame else false,
                isGameOver = false,
                isGameOverDialog = false,
                isShowAnswer = false,
                isSaveScoreInit = false,
                scoreDetails = null,
            )
        }

        when (timeMode) {
            TimeMode.STANDARD -> setStandardTimer(isGame = uiState.value.isGame)
            TimeMode.TIME_TRIAL -> setTimeTrial(startTime = startTime, isGame = uiState.value.isGame)
        }
    }


    /* To enable buttons, show flag and start timer after resetGame() without startGame = true */
    fun startGame() {
        if (!uiState.value.isGame && uiState.value.currentFlags.isNotEmpty()) {
            val isTimeTrial = uiState.value.timeMode == TimeMode.TIME_TRIAL

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
            updateCurrentCategory(
                category = SovereignCountry,
                answerModeNew = answerMode,
                difficultyModeNew = difficultyMode,
            )
        } else if (difficultyMode != uiState.value.difficultyMode) {
            resetGame(difficultyMode = difficultyMode)
        }
    }


    fun updateCurrentCategory(
        category: FlagCategoryBase,
        answerModeNew: AnswerMode? = null,
        difficultyModeNew: DifficultyMode? = null,
        timeModeNew: TimeMode? = null,
        startGame: Boolean = true,
    ) {
        val allFlags = uiState.value.allFlags
        val answerMode = answerModeNew ?: uiState.value.answerMode
        val difficultyMode = difficultyModeNew ?: uiState.value.difficultyMode
        val timeMode = timeModeNew ?: uiState.value.timeMode
        val timeTrialStartTime = uiState.value.timeTrialStartTime
        val isDatesMode = answerMode == AnswerMode.DATES

        /* If the new category is the All super category set state with default values (which
         * includes allFlagsList), else dynamically generate flags list from category info */
        if (category == All || isDatesMode) {
            _uiState.value = GameUiState(
                currentFlags = if (isDatesMode) getDatesFlags(allFlags) else allFlags
            )

        } else {
            _uiState.value = GameUiState(
                currentFlags = getFlagsFromCategory(
                    category = category,
                    allFlags = allFlags,
                ),
                currentSuperCategories = buildList {
                    if (category is FlagSuperCategory) add(category)
                },
                currentSubCategories = buildList {
                    if (category is FlagCategoryWrapper) add(category.enum)
                },
            )
        }

        resetGame(startGame, answerMode, difficultyMode, timeMode, timeTrialStartTime)
    }


    fun updateCurrentCategories(category: FlagCategoryBase) {
        /* Controls whether flags are updated from current or all flags */
        var isDeselectSwitch = false to false

        val superCategories = uiState.value.currentSuperCategories.toMutableList()
        val subCategories = uiState.value.currentSubCategories.toMutableList()

        /* Exit function if new<*>Category is not selectable or mutually exclusive from current.
         * Else, add/remove category argument to/from categories lists */
        when (category) {
            is FlagSuperCategory -> {
                if (isSuperCategoryExit(category, superCategories, subCategories))
                    return

                isDeselectSwitch = updateCategoriesFromSuper(
                    superCategory = category,
                    superCategories = superCategories,
                    subCategories = subCategories,
                ) to false
            }
            is FlagCategoryWrapper -> {
                if (isSubCategoryExit(category.enum, subCategories, superCategories))
                    return

                isDeselectSwitch = updateCategoriesFromSub(
                    subCategory = category.enum,
                    subCategories = subCategories,
                )
            }
        }
        /* Return updateCurrentCategory() if deselection to only 1 super category */
        if (isDeselectSwitch.first) {
            when (superCategories.size to subCategories.size) {
                0 to 0 -> return updateCurrentCategory(category = All, startGame = false)
                1 to 0 -> return updateCurrentCategory(
                    category = superCategories.first(),
                    startGame = false,
                )
                1 to 1 -> {
                    val onlySuperCategory = superCategories.first()
                    val onlySubCategory = subCategories.first()

                    if (onlySuperCategory.subCategories.size == 1 &&
                        onlySuperCategory.firstCategoryEnumOrNull() == onlySubCategory) {
                        return updateCurrentCategory(category = onlySuperCategory, startGame = false)
                    }
                }
            }
        }

        /* Update state with new categories lists and currentFlags list */
        val allFlags = uiState.value.allFlags
        val currentFlags = uiState.value.currentFlags
        val answerMode = uiState.value.answerMode
        val difficultyMode = uiState.value.difficultyMode
        val timeMode = uiState.value.timeMode
        val timeTrialStartTime = uiState.value.timeTrialStartTime
        _uiState.value = GameUiState(
            /* Get new flags list from categories lists and either currentFlags or allFlags
             * (depending on select vs. deselect) */
            currentFlags = getFlagsFromCategories(
                allFlags = allFlags,
                currentFlags = currentFlags,
                isDeselectSwitch = isDeselectSwitch,
                superCategory = category as? FlagSuperCategory,
                superCategories = superCategories,
                subCategories = subCategories,
            ),
            currentSuperCategories = superCategories,
            currentSubCategories = subCategories,
            answerMode = answerMode,
            difficultyMode = difficultyMode,
        )

        resetGame(
            startGame = false,
            timeMode = timeMode,
            startTime = timeTrialStartTime,
        )
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
        _guessedFlags.isEmpty() && _skippedFlags.isEmpty() && _shownFlags.isEmpty()


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

        if (normalizedUserGuess.isEmpty()) {
            /* If empty answer */
            return

        } else if (normalizedUserGuess in normalizedAnswers) {
            /* If correct answer */
            userGuess = ""
            _uiState.update {
                it.copy(
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

    fun resetGuessVeracity() =
        _uiState.update { it.copy(isGuessCorrect = false, isGuessWrong = false) }


    fun skipFlag() {
        if (uiState.value.isShowAnswer) {
            /* Unpause game */
            _uiState.update {
                it.copy(isGamePaused = false, isShowAnswer = false)
            }

            when (uiState.value.timeMode) {
                TimeMode.STANDARD -> if (isJobInactive(timerStandardJob)) {
                    timerStandardJob = viewModelScope.launch { startTimerStandard() }
                }
                TimeMode.TIME_TRIAL -> if (isJobInactive(timerTimeTrialJob)) {
                    timerTimeTrialJob = viewModelScope.launch { startTimerTimeTrial() }
                }
            }

            updateCurrentFlag(isShowAnswer = true)

        } else {
            /* Regular skip */
            cancelConfirmShowAnswer()
            updateCurrentFlag(isSkip = true)
        }
    }


    fun showAnswer() {
        /* Cancel any/all timers */
        cancelTimers()
        cancelConfirmShowAnswer()

        _shownFlags.add(uiState.value.currentFlag)
        _uiState.update {
            it.copy(
                isShowAnswer = true,
                isGamePaused = true,
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
    fun onTimeTrialDialog(on: Boolean) = _uiState.update { it.copy(isTimeTrialDialog = on) }

    fun onGameModesDialog(on: Boolean) = _uiState.update { it.copy(isGameModesDialog = on) }

    fun onGameOverDialog(on: Boolean) = _uiState.update { it.copy(isGameOverDialog = on) }

    fun onScoreDetails(on: Boolean) = _uiState.update { it.copy(isScoreDetails = on) }

    fun onConfirmExitDialog(on: Boolean) = _uiState.update { it.copy(isConfirmExitDialog = on) }

    fun onConfirmResetDialog(on: Boolean) = _uiState.update { it.copy(isConfirmResetDialog = on) }


    /* For LaunchedEffect() when language configuration changes */
    fun setFlagStrings() {
        val currentFlag = uiState.value.currentFlag
        val currentFlagStrings = getStringsList(flag = currentFlag)
        _uiState.update { it.copy(currentFlagStrings = currentFlagStrings) }
    }


    private fun setStandardTimer(isGame: Boolean) {
        _uiState.update {
            it.copy(
                timerStandard = 0,
                timeMode = TimeMode.STANDARD,
                timerTimeTrial = 0,
                timeTrialStartTime = 0
            )
        }
        if (isGame) {
            timerStandardJob = viewModelScope.launch { startTimerStandard() }
        }
    }

    private fun setTimeTrial(
        startTime: Int,
        isGame: Boolean,
    ) {
        _uiState.update {
            it.copy(
                timerStandard = 0,
                timeMode = TimeMode.TIME_TRIAL,
                timerTimeTrial = startTime,
                timeTrialStartTime = startTime,
            )
        }
        if (isGame) {
            timerTimeTrialJob = viewModelScope.launch { startTimerTimeTrial() }
        }
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
            if (!isSkipMax()) _skippedFlags.add(currentFlag)

        } else if (isFail) {
            _failedFlags.add(currentFlag)

        } else { /* If user guess, add flag to guessed set */
            if (!isShowAnswer) _guessedFlags.add(currentFlag)

            /* If flag in skipped list, move it into skippedGuessed list */
            if (currentFlag in _skippedFlags) {
                _skippedFlags.remove(currentFlag)
                if (!isShowAnswer) _skippedGuessedFlags.add(currentFlag)
            }
        }

        /* If all flags guessed, end the game */
        if (_guessedFlags.size + _shownFlags.size + _failedFlags.size >=
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
        return _shownFlags.size + _skippedFlags.size + _guessedFlags.size ==
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
                    listOf(_guessedFlags, _skippedFlags, _shownFlags, _failedFlags).flatten()
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
            val nextFlagIndex = _skippedFlags.indexOf(nextFlagInSkipped)
            val isListEnd = nextFlagIndex == _skippedFlags.size - 1

            if (isListEnd) {
                _uiState.update { it.copy(nextFlagInSkipped = _skippedFlags[0]) }
            } else {
                _uiState.update { it.copy(nextFlagInSkipped = _skippedFlags[nextFlagIndex + 1]) }
            }

            nextFlagInSkipped

        } else { // On first time call to getSkippedFlag()
            val nextIndex = if (_skippedFlags.size == 1) 0 else 1
            _uiState.update { it.copy(nextFlagInSkipped = _skippedFlags[nextIndex]) }

            _skippedFlags[0]
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
            timeMode = uiState.value.timeMode,
            timerStart = when (uiState.value.timeMode) {
                TimeMode.STANDARD -> null
                TimeMode.TIME_TRIAL -> uiState.value.timeTrialStartTime
            },
            timerEnd = when (uiState.value.timeMode) {
                TimeMode.STANDARD -> uiState.value.timerStandard
                TimeMode.TIME_TRIAL -> uiState.value.timerTimeTrial
            },
            gameSuperCategories = getScoreDataSupers(),
            gameSubCategories = getScoreDataSubs(),
            flagsAll = uiState.value.currentFlags,
            flagsGuessed = _guessedFlags.toList(),
            flagsGuessedSorted = sortFlags(_guessedFlags),
            flagsSkippedGuessed = _skippedGuessedFlags.toList(),
            flagsSkippedGuessedSorted = sortFlags(_skippedGuessedFlags),
            flagsSkipped = _skippedFlags.toList(),
            flagsSkippedSorted = sortFlags(_skippedFlags),
            flagsShown = _shownFlags.toList(),
            flagsShownSorted = sortFlags(_shownFlags),
            flagsFailed = _failedFlags.toList(),
            flagsFailedSorted = sortFlags(_failedFlags),
        )
    }


    /* Remove redundant super categories for ScoreData */
    private fun getScoreDataSupers(): List<FlagSuperCategory> = filterRedundantSuperCategories(
        superCategories = uiState.value.currentSuperCategories,
        subCategories = uiState.value.currentSubCategories
    )

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