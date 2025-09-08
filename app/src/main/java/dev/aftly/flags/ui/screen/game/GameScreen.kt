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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
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
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.game.AnswerMode
import dev.aftly.flags.model.game.DifficultyMode
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.CategoriesButtonMenu
import dev.aftly.flags.ui.component.DialogActionButton
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
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    currentBackStackEntry: NavBackStackEntry?,
    screen: Screen,
    isNavigationDrawerOpen: Boolean,
    onNavigateToList: Boolean,
    onResetNavigateToList: () -> Unit,
    onNavigationDrawer: () -> Unit,
    onExit: () -> Unit,
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

    LaunchedEffect(currentBackStackEntry) {
        systemUiController.setLightStatusBar(light = !isDarkTheme)
        systemUiController.setSystemBars(visible = true)

        currentBackStackEntry?.savedStateHandle?.get<Boolean>("isGameOver")?.let { isGameOver ->
            if (isGameOver) viewModel.toggleGameOverDialog(on = true)
        }
    }

    /* When language configuration changes, update strings in uiState */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }


    /* Handle if confirm exit from game progress when navigation back from NavigationDrawer or
     * system back action ------------------------------- */
    LaunchedEffect(key1 = onNavigateToList) {
        if (onNavigateToList) {
            if (viewModel.isScoresEmpty()) onExit()
            else viewModel.toggleConfirmExitDialog(on = true)
        }
    }

    BackHandler {
        if (isNavigationDrawerOpen) onNavigationDrawer()
        else if (viewModel.isScoresEmpty()) onExit()
        else viewModel.toggleConfirmExitDialog(on = true)
    }

    if (uiState.isConfirmExitDialog) {
        ConfirmExitDialog(
            onExit = {
                viewModel.toggleConfirmExitDialog(on = false)
                onExit()
            },
            onDismiss = {
                viewModel.toggleConfirmExitDialog(on = false)
                if (onNavigateToList) onResetNavigateToList()
            },
        )
    }
    /* -------------------------------------------------- */

    /* Show time trial dialog when timer action button pressed */
    if (uiState.isTimeTrialDialog) {
        TimeTrialDialog(
            userMinutesInput = viewModel.userTimerInputMinutes,
            userSecondsInput = viewModel.userTimerInputSeconds,
            onUserMinutesInputChange = { viewModel.updateUserMinutesInput(it) },
            onUserSecondsInputChange = { viewModel.updateUserSecondsInput(it) },
            onDismiss = { viewModel.toggleTimeTrialDialog(on = false) },
            onTimeTrial = { viewModel.setTimeTrial(it) },
        )
    }

    if (uiState.isGameModesDialog) {
        GameModesDialog(
            answerMode = uiState.answerMode,
            difficultyMode = uiState.difficultyMode,
            onDismiss = { viewModel.toggleGameModesDialog(on = false) },
            onGameModes = { answerMode, difficultyMode ->
                viewModel.updateGameModes(answerMode, difficultyMode)
            }
        )
    }

    /* Show pop-up when game over */
    if (uiState.isGameOverDialog) {
        val context = LocalContext.current
        GameOverDialog(
            finalScore = uiState.correctGuessCount,
            maxScore = uiState.totalFlagCount,
            gameMode = "TODO", // TODO
            onDetails = {
                viewModel.toggleGameOverDialog(on = false)
                viewModel.toggleScoreDetails(on = true)
            },
            onScoreHistory = {
                viewModel.toggleGameOverDialog(on = false)
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
                viewModel.toggleGameOverDialog(on = false)
                onExit()
            },
            onReplay = { viewModel.resetGame() },
        )
    }


    GameScreen(
        uiState = uiState,
        savedFlags = savedFlags,
        screen = screen,
        userGuess = viewModel.userGuess,
        onUserGuessChange = { viewModel.updateUserGuess(it) },
        onCloseScoreDetails = {
            viewModel.toggleScoreDetails(on = false)
            viewModel.endGame()
        },
        onSubmit = { viewModel.checkUserGuess() },
        onSkip = { viewModel.skipFlag() },
        onConfirmShowAnswer = { viewModel.confirmShowAnswer() },
        onShowAnswer = { viewModel.showAnswer() },
        onStartGame = { viewModel.startGame() },
        onEndGame = { viewModel.endGame() },
        onResetGame = { viewModel.resetGame() },
        onNavigationDrawer = onNavigationDrawer,
        onTimeTrialDialog = { viewModel.toggleTimeTrialDialog(on = true) },
        onGameModesDialog = { viewModel.toggleGameModesDialog(on = true) },
        onScoreHistory = { onScoreHistory(uiState.isGameOver) },
        onFullscreen = { isFlagWide ->
            onFullscreen(uiState.currentFlag, isFlagWide, !uiState.isShowAnswer)
        },
        onCategorySelectSingle = { newSuperCategory, newSubCategory ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
        },
        onCategorySelectMultiple = { newSuperCategory, newSubCategory ->
            viewModel.updateCurrentCategories(newSuperCategory, newSubCategory)
        },
        onSavedFlagsSelect = { viewModel.selectSavedFlags() },
    )
}


@Composable
private fun GameScreen(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    savedFlags: List<FlagView>,
    screen: Screen,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onCloseScoreDetails: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onConfirmShowAnswer: () -> Unit,
    onShowAnswer: () -> Unit,
    onStartGame: () -> Unit,
    onEndGame: () -> Unit,
    onResetGame: () -> Unit,
    onNavigationDrawer: () -> Unit,
    onTimeTrialDialog: () -> Unit,
    onGameModesDialog: () -> Unit,
    onScoreHistory: () -> Unit,
    onFullscreen: (Boolean) -> Unit,
    onCategorySelectSingle: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategorySelectMultiple: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onSavedFlagsSelect: () -> Unit,
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

    /* Collapse menu when no flags */
    LaunchedEffect(uiState.currentFlags) {
        if (uiState.currentFlags.isEmpty()) isMenuExpanded = false
    }

    /* Manage timer string */
    val timer = when (uiState.isTimeTrial) {
        true -> uiState.timerTimeTrial
        false -> uiState.timerStandard
    }
    val timerString = String.format(
        locale = Locale.US,
        format = "%d:%02d",
        timer / 60, timer % 60
    )

    /* Manage guess field focus and unpause game */
    var isGuessFieldFocused by rememberSaveable { mutableStateOf(value = false) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(isMenuExpanded) {
        when (isMenuExpanded) {
            true -> focusManager.clearFocus()
            false -> onStartGame()
        }
    }


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
                    isGame = uiState.isGame,
                    isFlags = uiState.currentFlags.isNotEmpty(),
                    isTimeTrial = uiState.isTimeTrial,
                    isTimerPaused = uiState.isTimerPaused,
                    timer = timerString,
                    onNavigationDrawer = {
                        focusManager.clearFocus()
                        onNavigationDrawer()
                    },
                    onTimeAction = {
                        if (uiState.isTimeTrial) {
                            onResetGame()
                        } else {
                            focusManager.clearFocus()
                            onTimeTrialDialog()
                        }
                    },
                    onSettingsAction = {
                        focusManager.clearFocus()
                        onGameModesDialog()
                    },
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
                answerMode = uiState.answerMode,
                isGame = uiState.isGame,
                isGameOver = uiState.isGameOver,
                isWideScreen = isWideScreen,
                filterButtonHeight = buttonHeight,
                totalFlagCount = uiState.totalFlagCount,
                correctGuessCount = uiState.correctGuessCount,
                shownFailedAnswerCount = uiState.shownAnswerCount + uiState.failedAnswerCount,
                currentFlag = uiState.currentFlag,
                userGuess = userGuess,
                onUserGuessChange = onUserGuessChange,
                onFocusChanged = { isGuessFieldFocused = it },
                isGuessFieldFocused = isGuessFieldFocused,
                isTimeTrialDialog = uiState.isTimeTrialDialog,
                isMenuExpanded = isMenuExpanded,
                isGuessCorrect = uiState.isGuessCorrect,
                isGuessCorrectEvent = uiState.isGuessCorrectEvent,
                isGuessWrong = uiState.isGuessWrong,
                isGuessWrongEvent = uiState.isGuessWrongEvent,
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
        CategoriesButtonMenu(
            modifier = Modifier.fillMaxSize(),
            scaffoldPadding = scaffoldPaddingValues,
            buttonHorizontalPadding = Dimens.marginHorizontal16,
            flagCount = null,
            onButtonHeightChange = { buttonHeight = it },
            isMenuEnabled = uiState.answerMode != AnswerMode.DATES,
            isMenuExpanded = isMenuExpanded,
            onMenuButtonClick = { isMenuExpanded = !isMenuExpanded },
            isSavedFlagsNotEmpty = savedFlags.isNotEmpty(),
            superCategories = uiState.currentSuperCategories,
            subCategories = uiState.currentSubCategories,
            onCategorySelectSingle = onCategorySelectSingle,
            onCategorySelectMultiple = onCategorySelectMultiple,
            onSavedFlagsSelect = onSavedFlagsSelect,
        )


        /* For displaying verbose score details */
        if (uiState.scoreDetails != null) {
            ScoreDetails(
                visible = uiState.isScoreDetails,
                scoreDetails = uiState.scoreDetails,
                onClose = onCloseScoreDetails,
            )
        }
    }
}


@Composable
private fun GameContent(
    modifier: Modifier = Modifier,
    answerMode: AnswerMode,
    isGame: Boolean,
    isGameOver: Boolean,
    isWideScreen: Boolean,
    filterButtonHeight: Dp,
    totalFlagCount: Int,
    correctGuessCount: Int,
    shownFailedAnswerCount: Int,
    currentFlag: FlagView,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isGuessFieldFocused: Boolean,
    isTimeTrialDialog: Boolean,
    isMenuExpanded: Boolean,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
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
        /* Spacer to make content start below FilterFlags button */
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(filterButtonHeight / 2 + aspectRatioTopPadding)
        )

        GameCard(
            answerMode = answerMode,
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
            currentFlag = currentFlag,
            userGuess = userGuess,
            onUserGuessChange = onUserGuessChange,
            onFocusChanged = onFocusChanged,
            isGuessFieldFocused = isGuessFieldFocused,
            isTimeTrialDialog = isTimeTrialDialog,
            isMenuExpanded = isMenuExpanded,
            isGuessCorrect = isGuessCorrect,
            isGuessCorrectEvent = isGuessCorrectEvent,
            isGuessWrong = isGuessWrong,
            isGuessWrongEvent = isGuessWrongEvent,
            isShowAnswer = isShowAnswer,
            onSubmit = onSubmit,
            onEndGame = onEndGame,
            onImageWide = onImageWide,
            onFullscreen = onFullscreen,
        )
        /* Something makes Submit button's top padding less than Skip button's  */
        Spacer(modifier = Modifier.height(2.dp))

        /* Submit button */
        Button(
            onClick = onSubmit,
            modifier = Modifier
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
            enabled = isGame && !isGameOver && !isShowAnswer,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = showAnswerColor),
        ) {
            Text(text = showAnswerText)
        }
    }
}


@Composable
private fun GameCard(
    modifier: Modifier = Modifier,
    answerMode: AnswerMode,
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
    currentFlag: FlagView,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isGuessFieldFocused: Boolean,
    isTimeTrialDialog: Boolean,
    isMenuExpanded: Boolean,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
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

    val gameModeLabelResId = when (answerMode) {
        AnswerMode.NAMES -> R.string.game_guess_field_names_label
        AnswerMode.DATES -> R.string.game_guess_field_dates_label
    }
    val labelDefault = stringResource(gameModeLabelResId)
    val labelSuccess = stringResource(R.string.game_guess_field_label_success)
    val labelError = stringResource(R.string.game_guess_field_label_error)
    var labelState by remember { mutableStateOf(value = labelDefault) }

    /* LaunchedEffects for triggering animations of above variables */
    LaunchedEffect(answerMode) {
        if (labelState != labelSuccess && labelState != labelError)
            labelState = labelDefault
    }
    LaunchedEffect(isGuessCorrectEvent) {
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
        }
    }
    LaunchedEffect(isGuessWrongEvent) {
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
        }
    }

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

    /* Manage FocusRequester */
    val focusRequesterGuessField = remember { FocusRequester() }
    /* Focus guess field on nav back if previously focused */
    LaunchedEffect(Unit) {
        if (isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    /* Focus guess field on TimeTrialDialog close if previously focused */
    LaunchedEffect(isTimeTrialDialog) {
        if (!isTimeTrialDialog && isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }
    /* Focus guess field on menu collapse if previously focused */
    LaunchedEffect(isMenuExpanded) {
        if (!isMenuExpanded && isGuessFieldFocused) focusRequesterGuessField.requestFocus()
    }


    /* Game Card content */
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = cardWidthModifier.padding(Dimens.large24),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /* Top row in card */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.medium16),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                /* Flag counters and game mode */
                Row {
                    Text(
                        text = buildString {
                            val progress = correctGuessCount + shownFailedAnswerCount
                            append(progress.toString())
                            append(stringResource(R.string.string_forward_slash))
                            append(totalFlagCount.toString())
                        },
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    if (shownFailedAnswerCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = shownFailedAnswerCount.toString(),
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.error)
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
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                Row {
                    /* Finish game button */
                    TextButton(
                        onClick = onEndGame,
                        modifier = Modifier.height(Dimens.extraLarge32),
                        enabled = isGame && !isGameOver,
                        shape = RoundedCornerShape(Dimens.small8),
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
                        painter = painterResource(currentFlag.imagePreview),
                        contentDescription = null,
                        alpha = 0f,
                    )

                    Image(
                        modifier = Modifier
                            .onSizeChanged { size ->
                                onCardImageHeightChange(
                                    with(density) { size.height.toDp() }
                                )
                                onCardImageWidthChange(
                                    with(density) { size.width.toDp() }
                                )

                                /* Set image height modifier */
                                if (contentColumnHeight < cardImageHeight) {
                                    onCardImageHeightModifierChange(
                                        Modifier.height(height = contentColumnHeight)
                                    )
                                    onCardWidthModifierChange(
                                        Modifier.width(width = (cardImageWidth.value * 1.25).dp)
                                    )
                                } else {
                                    onCardImageHeightModifierChange(
                                        Modifier.height(height = cardImageHeight)
                                    )
                                    onCardWidthModifierChange(
                                        Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            /* Concatenate height after onSizeChanged */
                            .then(cardImageHeightModifier)
                            /* Force aspect ratio to keep game card shape consistent */
                            .aspectRatio(ratio = 3f / 2f),
                        painter = painterResource(currentFlag.image),
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
                            modifier = Modifier.padding(Dimens.medium16),
                            contentAlignment = Alignment.Center,
                        ) {
                            correctAnswer?.let { answer ->
                                Text(
                                    text = answer,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.large)
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
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    content: @Composable (() -> Unit),
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
                        text = stringResource(title),
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
                    DialogActionButton(
                        onClick = onDismiss,
                        buttonStringResId = R.string.dialog_cancel,
                    )

                    DialogActionButton(
                        onClick = onSubmit,
                        buttonStringResId = R.string.dialog_ok,
                    )
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
private fun GameModesDialog(
    answerMode: AnswerMode,
    difficultyMode: DifficultyMode,
    onDismiss: () -> Unit,
    onGameModes: (AnswerMode, DifficultyMode) -> Unit,
) {
    var answerModeSelected by rememberSaveable { mutableStateOf(value = answerMode) }
    var difficultyModeSelected by rememberSaveable { mutableStateOf(value = difficultyMode) }

    val cardColorsSelected = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
    val cardColorsUnselected = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.outlineVariant
    )

    GameDialog(
        title = R.string.game_modes_title,
        onDismiss = onDismiss,
        onSubmit = {
            onGameModes(answerModeSelected, difficultyModeSelected)
            onDismiss()
        },
    ) {
        /* Answer mode selection */
        Text(
            text = "Answer mode:",
            modifier = Modifier.padding(top = Dimens.large24),
            style = MaterialTheme.typography.labelMedium,
        )
        Row {
            /* NAMES answer mode button */
            Card(
                onClick = { answerModeSelected = AnswerMode.NAMES },
                shape =
                    if (answerModeSelected == AnswerMode.NAMES) MaterialTheme.shapes.large
                    else MaterialTheme.shapes.extraSmall,
                colors =
                    if (answerModeSelected == AnswerMode.NAMES) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Text(
                    text = stringResource(AnswerMode.NAMES.title),
                    modifier = Modifier.padding(
                        vertical = Dimens.small8,
                        horizontal = Dimens.medium16,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Spacer(Modifier.width(Dimens.small8))

            /* DATES answer mode button */
            Card(
                onClick = { answerModeSelected = AnswerMode.DATES },
                shape =
                    if (answerModeSelected == AnswerMode.DATES) MaterialTheme.shapes.large
                    else MaterialTheme.shapes.extraSmall,
                colors =
                    if (answerModeSelected == AnswerMode.DATES) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Text(
                    text = stringResource(AnswerMode.DATES.title),
                    modifier = Modifier.padding(
                        vertical = Dimens.small8,
                        horizontal = Dimens.medium16,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }

        /* Difficulty mode selection */
        Text(
            text = "Answer limit:",
            modifier = Modifier.padding(top = Dimens.large24),
            style = MaterialTheme.typography.labelMedium,
        )
        Row {
            /* EASY difficulty mode button */
            Card(
                onClick = { difficultyModeSelected = DifficultyMode.EASY },
                shape =
                    if (difficultyModeSelected == DifficultyMode.EASY) MaterialTheme.shapes.large
                    else MaterialTheme.shapes.extraSmall,
                colors =
                    if (difficultyModeSelected == DifficultyMode.EASY) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    modifier = Modifier.padding(
                        vertical = Dimens.extraSmall6,
                        horizontal = Dimens.small8,
                    ),
                )
            }

            /* MEDIUM difficulty mode button */
            Card(
                onClick = { difficultyModeSelected = DifficultyMode.MEDIUM },
                shape =
                    if (difficultyModeSelected == DifficultyMode.MEDIUM) MaterialTheme.shapes.large
                    else MaterialTheme.shapes.extraSmall,
                colors =
                    if (difficultyModeSelected == DifficultyMode.MEDIUM) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Text(
                    text = DifficultyMode.MEDIUM.guessLimit.toString(),
                    modifier = Modifier.padding(
                        vertical = Dimens.small8,
                        horizontal = Dimens.medium16,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            /* HARD difficulty mode button */
            Card(
                onClick = { difficultyModeSelected = DifficultyMode.HARD },
                shape =
                    if (difficultyModeSelected == DifficultyMode.HARD) MaterialTheme.shapes.large
                    else MaterialTheme.shapes.extraSmall,
                colors =
                    if (difficultyModeSelected == DifficultyMode.HARD) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Text(
                    text = DifficultyMode.HARD.guessLimit.toString(),
                    modifier = Modifier.padding(
                        vertical = Dimens.small8,
                        horizontal = Dimens.medium16,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            /* SUDDEN_DEATH difficulty mode button */
            Card(
                onClick = { difficultyModeSelected = DifficultyMode.SUDDEN_DEATH },
                shape =
                    if (difficultyModeSelected == DifficultyMode.SUDDEN_DEATH)
                        MaterialTheme.shapes.large
                    else
                        MaterialTheme.shapes.extraSmall,
                colors =
                    if (difficultyModeSelected == DifficultyMode.SUDDEN_DEATH) cardColorsSelected
                    else cardColorsUnselected,
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    modifier = Modifier.padding(
                        vertical = Dimens.extraSmall6,
                        horizontal = Dimens.small8,
                    ),
                )
            }
        }
    }
}


@Composable
private fun ConfirmExitDialog(
    onDismiss: () -> Unit,
    onExit: () -> Unit,
) {
    GameDialog(
        title = R.string.game_confirm_exit_title,
        onDismiss = onDismiss,
        onSubmit = onExit,
    ) {
        /* Description */
        Row(
            modifier = Modifier.padding(
                top = Dimens.large24,
                end = Dimens.large24,
            ),
        ) {
            Text(
                text = stringResource(R.string.game_confirm_exit_description),
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
    gameMode: String,
    onDetails: () -> Unit,
    onScoreHistory: () -> Unit,
    onShare: (String) -> Unit,
    onExit: () -> Unit,
    onReplay: () -> Unit,
) {
    val scoreMessage = when (finalScore) {
        maxScore -> stringResource(R.string.game_over_text_max_score)
        0 -> stringResource(R.string.game_over_text_min_score)
        else -> stringResource(R.string.game_over_text, finalScore, maxScore)
    }

    val shareScoreMessage = stringResource(
        R.string.game_over_share_text, finalScore, maxScore, gameMode
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopBar(
    modifier: Modifier = Modifier,
    screen: Screen,
    isGame: Boolean,
    isFlags: Boolean,
    isTimeTrial: Boolean,
    isTimerPaused: Boolean,
    timer: String = "0:00",
    onNavigationDrawer: () -> Unit,
    onTimeAction: () -> Unit,
    onSettingsAction: () -> Unit,
    onHistoryAction: () -> Unit,
) {
    val activeTimerColor = MaterialTheme.colorScheme.error

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
            Text(
                text = timer,
                modifier = Modifier.padding(end = Dimens.extraSmall4),
                color = when (isGame to isTimerPaused) {
                    true to false -> activeTimerColor
                    else -> MaterialTheme.colorScheme.outline
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            IconButton(
                onClick = onTimeAction,
                enabled = isFlags,
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (isTimeTrial) activeTimerColor else LocalContentColor.current,
                )
            }

            IconButton(onClick = onSettingsAction) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                )
            }

            IconButton(onClick = onHistoryAction) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.width(Dimens.small8))
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