package dev.aftly.flags.ui.screen.game

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.FilterFlagsButton
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.component.NoResultsFound
import dev.aftly.flags.ui.component.Scrim
import dev.aftly.flags.ui.component.GeneralTopBar
import dev.aftly.flags.ui.component.ScoreDetails
import dev.aftly.flags.ui.component.shareText
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.successDark
import dev.aftly.flags.ui.theme.successLight
import dev.aftly.flags.ui.theme.surfaceLight
import dev.aftly.flags.ui.util.SystemUiController
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    navController: NavHostController,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onFullscreen: (Int, Boolean, Boolean) -> Unit,
) {
    /* Expose screen and backStack state */
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry = navController.currentBackStackEntryAsState()

    /* Manage system bars and flag state after returning from FullScreen */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(window, view) }
    val isDarkTheme by rememberUpdatedState(newValue = isSystemInDarkTheme())

    LaunchedEffect(backStackEntry) {
        systemUiController.setLightStatusBar(light = !isDarkTheme)
        systemUiController.setSystemBars(visible = true)
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isWideScreen = remember {
        context.resources.displayMetrics.widthPixels > context.resources.displayMetrics.heightPixels
    }

    /* When language configuration changes, update strings in uiState */
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.setFlagStrings() }

    /* Manage timer string */
    val formattedTimer = String.format(
        locale = Locale.US,
        format = "%d:%02d",
        uiState.timer / 60, uiState.timer % 60
    )

    /* Show pop-up when game over */
    if (uiState.isGameOver) {
        GameOverDialog(
            finalScore = uiState.correctGuessCount,
            maxScore = uiState.totalFlagCount,
            gameMode = stringResource(uiState.currentSuperCategory.title),
            onDetails = {
                viewModel.endGame(isGameOver = false)
                viewModel.scoreDetails(newState = true)
            },
            onShare = { text ->
                shareText(
                    context = context,
                    subject = R.string.game_over_share_subject,
                    textToShare = text,
                )
            },
            onPlayAgain = { viewModel.resetGame() },
        )
    }


    GameScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        isWideScreen = isWideScreen,
        timer = formattedTimer,
        totalFlagCount = uiState.totalFlagCount,
        correctGuessCount = uiState.correctGuessCount,
        shownAnswerCount = uiState.shownAnswerCount,
        currentCategoryTitle = uiState.currentCategoryTitle,
        currentSuperCategory = uiState.currentSuperCategory,
        currentSuperCategories = uiState.currentSuperCategories,
        currentSubCategories = uiState.currentSubCategories,
        currentFlag = uiState.currentFlag,
        userGuess = viewModel.userGuess,
        onUserGuessChange = { viewModel.updateUserGuess(it) },
        isGuessCorrect = uiState.isGuessedFlagCorrect,
        isGuessCorrectEvent = uiState.isGuessedFlagCorrectEvent,
        isGuessWrong = uiState.isGuessedFlagWrong,
        isGuessWrongEvent = uiState.isGuessedFlagWrongEvent,
        isShowAnswer = uiState.isShowAnswer,
        isScoreDetails = uiState.isScoreDetails,
        currentGameFlags = uiState.currentFlags,
        endGameGuessedFlags = uiState.endGameGuessedFlags,
        endGameSkippedFlags = uiState.endGameSkippedFlags,
        endGameShownFlags = uiState.endGameShownFlags,
        endGameTime = uiState.timer,
        onKeyboardDoneAction = { viewModel.checkUserGuess() },
        onSubmit = { viewModel.checkUserGuess() },
        onSkip = { viewModel.skipFlag(isAnswerShown = uiState.isShowAnswer) },
        onShowAnswer = { viewModel.showAnswer() },
        onCloseScoreDetails = { viewModel.scoreDetails(newState = false) },
        onEndGame = { viewModel.endGame() },
        onNavigateUp = onNavigateUp,
        onFullscreen = { isLandscape ->
            onFullscreen(uiState.currentFlag.id, isLandscape, !uiState.isShowAnswer)
        },
        onCategorySelect = { newSuperCategory, newSubCategory ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
        },
        onCategoryMultiSelect = { selectSuperCategory, selectSubCategory ->
            viewModel.updateCurrentCategories(selectSuperCategory, selectSubCategory)
        },
    )
}


@Composable
private fun GameScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    isWideScreen: Boolean,
    timer: String,
    totalFlagCount: Int,
    correctGuessCount: Int,
    shownAnswerCount: Int,
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    currentSuperCategories: List<FlagSuperCategory>?,
    currentSubCategories: List<FlagCategory>?,
    currentFlag: FlagResources,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    isShowAnswer: Boolean,
    isScoreDetails: Boolean,
    currentGameFlags: List<FlagResources>,
    endGameGuessedFlags: List<FlagResources>,
    endGameSkippedFlags: List<FlagResources>,
    endGameShownFlags: List<FlagResources>,
    endGameTime: Int,
    onKeyboardDoneAction: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onShowAnswer: () -> Unit,
    onCloseScoreDetails: () -> Unit,
    onEndGame: () -> Unit,
    onNavigateUp: () -> Unit,
    onFullscreen: (Boolean) -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
    onCategoryMultiSelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion and tracks button height */
    var buttonExpanded by rememberSaveable { mutableStateOf(value = false) }
    var buttonHeight by remember { mutableStateOf(value = 0.dp) }

    /* So that FilterFlagsButton can access Scaffold() padding */
    var scaffoldTopPadding by remember { mutableStateOf(value = 0.dp) }
    var scaffoldBottomPadding by remember { mutableStateOf(value = 0.dp) }

    var isFlagWide by rememberSaveable { mutableStateOf(value = true) }

    /* Minimize filter menu when keyboard input */
    LaunchedEffect(userGuess) {
        if (userGuess.isNotEmpty() && buttonExpanded) {
            buttonExpanded = false
        }
    }


    /* Scaffold within box so that FilterFlagsButton & it's associated surface can overlay it */
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                GeneralTopBar(
                    currentScreen = currentScreen,
                    canNavigateBack = canNavigateBack,
                    timer = timer,
                    onNavigateUp = onNavigateUp,
                    onNavigateDetails = {},
                    onAction = {},
                )
            }
        ) { scaffoldPadding ->
            scaffoldTopPadding = scaffoldPadding.calculateTopPadding()
            scaffoldBottomPadding = scaffoldPadding.calculateBottomPadding()

            GameContent(
                modifier = Modifier.padding(scaffoldPadding),
                isWideScreen = isWideScreen,
                filterButtonHeight = buttonHeight,
                totalFlagCount = totalFlagCount,
                correctGuessCount = correctGuessCount,
                shownAnswerCount = shownAnswerCount,
                currentFlag = currentFlag,
                userGuess = userGuess,
                onUserGuessChange = onUserGuessChange,
                isGuessCorrect = isGuessCorrect,
                isGuessCorrectEvent = isGuessCorrectEvent,
                isGuessWrong = isGuessWrong,
                isGuessWrongEvent = isGuessWrongEvent,
                isShowAnswer = isShowAnswer,
                onKeyboardDoneAction = onKeyboardDoneAction,
                onSubmit = onSubmit,
                onSkip = onSkip,
                onShowAnswer = onShowAnswer,
                onEndGame = onEndGame,
                onImageWide = { isFlagWide = it },
                onFullscreen = { onFullscreen(isFlagWide) },
            )
        }

        /* Surface to receive taps when FilterFlagsButton is expanded, to collapse it */
        AnimatedVisibility(
            visible = buttonExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
        ) {
            Scrim(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                onAction = { buttonExpanded = !buttonExpanded }
            )
        }


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        FilterFlagsButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = scaffoldTopPadding,
                    bottom = scaffoldBottomPadding,
                    start = Dimens.marginHorizontal16,
                    end = Dimens.marginHorizontal16,
                ),
            screen = currentScreen,
            onButtonHeightChange = { buttonHeight = it },
            buttonExpanded = buttonExpanded,
            onButtonExpand = { buttonExpanded = !buttonExpanded },
            currentCategoryTitle = currentCategoryTitle,
            currentSuperCategory = currentSuperCategory,
            currentSuperCategories = currentSuperCategories,
            currentSubCategories = currentSubCategories,
            onCategorySelect = onCategorySelect,
            onCategoryMultiSelect = onCategoryMultiSelect,
        )

        /* For displaying verbose score details */
        ScoreDetails(
            visible = isScoreDetails,
            insetsPadding = WindowInsets.systemBars.asPaddingValues(),
            gameFlags = currentGameFlags,
            guessedFlags = endGameGuessedFlags,
            skippedFlags = endGameSkippedFlags,
            shownFlags = endGameShownFlags,
            timeElapsed = endGameTime,
            onClose = {
                onCloseScoreDetails()
                onEndGame()
            }
        )
    }
}


@Composable
private fun GameContent(
    modifier: Modifier = Modifier,
    isWideScreen: Boolean,
    filterButtonHeight: Dp,
    totalFlagCount: Int,
    correctGuessCount: Int,
    shownAnswerCount: Int,
    currentFlag: FlagResources,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    isShowAnswer: Boolean,
    onKeyboardDoneAction: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
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

    /* Manage show answer button */
    var showAnswerButtonState by rememberSaveable { mutableIntStateOf(value = 0) }
    val isDarkTheme by rememberUpdatedState(newValue = isSystemInDarkTheme())
    val showAnswerColor = when (showAnswerButtonState) {
        1 -> MaterialTheme.colorScheme.error
        else -> if (isDarkTheme) successDark else successLight
    }
    val showAnswerText = when (showAnswerButtonState) {
        0 -> stringResource(R.string.game_button_show)
        1 -> stringResource(R.string.game_button_show_confirm)
        else -> stringResource(R.string.game_button_shown)
    }
    /* Reset showAnswerButtonState when isShowAnswer is reset */
    LaunchedEffect(isShowAnswer) {
        if (!isShowAnswer) showAnswerButtonState = 0
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
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(filterButtonHeight / 2 + aspectRatioTopPadding)
        )

        GameCard(
            isDarkTheme = isDarkTheme,
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
            shownAnswerCount = shownAnswerCount,
            currentFlag = currentFlag,
            userGuess = userGuess,
            onUserGuessChange = onUserGuessChange,
            isGuessCorrect = isGuessCorrect,
            isGuessCorrectEvent = isGuessCorrectEvent,
            isGuessWrong = isGuessWrong,
            isGuessWrongEvent = isGuessWrongEvent,
            isShowAnswer = isShowAnswer,
            onKeyboardDoneAction = onKeyboardDoneAction,
            onEndGame = onEndGame,
            onImageWide = onImageWide,
            onFullscreen = onFullscreen,
        )
        /* Something makes Submit button's top padding less than Skip button's  */
        Spacer(modifier = Modifier.height(2.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
            enabled = showAnswerButtonState < 2,
        ) {
            Text(text = stringResource(R.string.game_button_submit))
        }

        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
        ) {
            Text(text = stringResource(R.string.game_button_skip))
        }

        OutlinedButton(
            onClick = {
                showAnswerButtonState++
                if (showAnswerButtonState > 1) onShowAnswer()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.medium16),
            enabled = showAnswerButtonState < 2,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = showAnswerColor),
        ) {
            Text(text = showAnswerText)
        }
    }
}


@Composable
private fun GameCard(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
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
    shownAnswerCount: Int,
    currentFlag: FlagResources,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    isShowAnswer: Boolean,
    onKeyboardDoneAction: () -> Unit,
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

    val labelDefault = stringResource(R.string.game_guess_field_label)
    val labelSuccess = stringResource(R.string.game_guess_field_label_success)
    val labelError = stringResource(R.string.game_guess_field_label_error)
    var labelState by remember { mutableStateOf(value = labelDefault) }


    /* LaunchedEffects for triggering animations of above variables */
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

    /* Referencing currentFlag.flagOf directly in Text() shows next flagOf for 1 frame after skip */
    @StringRes var correctAnswer by rememberSaveable {
        mutableIntStateOf(value = R.string.game_button_show)
    }
    LaunchedEffect(isShowAnswer) {
        if (isShowAnswer) correctAnswer = currentFlag.flagOf
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
                /* Flag counters */
                Row {
                    Text(
                        text = "${correctGuessCount + shownAnswerCount}/$totalFlagCount",
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    if (shownAnswerCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = "$shownAnswerCount",
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
                }

                /* Finish game button */
                TextButton(
                    onClick = onEndGame,
                    modifier = Modifier.height(Dimens.extraLarge32),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(
                        start = Dimens.small10,
                        bottom = Dimens.extraSmall6,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.end_game),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            /* Flag & show answer content */
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = { isFullScreenButton = !isFullScreenButton },
                        onDoubleClick = onFullscreen,
                    ),
                contentAlignment = Alignment.BottomEnd,
            ) {
                if (totalFlagCount != 0) {
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

                                /* For fullscreen orientation */
                                onImageWide(size.width > size.height)
                            }
                            /* Concatenate height after onSizeChanged */
                            .then(cardImageHeightModifier)
                            /* Force aspect ratio to keep game card shape consistent */
                            .aspectRatio(ratio = 3f / 2f),
                        painter = painterResource(currentFlag.image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                    )

                    /* Show answer content */
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isShowAnswer,
                        modifier = Modifier.matchParentSize(),
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = Timing.SHOW_ANSWER)
                        ),
                        exit = ExitTransition.None,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(correctAnswer),
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
                        isGame = true,
                    )
                }
            }

            /* User guess field */
            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.small10),
                enabled = !isShowAnswer,
                label = {
                    Text(text = labelState)
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDoneAction() },
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


/* End game popup dialog showing final score. With buttons: Leave game, Share score, Play again */
@Composable
private fun GameOverDialog(
    modifier: Modifier = Modifier,
    finalScore: Int,
    maxScore: Int,
    gameMode: String,
    onDetails: () -> Unit,
    onShare: (String) -> Unit,
    onPlayAgain: () -> Unit,
) {
    val scoreMessage = when (finalScore) {
        maxScore -> stringResource(R.string.game_over_text_max_score)
        0 -> stringResource(R.string.game_over_text_min_score)
        else -> stringResource(R.string.game_over_text, finalScore, maxScore)
    }

    val shareScoreMessage = stringResource(
        R.string.game_over_share_text, finalScore, maxScore, gameMode
    )

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = stringResource(R.string.game_over_title)) },
        text = { Text(text = scoreMessage) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDetails,
            ) {
                Text(text = stringResource(R.string.game_over_details_button))
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = { onShare(shareScoreMessage) }) {
                    Text(text = stringResource(R.string.game_over_share_button))
                }
                TextButton(onClick = onPlayAgain) {
                    Text(text = stringResource(R.string.game_over_play_again_button))
                }
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