package dev.aftly.flags.ui.screen.game

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode
import dev.aftly.flags.model.game.GameConfirmDialog
import dev.aftly.flags.model.game.TimeMode
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.DialogActionButton
import dev.aftly.flags.ui.component.FilterButtonMenu
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.ResultsType
import dev.aftly.flags.ui.component.ScoreDetails
import dev.aftly.flags.ui.component.shareText
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.successDark
import dev.aftly.flags.ui.theme.successLight
import dev.aftly.flags.ui.theme.surfaceLight
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.SystemUiController
import dev.aftly.flags.ui.util.flagDatesString
import dev.aftly.flags.ui.util.getCategoriesTitleIds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    currentBackStackEntry: NavBackStackEntry?,
    screen: Screen,
    isNavigationDrawerOpen: Boolean,
    onDrawerNavigateToList: Boolean,
    onResetDrawerNavigateToList: () -> Unit,
    onDrawerNavigateToGame: Boolean,
    onResetDrawerNavigateToGame: () -> Unit,
    onNavigationDrawer: () -> Unit,
    onNavigateUp: () -> Unit,
    onScoreHistory: (Boolean) -> Unit, // isGameOver Boolean
    onFullscreen: (FlagView, Boolean, Boolean) -> Unit,
) {
    /* Expose screen and backStack state */
    val uiState by viewModel.uiState.collectAsState()
    val savedFlags by viewModel.savedFlagsState.collectAsState()

    /* Manage system bars and flag state after returning from FullScreen */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(view, window) }
    val isDarkTheme = LocalDarkTheme.current

    /* When language configuration changes, update strings in uiState */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = currentBackStackEntry) {
        systemUiController.setLightStatusBar(light = !isDarkTheme)
        systemUiController.setSystemBars(visible = true)

        currentBackStackEntry?.savedStateHandle?.get<Boolean>("isGameOver")?.let { isGameOver ->
            if (isGameOver) viewModel.onGameOverDialog(on = true)
        }
    }

    /* Handle if confirm exit from game progress when navigation back from NavigationDrawer or
     * system back action ------------------------------- */
    LaunchedEffect(key1 = onDrawerNavigateToList) {
        if (onDrawerNavigateToList) {
            if (viewModel.isScoresEmpty()) onNavigateUp()
            else viewModel.onConfirmExitDialog(on = true)
        }
    }

    /* Reset game when click from nav drawer */
    LaunchedEffect(key1 = onDrawerNavigateToGame) {
        if (onDrawerNavigateToGame) {
            if (viewModel.isScoresEmpty()) viewModel.resetScreen()
            else viewModel.onConfirmResetDialog(on = true)

            onResetDrawerNavigateToGame()
        }
    }

    BackHandler {
        if (uiState.isGameOver) {
            viewModel.onScoreDetails(on = false)
            viewModel.endGame()
        }
        else if (isNavigationDrawerOpen) onNavigationDrawer()
        else if (viewModel.isScoresEmpty()) onNavigateUp()
        else viewModel.onConfirmExitDialog(on = true)
    }


    if (uiState.isConfirmExitDialog) {
        ConfirmDialog(
            type = GameConfirmDialog.EXIT,
            onConfirm = {
                focusManager.clearFocus()
                viewModel.onConfirmExitDialog(on = false)
                onNavigateUp()
            },
            onDismiss = {
                viewModel.onConfirmExitDialog(on = false)
                if (onDrawerNavigateToList) onResetDrawerNavigateToList()
            },
        )
    }
    if (uiState.isConfirmResetDialog) {
        ConfirmDialog(
            type = GameConfirmDialog.RESET,
            onConfirm = {
                viewModel.resetScreen()
                viewModel.onConfirmResetDialog(on = false)
            },
            onDismiss = {
                viewModel.onConfirmResetDialog(on = false)
            },
        )
    }

    /* -------------------------------------------------- */

    if (uiState.isTimeTrialDialog) {
        TimeTrialDialog(
            userMinutesInput = viewModel.userTimerInputMinutes,
            userSecondsInput = viewModel.userTimerInputSeconds,
            onUserMinutesInputChange = { viewModel.updateUserMinutesInput(it) },
            onUserSecondsInputChange = { viewModel.updateUserSecondsInput(it) },
            onDismiss = { viewModel.onTimeTrialDialog(on = false) },
            onTimeTrial = { viewModel.resetGame(timeMode = TimeMode.TIME_TRIAL, startTime = it) },
        )
    }

    if (uiState.isInfoDialog) {
        InfoDialog(
            onDismiss = { viewModel.onInfoDialog(on = false) }
        )
    }

    if (uiState.isGameModesDialog) {
        GameModesDialog(
            answerMode = uiState.answerMode,
            difficultyMode = uiState.difficultyMode,
            onDismiss = { viewModel.onGameModesDialog(on = false) },
            onGameModes = { answerMode, difficultyMode ->
                viewModel.updateGameModes(answerMode, difficultyMode)
            }
        )
    }

    /* Show pop-up when game over */
    if (uiState.isGameOverDialog) {
        val context = LocalContext.current
        GameOverDialog(
            finalScore = viewModel.guessedFlags.size,
            maxScore = uiState.currentFlags.size,
            superCategories = uiState.currentSuperCategories,
            subCategories = uiState.currentSubCategories,
            answerMode = uiState.answerMode,
            difficultyMode = uiState.difficultyMode,
            onDetails = {
                viewModel.onGameOverDialog(on = false)
                viewModel.onScoreDetails(on = true)
            },
            onScoreHistory = {
                viewModel.onGameOverDialog(on = false)
                onScoreHistory(uiState.isGameOver)
            },
            onShare = { text ->
                shareText(
                    context = context,
                    subject = R.string.game_over_share_subject,
                    textToShare = text,
                )
            },
            onExit = {
                viewModel.onGameOverDialog(on = false)
                onNavigateUp()
            },
            onReplay = { viewModel.resetGame() },
        )
    }


    GameScreen(
        uiState = uiState,
        screen = screen,
        isNavigationDrawerOpen = isNavigationDrawerOpen,
        isSavedFlagsNotEmpty = savedFlags.isNotEmpty(),
        correctGuessCount = viewModel.guessedFlags.size,
        shownFailedAnswerCount = viewModel.shownFlags.size + viewModel.failedFlags.size,
        userGuess = viewModel.userGuess,
        onUserGuessChange = { viewModel.updateUserGuess(it) },
        onResetGuessVeracityStates = { viewModel.resetGuessVeracity() },
        onSubmit = { viewModel.checkUserGuess() },
        onSkip = { viewModel.skipFlag() },
        onConfirmShowAnswer = { viewModel.confirmShowAnswer() },
        onShowAnswer = { viewModel.showAnswer() },
        onStartGame = { viewModel.startGame() },
        onEndGame = { viewModel.endGame() },
        onResetGameAndTimeMode = { viewModel.resetGame(timeMode = TimeMode.STANDARD) },
        onResetGame = { viewModel.resetGame() },
        onCloseScoreDetails = {
            viewModel.onScoreDetails(on = false)
            viewModel.endGame()
        },
        onNavigationDrawer = onNavigationDrawer,
        onTimeTrialDialog = { viewModel.onTimeTrialDialog(on = true) },
        onInfoDialog = { viewModel.onInfoDialog(on = true) },
        onGameModesDialog = { viewModel.onGameModesDialog(on = true) },
        onScoreHistory = { onScoreHistory(uiState.isGameOver) },
        onFullscreen = { isFlagWide ->
            onFullscreen(uiState.currentFlag, isFlagWide, !uiState.isShowAnswer)
        },
        onCategorySelectSingle = {
            viewModel.updateCurrentCategory(category = it)
        },
        onCategorySelectMultiple = {
            viewModel.updateCurrentCategories(category = it)
        },
        onSavedFlagsSelect = { viewModel.selectSavedFlags() },
        onFilterByCountry = { viewModel.updateFilterByCountry(country = it) }
    )
}


@Composable
private fun GameScreen(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    screen: Screen,
    isNavigationDrawerOpen: Boolean,
    isSavedFlagsNotEmpty: Boolean,
    correctGuessCount: Int,
    shownFailedAnswerCount: Int,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onResetGuessVeracityStates: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onConfirmShowAnswer: () -> Unit,
    onShowAnswer: () -> Unit,
    onStartGame: () -> Unit,
    onEndGame: () -> Unit,
    onResetGameAndTimeMode: () -> Unit,
    onResetGame: () -> Unit,
    onCloseScoreDetails: () -> Unit,
    onNavigationDrawer: () -> Unit,
    onTimeTrialDialog: () -> Unit,
    onInfoDialog: () -> Unit,
    onGameModesDialog: () -> Unit,
    onScoreHistory: () -> Unit,
    onFullscreen: (Boolean) -> Unit,
    onCategorySelectSingle: (FlagCategoryBase) -> Unit,
    onCategorySelectMultiple: (FlagCategoryBase) -> Unit,
    onSavedFlagsSelect: () -> Unit,
    onFilterByCountry: (FlagView) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion and tracks button height */
    var isMenuExpanded by rememberSaveable { mutableStateOf(value = false) }
    var buttonHeight by remember { mutableStateOf(value = 0.dp) }

    /* So that FilterFlagsButton can access Scaffold() padding */
    var scaffoldPaddingValues by remember { mutableStateOf(value = PaddingValues()) }

    val resources = LocalResources.current
    val isWideScreen = remember {
        resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels
    }
    var isFlagWide by rememberSaveable { mutableStateOf(value = true) }

    /* Manage timer string */
    val timer = when (uiState.timeMode) {
        TimeMode.STANDARD -> uiState.timerStandard
        TimeMode.TIME_TRIAL -> uiState.timerTimeTrial
    }
    val timerString = String.format(
        locale = Locale.US,
        format = "%d:%02d",
        timer / 60, timer % 60
    )

    /* Collapse menu when no flags */
    LaunchedEffect(key1 = uiState.currentFlags) {
        if (uiState.currentFlags.isEmpty()) isMenuExpanded = false
    }

    /* Manage guess field focus and unpause game */
    var isGuessFieldFocused by rememberSaveable { mutableStateOf(value = false) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = isMenuExpanded) {
        if (isMenuExpanded) focusManager.clearFocus() else onStartGame()
    }
    val coroutineScope = rememberCoroutineScope()


    /* Scaffold within box so that FilterFlagsButton & it's associated surface can overlay it */
    Box(modifier = modifier.fillMaxSize()) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                        isGuessFieldFocused = false
                    }
                },
            topBar = {
                GameTopBar(
                    screen = screen,
                    isFlags = uiState.currentFlags.isNotEmpty(),
                    isTimeTrial = uiState.timeMode == TimeMode.TIME_TRIAL,
                    isGame = uiState.isGame,
                    isGamePaused = uiState.isGamePaused,
                    isGameOver = uiState.isGameOver,
                    timer = timerString,
                    onNavigationDrawer = {
                        onNavigationDrawer()
                        focusManager.clearFocus()
                    },
                    onResetAction = onResetGame,
                    onTimeAction = {
                        when (uiState.timeMode) {
                            TimeMode.TIME_TRIAL -> {
                                focusManager.clearFocus()
                                onResetGameAndTimeMode()
                            }
                            TimeMode.STANDARD -> onTimeTrialDialog()
                        }
                    },
                    onInfoAction = onInfoDialog,
                    onSettingsAction = onGameModesDialog,
                    onHistoryAction = {
                        focusManager.clearFocus()
                        onScoreHistory()
                    },
                )
            }
        ) { scaffoldPadding ->
            scaffoldPaddingValues = scaffoldPadding

            GameContent(
                modifier = Modifier.padding(scaffoldPadding),
                isNavigationDrawerOpen = isNavigationDrawerOpen,
                answerMode = uiState.answerMode,
                difficultyMode = uiState.difficultyMode,
                isGame = uiState.isGame,
                isGameOver = uiState.isGameOver,
                isWideScreen = isWideScreen,
                filterButtonHeight = buttonHeight,
                totalFlagCount = uiState.currentFlags.size,
                correctGuessCount = correctGuessCount,
                shownFailedAnswerCount = shownFailedAnswerCount,
                answersRemainingCount = uiState.answersRemaining,
                currentFlag = uiState.currentFlag,
                userGuess = userGuess,
                onUserGuessChange = onUserGuessChange,
                onFocusChanged = { isGuessFieldFocused = it },
                isGuessFieldFocused = isGuessFieldFocused,
                isTimeTrialDialog = uiState.isTimeTrialDialog,
                isGameModesDialog = uiState.isGameModesDialog,
                isMenuExpanded = isMenuExpanded,
                isGuessCorrect = uiState.isGuessCorrect,
                isGuessCorrectEvent = uiState.isGuessCorrectEvent,
                isGuessWrong = uiState.isGuessWrong,
                isGuessWrongEvent = uiState.isGuessWrongEvent,
                onResetGuessVeracityStates = onResetGuessVeracityStates,
                showAnswerResetTimer = uiState.timerShowAnswerReset,
                isConfirmShowAnswer = uiState.isConfirmShowAnswer,
                isShowAnswer = uiState.isShowAnswer,
                onSubmit = onSubmit,
                onSkip = onSkip,
                onConfirmShowAnswer = onConfirmShowAnswer,
                onShowAnswer = onShowAnswer,
                onEndGame = onEndGame,
                onImageWide = { isFlagWide = it },
                onFullscreen = { onFullscreen(isFlagWide) },
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        FilterButtonMenu(
            modifier = Modifier.fillMaxSize(),
            scaffoldPadding = scaffoldPaddingValues,
            buttonHorizontalPadding = Dimens.marginHorizontal16,
            flagCount = null,
            onButtonHeightChange = { buttonHeight = it },
            isMenuEnabled = uiState.answerMode != AnswerMode.DATES,
            isMenuExpanded = isMenuExpanded,
            onMenuButtonClick = { isMenuExpanded = !isMenuExpanded },
            isSavedFlagsNotEmpty = isSavedFlagsNotEmpty,
            filterByCountry = uiState.filterByCountry,
            superCategories = uiState.currentSuperCategories,
            subCategories = uiState.currentSubCategories,
            onCategorySelectSingle = onCategorySelectSingle,
            onCategorySelectMultiple = onCategorySelectMultiple,
            onSavedFlagsSelect = onSavedFlagsSelect,
            onFilterByCountry = { onFilterByCountry(it) },
        )


        /* For displaying verbose score details */
        if (uiState.scoreDetails != null) {
            ScoreDetails(
                visible = uiState.isScoreDetails,
                scoreData = uiState.scoreDetails,
                onClose = onCloseScoreDetails,
            )
        }
    }
}


@Composable
private fun GameContent(
    modifier: Modifier = Modifier,
    isNavigationDrawerOpen: Boolean,
    answerMode: AnswerMode,
    difficultyMode: DifficultyMode,
    isGame: Boolean,
    isGameOver: Boolean,
    isWideScreen: Boolean,
    filterButtonHeight: Dp,
    totalFlagCount: Int,
    correctGuessCount: Int,
    shownFailedAnswerCount: Int,
    answersRemainingCount: Int?,
    currentFlag: FlagView,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isGuessFieldFocused: Boolean,
    isTimeTrialDialog: Boolean,
    isGameModesDialog: Boolean,
    isMenuExpanded: Boolean,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    onResetGuessVeracityStates: () -> Unit,
    showAnswerResetTimer: Int,
    isConfirmShowAnswer: Boolean,
    isShowAnswer: Boolean,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onConfirmShowAnswer: () -> Unit,
    onShowAnswer: () -> Unit,
    onEndGame: () -> Unit,
    onImageWide: (Boolean) -> Unit,
    onFullscreen: () -> Unit,
) {
    /* Properties for if card image height greater than content column height, eg. in landscape
     * orientation, make image height modifier value of column height, else value of image height
     * Also limits game card width in landscape orientation */
    var columnHeight by remember { mutableStateOf(value = 0.dp) }
    var imageHeight by remember { mutableStateOf(value = 0.dp) }
    var imageWidth by remember { mutableStateOf(value = 0.dp) }
    var imageHeightModifier by remember { mutableStateOf<Modifier>(value = Modifier) }
    var cardWidthModifier by remember { mutableStateOf<Modifier>(value = Modifier) }
    val density = LocalDensity.current
    val contentTopPadding = Dimens.defaultFilterButtonHeight30 / 2

    val aspectRatioTopPadding = when (isWideScreen) {
        false -> Dimens.large24
        true -> Dimens.large24 / 2
    }

    /* Show answer button properties */
    val isDarkTheme = LocalDarkTheme.current
    val showAnswerColor = when (isConfirmShowAnswer) {
        false -> if (isDarkTheme) successDark else successLight
        true -> MaterialTheme.colorScheme.error
    }
    val showAnswerText = when (isConfirmShowAnswer to isShowAnswer) {
        false to false -> stringResource(R.string.game_button_show)
        true to false -> stringResource(R.string.game_button_show_confirm, showAnswerResetTimer)
        else -> stringResource(R.string.game_button_shown)
    }


    /* Center arranged column with Game content */
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                /* Top padding so that content scroll disappears into FilterFlagsButton */
                top = filterButtonHeight / 2,
                start = Dimens.marginHorizontal16,
                end = Dimens.marginHorizontal16,
            )
            .onSizeChanged { size ->
                columnHeight = with(density) { size.height.toDp() } - contentTopPadding

                /* Set image height modifier */
                if (columnHeight - contentTopPadding < imageHeight) {
                    imageHeightModifier = Modifier.height(height = columnHeight)
                    cardWidthModifier = Modifier.width(width = (imageWidth.value * 1.25).dp)
                } else {
                    imageHeightModifier = Modifier.height(height = imageHeight)
                    cardWidthModifier = Modifier.fillMaxWidth()
                }
            }
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GameCard(
            modifier = Modifier.padding(top = filterButtonHeight / 2 + aspectRatioTopPadding),
            isNavigationDrawerOpen = isNavigationDrawerOpen,
            answerMode = answerMode,
            difficultyMode = difficultyMode,
            isGame = isGame,
            isGameOver = isGameOver,
            contentColumnHeight = columnHeight,
            cardImageWidth = imageWidth,
            onCardImageWidthChange = { imageWidth = it },
            cardImageHeight = imageHeight,
            onCardImageHeightChange = { imageHeight = it },
            cardImageHeightModifier = imageHeightModifier,
            onCardImageHeightModifierChange = { imageHeightModifier = it },
            cardWidthModifier = cardWidthModifier,
            onCardWidthModifierChange = { cardWidthModifier = it },
            totalFlagCount = totalFlagCount,
            correctGuessCount = correctGuessCount,
            shownFailedAnswerCount = shownFailedAnswerCount,
            answersRemainingCount = answersRemainingCount,
            currentFlag = currentFlag,
            userGuess = userGuess,
            onUserGuessChange = onUserGuessChange,
            onFocusChanged = onFocusChanged,
            isGuessFieldFocused = isGuessFieldFocused,
            isTimeTrialDialog = isTimeTrialDialog,
            isGameModesDialog = isGameModesDialog,
            isMenuExpanded = isMenuExpanded,
            isGuessCorrect = isGuessCorrect,
            isGuessCorrectEvent = isGuessCorrectEvent,
            isGuessWrong = isGuessWrong,
            isGuessWrongEvent = isGuessWrongEvent,
            onResetGuessVeracityStates = onResetGuessVeracityStates,
            isShowAnswer = isShowAnswer,
            onSubmit = onSubmit,
            onEndGame = onEndGame,
            onImageWide = onImageWide,
            onFullscreen = onFullscreen,
        )

        /* Submit button */
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .padding(top = 2.dp)
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
            enabled = isGame && !isGameOver && !isShowAnswer,
        ) {
            Text(text = stringResource(R.string.game_button_submit))
        }

        /* Skip button */
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
            enabled = isGame && !isGameOver,
        ) {
            Text(text = stringResource(R.string.game_button_skip))
        }

        /* Show answer button */
        OutlinedButton(
            onClick = {
                when (isConfirmShowAnswer) {
                    false -> onConfirmShowAnswer()
                    true -> onShowAnswer()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
            enabled = isGame && !isGameOver && !isShowAnswer &&
                    difficultyMode != DifficultyMode.SUDDEN_DEATH,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = showAnswerColor),
        ) {
            Text(text = showAnswerText)
        }
    }
}


@Composable
private fun GameCard(
    modifier: Modifier = Modifier,
    isNavigationDrawerOpen: Boolean,
    answerMode: AnswerMode,
    difficultyMode: DifficultyMode,
    isGame: Boolean,
    isGameOver: Boolean,
    contentColumnHeight: Dp,
    cardImageWidth: Dp,
    onCardImageWidthChange: (Dp) -> Unit,
    cardImageHeight: Dp,
    onCardImageHeightChange: (Dp) -> Unit,
    cardImageHeightModifier: Modifier,
    onCardImageHeightModifierChange: (Modifier) -> Unit,
    cardWidthModifier: Modifier,
    onCardWidthModifierChange: (Modifier) -> Unit,
    totalFlagCount: Int,
    correctGuessCount: Int,
    shownFailedAnswerCount: Int,
    answersRemainingCount: Int?,
    currentFlag: FlagView,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isGuessFieldFocused: Boolean,
    isTimeTrialDialog: Boolean,
    isGameModesDialog: Boolean,
    isMenuExpanded: Boolean,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    onResetGuessVeracityStates: () -> Unit,
    isShowAnswer: Boolean,
    onSubmit: () -> Unit,
    onEndGame: () -> Unit,
    onImageWide: (Boolean) -> Unit,
    onFullscreen: () -> Unit,
) {
    /* State variables for animated colors */
    val focusedColorDefault = OutlinedTextFieldDefaults.colors().focusedIndicatorColor
    val focusedContainerColor: Color = MaterialTheme.colorScheme.surface

    val animationSpecEnter = tween<Color>(durationMillis = 0)
    val animationSpecExit = tween<Color>(durationMillis = Timing.GUESS_STATE_EXIT)
    var animationSpecSuccess by remember { mutableStateOf(value = animationSpecEnter) }
    var animationSpecError by remember { mutableStateOf(value = animationSpecEnter) }

    var isSuccessColor by remember { mutableStateOf(value = false) }
    var isErrorColor by remember { mutableStateOf(value = false) }

    val isDarkTheme = LocalDarkTheme.current
    val animatedSuccessColor by animateColorAsState(
        targetValue = when (isSuccessColor) {
            true -> if (isDarkTheme) successDark else successLight
            else -> focusedColorDefault
        },
        animationSpec = animationSpecSuccess
    )
    val animatedErrorColor by animateColorAsState(
        targetValue = if (isErrorColor) MaterialTheme.colorScheme.error
        else focusedColorDefault,
        animationSpec = animationSpecError,
    )

    var isFullScreenButton by rememberSaveable { mutableStateOf(value = false) }

    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current

    /* Referencing currentFlag.flagOf directly in Text() shows next flagOf for 1 frame after skip */
    val correctAnswer = when (isShowAnswer to answerMode) {
        true to AnswerMode.NAMES -> AnnotatedString(text = stringResource(currentFlag.flagOf))
        true to AnswerMode.DATES -> buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                append(stringResource(currentFlag.flagOf))
            }
            append(stringResource(R.string.string_new_line))
            append(flagDatesString(flag = currentFlag, isGameDatesMode = true))
        }
        else -> null
    }

    /* Guess field label & effects */
    val gameModeLabelResId = when (answerMode) {
        AnswerMode.NAMES -> R.string.game_guess_field_names_label
        AnswerMode.DATES -> R.string.game_guess_field_dates_label
    }
    val labelDefault = stringResource(gameModeLabelResId)
    val labelSuccess = stringResource(R.string.game_guess_field_label_success)
    val labelError = stringResource(R.string.game_guess_field_label_error)
    var labelState by remember { mutableStateOf(value = labelDefault) }

    /* LaunchedEffects for triggering animations of above variables */
    LaunchedEffect(key1 = answerMode) {
        if (labelState !in listOf(labelSuccess, labelError)) {
            labelState = labelDefault
        }
    }
    LaunchedEffect(key1 = isGuessCorrectEvent) {
        if (isGuessCorrect) {
            animationSpecSuccess = animationSpecEnter
            isSuccessColor = true
            labelState = labelSuccess

            delay(timeMillis = Timing.GUESS_STATE_DURATION.toLong())
            animationSpecSuccess = animationSpecExit
            isSuccessColor = false

            delay(timeMillis = Timing.GUESS_STATE_EXIT.toLong())
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelError) labelError else labelDefault
            onResetGuessVeracityStates()
        }
    }
    LaunchedEffect(key1 = isGuessWrongEvent) {
        if (isGuessWrong) {
            animationSpecError = animationSpecEnter
            isErrorColor = true
            labelState = labelError

            delay(timeMillis = Timing.GUESS_STATE_DURATION.toLong())
            animationSpecError = animationSpecExit
            isErrorColor = false

            delay(timeMillis = Timing.GUESS_STATE_EXIT.toLong())
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelSuccess) labelSuccess else labelDefault
            onResetGuessVeracityStates()
        }
    }

    /* Manage text field focus */
    val focusRequesterGuessField = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    LaunchedEffect(key1 = isTimeTrialDialog) {
        if (isTimeTrialDialog) coroutineScope.launch {
            delay(timeMillis = Timing.DIALOG_DEFAULT.toLong())
            focusManager.clearFocus()
        }
        else if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    LaunchedEffect(key1 = isGameModesDialog) {
        if (isGameModesDialog) coroutineScope.launch {
            delay(timeMillis = Timing.DIALOG_DEFAULT.toLong())
            focusManager.clearFocus()
        }
        else if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    LaunchedEffect(key1 = isMenuExpanded) {
        if (isMenuExpanded) focusManager.clearFocus()
        else if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    LaunchedEffect(key1 = isNavigationDrawerOpen) {
        if (isNavigationDrawerOpen) focusManager.clearFocus()
        else if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }


    /* Game Card content */
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = cardWidthModifier.padding(all = Dimens.large24),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /* Top row in card */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.medium16),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                /* Flag counters and game mode */
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buildString {
                            val progress = correctGuessCount + shownFailedAnswerCount
                            append(progress.toString())
                            append(stringResource(R.string.string_forward_slash))
                            append(totalFlagCount.toString())
                        },
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.medium)
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    if (shownFailedAnswerCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = shownFailedAnswerCount.toString(),
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.medium)
                                .background(color = MaterialTheme.colorScheme.error)
                                .padding(
                                    vertical = Dimens.extraSmall4,
                                    horizontal = Dimens.small10,
                                ),
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = stringResource(answerMode.title),
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.medium)
                            .background(color = MaterialTheme.colorScheme.tertiary)
                            .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                        color = MaterialTheme.colorScheme.onTertiary,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    /* Difficulty mode icon/text */
                    if (difficultyMode.icon != null) {
                        Spacer(modifier = Modifier.width(2.dp))

                        Icon(
                            imageVector = difficultyMode.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.medium)
                                .background(color = MaterialTheme.colorScheme.tertiary)
                                .padding(vertical = 23.dp / 10, horizontal = Dimens.small8),
                            tint = MaterialTheme.colorScheme.onTertiary,
                        )
                    } else if (difficultyMode.guessLimit != null) {
                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = difficultyMode.guessLimit.toString(),
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.medium)
                                .background(color = MaterialTheme.colorScheme.tertiary)
                                .padding(
                                    vertical = Dimens.extraSmall4,
                                    horizontal = Dimens.small10
                                ),
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }

                    /* If relevant, show guesses remaining */
                    difficultyMode.guessLimit?.let { guessLimit ->
                        if (guessLimit > 1) {
                            answersRemainingCount?.let { answersRemaining ->
                                Spacer(modifier = Modifier.width(2.dp))

                                Text(
                                    text = answersRemaining.toString(),
                                    modifier = Modifier
                                        .clip(shape = MaterialTheme.shapes.medium)
                                        .background(
                                            color = if (isDarkTheme) successDark else successLight
                                        )
                                        .padding(
                                            vertical = Dimens.extraSmall4,
                                            horizontal = Dimens.small10
                                        ),
                                    color = MaterialTheme.colorScheme.surface,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                    }
                }

                Row {
                    /* Finish game button */
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            onEndGame()
                        },
                        modifier = Modifier.height(height = Dimens.extraLarge32),
                        enabled = isGame && !isGameOver,
                        shape = RoundedCornerShape(size = Dimens.small8),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(
                            start = Dimens.small10,
                            bottom = Dimens.extraSmall6,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.game_end),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            /* Flag & show answer content */
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            focusManager.clearFocus()
                            onFocusChanged(false)
                            isFullScreenButton = !isFullScreenButton
                        },
                        onDoubleClick = {
                            focusManager.clearFocus()
                            onFocusChanged(false)
                            onFullscreen()
                        },
                    ),
                contentAlignment = Alignment.BottomEnd,
            ) {
                if (totalFlagCount > 0) {
                    /* Invisible image for determining fullscreen orientation */
                    Image(
                        modifier = Modifier
                            .fillMaxSize(fraction = 0.15f)
                            .onSizeChanged { onImageWide(it.width > it.height) },
                        painter = painterResource(id = currentFlag.imagePreview),
                        contentDescription = null,
                        alpha = 0f,
                    )

                    Image(
                        modifier = Modifier
                            .onSizeChanged { size ->
                                onCardImageHeightChange(
                                    with(receiver = density) { size.height.toDp() }
                                )
                                onCardImageWidthChange(
                                    with(receiver = density) { size.width.toDp() }
                                )

                                /* Set image height modifier */
                                if (contentColumnHeight < cardImageHeight) {
                                    onCardImageHeightModifierChange(
                                        Modifier.height(contentColumnHeight)
                                    )
                                    onCardWidthModifierChange(
                                        Modifier.width((cardImageWidth.value * 1.25).dp)
                                    )
                                } else {
                                    onCardImageHeightModifierChange(
                                        Modifier.height(cardImageHeight)
                                    )
                                    onCardWidthModifierChange(
                                        Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            /* Concatenate height after onSizeChanged */
                            .then(other = cardImageHeightModifier)
                            /* Force aspect ratio to keep game card shape consistent */
                            .aspectRatio(ratio = 3f / 2f),
                        painter = painterResource(id = currentFlag.image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                    )

                    /* Conceal flag (for when game paused) */
                    if (!isGame) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(color = MaterialTheme.colorScheme.onSurface),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.game_paused),
                                color = MaterialTheme.colorScheme.surface,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }

                    /* Show answer content */
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isShowAnswer,
                        modifier = Modifier.matchParentSize(),
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = Timing.SHOW_ANSWER)
                        ),
                        exit = ExitTransition.None,
                    ) {
                        Box(
                            modifier = Modifier.padding(all = Dimens.medium16),
                            contentAlignment = Alignment.Center,
                        ) {
                            correctAnswer?.let { answer ->
                                Text(
                                    text = answer,
                                    modifier = Modifier
                                        .clip(shape = MaterialTheme.shapes.large)
                                        .background(color = Color.Black.copy(alpha = 0.75f))
                                        .padding(
                                            vertical = Dimens.small8,
                                            horizontal = Dimens.medium16,
                                        ),
                                    color = surfaceLight,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineLarge,
                                )
                            }
                        }
                    }

                    FullscreenButton(
                        visible = isFullScreenButton,
                        onInvisible = { isFullScreenButton = false },
                        onFullScreen = onFullscreen,
                    )
                } else {
                    NoResultsFound(
                        modifier = Modifier
                            .aspectRatio(ratio = 2f / 1f)
                            .fillMaxSize(),
                        resultsType = ResultsType.CATEGORIES,
                    )
                }
            }

            /* User guess field */
            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.small10)
                    .onFocusChanged {
                        if (it.isFocused) onFocusChanged(true)
                    }
                    .focusRequester(focusRequesterGuessField),
                enabled = isGame && !isGameOver && !isShowAnswer,
                label = {
                    Text(text = labelState)
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = when (answerMode) {
                        AnswerMode.NAMES -> KeyboardType.Text
                        AnswerMode.DATES -> KeyboardType.Number
                    },
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit() },
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = focusedContainerColor,
                    focusedContainerColor = focusedContainerColor,
                    errorContainerColor = focusedContainerColor,
                    focusedBorderColor = animatedSuccessColor,
                    focusedLabelColor = animatedSuccessColor,
                    cursorColor = animatedSuccessColor,
                    errorBorderColor = animatedErrorColor,
                    errorCursorColor = animatedErrorColor,
                    errorLabelColor = animatedErrorColor,
                )
            )
        }
    }
}


/* Template component for other simple game screen submission dialogs */
@Composable
private fun GameDialog(
    @StringRes title: Int,
    isSubmittable: Boolean,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(top = Dimens.large24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                /* Title */
                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimens.large24)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = stringResource(id = title),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                /* CONTENT */
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.large24),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    content()
                }

                /* Action buttons */
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = Dimens.small8,
                            horizontal = Dimens.extraSmall4,
                        )
                        .align(Alignment.End),
                ) {
                    if (isSubmittable) {
                        DialogActionButton(
                            onClick = onDismiss,
                            buttonStringResId = R.string.dialog_cancel,
                        )

                        DialogActionButton(
                            onClick = onSubmit,
                            buttonStringResId = R.string.dialog_ok,
                        )
                    } else {
                        DialogActionButton(
                            onClick = onDismiss,
                            buttonStringResId = R.string.dialog_close,
                        )
                        Spacer(modifier = Modifier.width(Dimens.small8))
                    }
                }
            }
        }
    }
}


@Composable
private fun TimeTrialDialog(
    userMinutesInput: String,
    userSecondsInput: String,
    onUserMinutesInputChange: (String) -> Unit,
    onUserSecondsInputChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onTimeTrial: (Int) -> Unit,
) {
    val inputStyle = MaterialTheme.typography.headlineLarge
    val inputWidth = 68.dp
    val inputShape = MaterialTheme.shapes.large
    val inputAnnotationStyle = MaterialTheme.typography.titleLarge
    val focusRequesterMinutes = remember { FocusRequester() }
    val focusRequesterSeconds = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) { focusRequesterMinutes.requestFocus() }

    fun onTimeTrialAction() {
        val timeMinute = when (userMinutesInput) {
            "" -> 0
            else -> userMinutesInput.toInt() * 60
        }
        val timeSecond = when (userSecondsInput) {
            "" -> 0
            else -> userSecondsInput.toInt()
        }

        onTimeTrial(timeMinute + timeSecond)
        onDismiss()
    }

    GameDialog(
        title = R.string.game_time_trial_title,
        isSubmittable = true,
        onDismiss = onDismiss,
        onSubmit = { onTimeTrialAction() },
    ) {
        /* Time trial inputs */
        Row(
            modifier = Modifier.padding(top = Dimens.large24),
            verticalAlignment = Alignment.Bottom,
        ) {
            /* Minutes */
            OutlinedTextField(
                value = userMinutesInput,
                onValueChange = onUserMinutesInputChange,
                modifier = Modifier
                    .width(inputWidth)
                    .focusRequester(focusRequesterMinutes),
                textStyle = inputStyle,
                placeholder = {
                    Text(
                        text = stringResource(R.string.game_time_trial_input_placeholder),
                        style = inputStyle,
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterSeconds.requestFocus() }
                ),
                singleLine = true,
                shape = inputShape
            )
            Text(
                text = stringResource(R.string.game_time_trial_minute),
                modifier = Modifier.padding(horizontal = Dimens.small8),
                style = inputAnnotationStyle,
            )

            /* Seconds */
            OutlinedTextField(
                value = userSecondsInput,
                onValueChange = onUserSecondsInputChange,
                modifier = Modifier
                    .width(inputWidth)
                    .focusRequester(focusRequesterSeconds),
                textStyle = inputStyle,
                placeholder = {
                    Text(
                        text = stringResource(R.string.game_time_trial_input_placeholder),
                        style = inputStyle,
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onTimeTrialAction() }
                ),
                singleLine = true,
                shape = inputShape
            )
            Text(
                text = stringResource(R.string.game_time_trial_second),
                modifier = Modifier.padding(start = Dimens.small8),
                style = inputAnnotationStyle,
            )
        }
    }
}


@Composable
private fun InfoDialog(onDismiss: () -> Unit) {
    GameDialog(
        title = R.string.game_info_dialog_title,
        isSubmittable = false,
        onDismiss = onDismiss,
        onSubmit = {},
    ) {
        /* Answer Mode description card */
        InfoDialogCard(
            containerColor = MaterialTheme.colorScheme.secondary,
            title = R.string.game_mode_answer_title,
        ) {
            AnswerMode.entries.forEach { answerMode ->
                InfoDialogCardItemText(
                    title = answerMode.title,
                    description = answerMode.description,
                    description2 = answerMode.description2,
                )
            }
        }

        /* Guess Limit description card */
        InfoDialogCard(
            containerColor = MaterialTheme.colorScheme.tertiary,
            title = R.string.game_mode_difficulty_title,
        ) {
            InfoDialogCardItemText(
                description = R.string.game_mode_difficulty_description
            )

            DifficultyMode.entries.forEach { difficultyMode ->
                if (difficultyMode.icon != null && difficultyMode.description != null) {
                    InfoDialogCardItemIcon(
                        icon = difficultyMode.icon,
                        description = difficultyMode.description,
                    )
                }
            }
        }

        /* Top bar button(s) description */
        InfoDialogCard(
            containerColor = MaterialTheme.colorScheme.primary,
            title = R.string.game_info_dialog_top_bar_title,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopBarTimer(
                    isTimerEnabled = true,
                    isClickEnabled = false,
                )

                InfoDialogCardItemText(
                    modifier = Modifier.padding(start = Dimens.extraSmall4),
                    description = R.string.game_info_dialog_timer_description,
                )
            }
        }
    }
}

@Composable
private fun InfoDialogCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    @StringRes title: Int,
    content: @Composable (ColumnScope.() -> Unit),
) {
    Card(
        modifier = modifier.padding(top = Dimens.medium16),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.225f)
        ),
    ) {
        Column(
            modifier = Modifier.padding(all = Dimens.medium12)
        ) {
            /* TITLE */
            Text(
                text = stringResource(id = title),
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
            )

            /* DESCRIPTION content */
            content()
        }
    }
}

@Composable
private fun InfoDialogCardItemText(
    modifier: Modifier = Modifier,
    @StringRes title: Int? = null,
    @StringRes description: Int,
    @StringRes description2: Int? = null,
) {
    InfoDialogCardItem(
        modifier = modifier.padding(top = Dimens.extraSmall4),
        description = description,
        description2 = description2,
    ) {
        title?.let {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.labelLarge,
            )

            Text(
                text = stringResource(id = R.string.string_colon_whitespace),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun InfoDialogCardItemIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes description: Int,
) {
    InfoDialogCardItem(
        modifier = modifier.padding(top = Dimens.extraSmall4 / 2),
        description2 = description,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.tertiary)
                .padding(vertical = 23.dp / 10, horizontal = Dimens.small8),
            tint = MaterialTheme.colorScheme.onTertiary,
        )

        Text(
            text = stringResource(id = R.string.string_equals_whitespace),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun InfoDialogCardItem(
    modifier: Modifier = Modifier,
    @StringRes description: Int? = null,
    @StringRes description2: Int? = null,
    titleContent: @Composable (RowScope.() -> Unit),
) {
    val rowVerticalAlignment =
        if (description != null && description2 != null) Alignment.Top
        else Alignment.CenterVertically

    Row(
        modifier = modifier,
        verticalAlignment = rowVerticalAlignment,
    ) {
        /* Eg. Mode title */
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                titleContent()
            }
        }

        /* Eg. Mode description */
        Column {
            description?.let {
                Text(
                    text = stringResource(id = description),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            description2?.let {
                Text(
                    text = stringResource(id = description2),
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}


@Composable
private fun GameModesDialog(
    answerMode: AnswerMode,
    difficultyMode: DifficultyMode,
    onDismiss: () -> Unit,
    onGameModes: (AnswerMode, DifficultyMode) -> Unit,
) {
    var answerModeSelect by rememberSaveable { mutableStateOf(value = answerMode) }
    var difficultyModeSelect by rememberSaveable { mutableStateOf(value = difficultyMode) }

    LaunchedEffect(key1 = answerModeSelect) {
        if (answerModeSelect == AnswerMode.DATES && difficultyModeSelect == DifficultyMode.EASY) {
            difficultyModeSelect = DifficultyMode.MEDIUM
        }
    }

    GameDialog(
        title = R.string.game_modes_dialog_title,
        isSubmittable = true,
        onDismiss = onDismiss,
        onSubmit = {
            onGameModes(answerModeSelect, difficultyModeSelect)
            onDismiss()
        },
    ) {
        /* Answer mode selection */
        GameModesDialogAnswerItem(
            answerModeSelect = answerModeSelect,
            onAnswerMode = { answerModeSelect = it },
        )

        /* Difficulty mode selection */
        GameModesDialogDifficultyItem(
            answerModeSelect = answerModeSelect,
            difficultyModeSelect = difficultyModeSelect,
            onDifficultyMode = { difficultyModeSelect = it },
        )
    }
}

@Composable
private fun GameModesDialogAnswerItem(
    answerModeSelect: AnswerMode,
    onAnswerMode: (AnswerMode) -> Unit,
) {
    GameModesDialogItemTitle(title = R.string.game_mode_answer_title)

    Row {
        AnswerMode.entries.forEachIndexed { index, answerMode ->
            val endPadding = if (index != AnswerMode.entries.lastIndex) Dimens.small8 else 0.dp

            GameModesDialogItemButton(
                modifier = Modifier.padding(end = endPadding),
                text = stringResource(id = answerMode.title),
                icon = null,
                isEnabled = true,
                isSelected = answerMode == answerModeSelect,
                onMode = { onAnswerMode(answerMode) },
            )
        }
    }
}

@Composable
private fun GameModesDialogDifficultyItem(
    answerModeSelect: AnswerMode,
    difficultyModeSelect: DifficultyMode,
    onDifficultyMode: (DifficultyMode) -> Unit,
) {
    val isDatesMode = answerModeSelect == AnswerMode.DATES

    GameModesDialogItemTitle(title = R.string.game_mode_difficulty_title)

    Row {
        DifficultyMode.entries.forEachIndexed { index, difficultyMode ->
            val isEasyMode = difficultyMode == DifficultyMode.EASY

            GameModesDialogItemButton(
                text = difficultyMode.guessLimit?.toString(),
                icon = difficultyMode.icon,
                isEnabled = !(isEasyMode && isDatesMode),
                isSelected = difficultyMode == difficultyModeSelect,
                onMode = { onDifficultyMode(difficultyMode) },
            )
        }
    }
}

@Composable
private fun GameModesDialogItemTitle(@StringRes title: Int) = Text(
    text = stringResource(id = title),
    modifier = Modifier.padding(top = Dimens.large24),
    style = MaterialTheme.typography.labelMedium,
)

@Composable
private fun GameModesDialogItemButton(
    modifier: Modifier = Modifier,
    text: String?,
    icon: ImageVector?,
    isEnabled: Boolean,
    isSelected: Boolean,
    onMode: () -> Unit,
) {
    val buttonContainerColor =
        if (isSelected) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.outlineVariant

    Card(
        onClick = onMode,
        modifier = modifier,
        enabled = isEnabled,
        shape =
            if (isSelected) MaterialTheme.shapes.large
            else MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = buttonContainerColor),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(
                    vertical = Dimens.extraSmall6,
                    horizontal = Dimens.small8,
                ),
            )
        } ?: text?.let {
            Text(
                text = text,
                modifier = Modifier.padding(
                    vertical = Dimens.small8,
                    horizontal = Dimens.medium16,
                ),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}


@Composable
private fun ConfirmDialog(
    type: GameConfirmDialog,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title = when (type) {
        GameConfirmDialog.EXIT -> R.string.game_confirm_exit_title
        GameConfirmDialog.RESET -> R.string.game_confirm_reset_title
    }
    val description = when (type) {
        GameConfirmDialog.EXIT -> R.string.game_confirm_exit_description
        GameConfirmDialog.RESET -> R.string.game_confirm_reset_description
    }

    GameDialog(
        title = title,
        isSubmittable = true,
        onDismiss = onDismiss,
        onSubmit = onConfirm,
    ) {
        /* Description */
        Row(
            modifier = Modifier.padding(
                top = Dimens.large24,
                end = Dimens.large24,
            ),
        ) {
            Text(
                text = stringResource(description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}


/* End game popup dialog showing final score. With buttons: Leave game, Share score, Play again */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameOverDialog(
    modifier: Modifier = Modifier,
    finalScore: Int,
    maxScore: Int,
    superCategories: List<FlagSuperCategory>,
    subCategories: List<FlagCategory>,
    answerMode: AnswerMode,
    difficultyMode: DifficultyMode,
    onDetails: () -> Unit,
    onScoreHistory: () -> Unit,
    onShare: (String) -> Unit,
    onExit: () -> Unit,
    onReplay: () -> Unit,
) {
    val categoriesString = buildString {
        getCategoriesTitleIds(superCategories, subCategories, isAppendFlags = false)
            .forEach { append(stringResource(it)) }
    }

    val scoreMessage =
        if (finalScore == 0) stringResource(R.string.game_over_text_min_score)
        else when (answerMode) {
            AnswerMode.NAMES -> stringResource(R.string.game_over_text_names, finalScore, maxScore)
            AnswerMode.DATES -> stringResource(R.string.game_over_text_dates, finalScore, maxScore)
        }

    val shareScoreMessage = stringResource(
        R.string.game_over_share_text,
        finalScore,
        maxScore,
        categoriesString,
        stringResource(answerMode.title),
        stringResource(difficultyMode.title)
    )


    BasicAlertDialog(
        onDismissRequest = {},
        modifier = modifier,
    ) {
        Card(
            modifier = Modifier.wrapContentWidth(),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.padding(
                    top = Dimens.large24,
                    start = Dimens.large24,
                    end = Dimens.large24,
                ),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                /* Title and exit & replay action buttons */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onExit) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.graphicsLayer(scaleX = -1f)
                        )
                    }

                    Text(
                        text = stringResource(R.string.game_over_title),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    IconButton(onClick = onReplay) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                        )
                    }
                }

                /* Description */
                Row(
                    modifier = Modifier.padding(
                        top = Dimens.medium16,
                        bottom = Dimens.small8,
                    ),
                ) {
                    Text(
                        text = scoreMessage,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                /* Action buttons */
                Row(
                    modifier = Modifier
                        .padding(top = Dimens.small8, bottom = Dimens.medium16)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DialogActionButton(
                        onClick = onDetails,
                        buttonStringResId = R.string.game_over_details_button,
                    )

                    DialogActionButton(
                        onClick = onScoreHistory,
                        buttonStringResId = R.string.game_over_score_history_button,
                    )

                    DialogActionButton(
                        onClick = { onShare(shareScoreMessage) },
                        buttonStringResId = R.string.game_over_share_button,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBarTimer(
    timerText: String = "0:00",
    isTimerEnabled: Boolean,
    isClickEnabled: Boolean,
    onClick: () -> Unit = {},
) {
    Text(
        text = timerText,
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(
                enabled = isClickEnabled,
                onClick = onClick,
            )
            .padding(
                vertical = Dimens.extraSmall4,
                horizontal = Dimens.extraSmall6,
            ),
        color =
            if (isTimerEnabled) MaterialTheme.colorScheme.error
            else LocalContentColor.current,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopBar(
    modifier: Modifier = Modifier,
    screen: Screen,
    isFlags: Boolean,
    isTimeTrial: Boolean,
    isGame: Boolean,
    isGamePaused: Boolean,
    isGameOver: Boolean,
    timer: String = "0:00",
    onNavigationDrawer: () -> Unit,
    onResetAction: () -> Unit,
    onTimeAction: () -> Unit,
    onInfoAction: () -> Unit,
    onSettingsAction: () -> Unit,
    onHistoryAction: () -> Unit,
) {
    val isTimerEnabled = !(!isGame || isGamePaused || isGameOver)

    TopAppBar(
        title = {
            Text(
                text = stringResource(screen.title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigationDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_drawer_content_description),
                )
            }
        },
        actions = {
            TopBarTimer(
                timerText = timer,
                isTimerEnabled = isTimerEnabled,
                isClickEnabled = isTimerEnabled,
                onClick = onResetAction,
            )

            IconButton(
                onClick = onTimeAction,
                enabled = isFlags,
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint =
                        if (isTimeTrial && !isGameOver) MaterialTheme.colorScheme.error
                        else LocalContentColor.current,
                )
            }

            IconButton(onClick = onInfoAction) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                )
            }

            IconButton(onClick = onSettingsAction) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                )
            }

            IconButton(
                onClick = onHistoryAction,
                modifier = Modifier.padding(end = Dimens.small8)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                )
            }
        },
    )
}


// Preview screen in Android Studio
/*
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun GameScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        GameScreen(
            currentScreen = Screen.Game,
            canNavigateBack = true,
            onNavigateUp = { },
        )
    }
}
 */