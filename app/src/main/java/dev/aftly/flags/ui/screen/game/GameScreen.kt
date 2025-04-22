package dev.aftly.flags.ui.screen.game

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.FilterFlagsButton
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.component.shareText
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.FlagsTheme
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.theme.successLight
import kotlinx.coroutines.delay


/*
 * Notes/ideas:
 * Flag + "???" + "Running counter" in card
 * Guess field + Submit button + Skip button under card
 * Hint button: Shows flag name? or shows categories?
 * Logic for skip. Skip adds flags to (mutable)list which returns, in order, after non-skipped flags
 * Score/correct number of guessed flags
 * End/Reset/Finish game button (text button) next to hint button? underneath skip button?
 * End/Finish game shows pop up? With share activity like unscrambled?
 */

// TODO: Revert to isError on OutlinedTextField to solve animationColor issues?

// TODO: Tapping off keyboard, minimises keyboard

// TODO: Add incorrect guess counter
// TODO: For flags not guessed in endgame dialogue have option to show flags not guessed?

// TODO: Add time/timer functions/modes to Game

// TODO: When persistent data: Make scoreboard history with game mode, score, incorrect guess count, time and date.
// TODO: When persistent data: Make feature to import other user's scores. Export feature asks for username? Or import feature does. Or both?
// TODO: When persistent data: In settings, add strict spelling mode where guessed strings are not normalized

// TODO: Sort out scaling for GameCard on screens with different/wide aspect ratios
// TODO idea: calculate aspect ratio of screen dynamically with local context, for portrait aspect ratio have layout X, for landscape have layout Y? (Using conditional)

// TODO: Make keyboard action button trigger onSubmit()
// TODO: add "correct guess" message to user

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    /* When language configuration changes, update strings in uiState */
    val currentLocale = context.resources.configuration.locales[0]
    LaunchedEffect(currentLocale) { viewModel.setFlagStrings() }

    // When the game is over show game over pop-up
    if (uiState.isGameOver) {
        GameOverDialog(
            finalScore = uiState.correctGuessCount,
            outOf = uiState.totalFlagCount,
            gameMode = stringResource(uiState.currentSuperCategory.title),
            onExit = {
                viewModel.endGame(isGameOver = false)
                onNavigateUp()
            },
            onShare = { text ->
                shareText(
                    context = context,
                    subject = R.string.flag_game_over_share_subject,
                    textToShare = text,
                )
            },
            onPlayAgain = { viewModel.resetGame() },
        )
    }

    GameScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        totalFlagCount = uiState.totalFlagCount,
        correctGuessCount = uiState.correctGuessCount,
        currentCategoryTitle = uiState.currentCategoryTitle,
        currentSuperCategory = uiState.currentSuperCategory,
        currentFlag = uiState.currentFlag,
        //currentFlagStrings = uiState.currentFlagStrings,
        userGuess = viewModel.userGuess,
        onUserGuessChange = { viewModel.updateUserGuess(it) },
        isGuessCorrect = uiState.isGuessedFlagCorrect,
        isGuessCorrectEvent = uiState.isGuessedFlagCorrectEvent,
        isGuessWrong = uiState.isGuessedFlagWrong,
        isGuessWrongEvent = uiState.isGuessedFlagWrongEvent,
        onKeyboardDoneAction = { viewModel.checkUserGuess() },
        onSubmit = { viewModel.checkUserGuess() },
        onSkip = { viewModel.skipFlag() },
        onEndGame = { viewModel.endGame() },
        onNavigateUp = onNavigateUp,
        onCategorySelect = { newSuperCategory: FlagSuperCategory?, newSubCategory: FlagCategory? ->
            viewModel.updateCurrentCategory(newSuperCategory, newSubCategory)
        },
    )
}


@Composable
private fun GameScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    totalFlagCount: Int,
    correctGuessCount: Int,
    @StringRes currentCategoryTitle: Int,
    currentSuperCategory: FlagSuperCategory,
    currentFlag: FlagResources,
    //currentFlagStrings: List<String>,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    onKeyboardDoneAction: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onEndGame: () -> Unit,
    onNavigateUp: () -> Unit,
    onCategorySelect: (FlagSuperCategory?, FlagCategory?) -> Unit,
) {
    /* Controls FilterFlagsButton menu expansion */
    var expanded by rememberSaveable { mutableStateOf(value = false) }

    /* So that FilterFlagsButton can access Scaffold() padding */
    var scaffoldTopPadding by remember { mutableStateOf(value = 0.dp) }
    var scaffoldBottomPadding by remember { mutableStateOf(value = 0.dp) }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                StaticTopAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = canNavigateBack,
                    onNavigateUp = onNavigateUp,
                    onAction = { },
                )
            }
        ) { scaffoldPadding ->
            scaffoldTopPadding = scaffoldPadding.calculateTopPadding()
            scaffoldBottomPadding = scaffoldPadding.calculateBottomPadding()

            GameContent(
                modifier = Modifier.padding(scaffoldPadding),
                totalFlagCount = totalFlagCount,
                correctGuessCount = correctGuessCount,
                currentFlag = currentFlag,
                //currentFlagStrings = currentFlagStrings,
                userGuess = userGuess,
                onUserGuessChange = onUserGuessChange,
                isGuessCorrect = isGuessCorrect,
                isGuessCorrectEvent = isGuessCorrectEvent,
                isGuessWrong = isGuessWrong,
                isGuessWrongEvent = isGuessWrongEvent,
                onKeyboardDoneAction = onKeyboardDoneAction,
                onSubmit = onSubmit,
                onSkip = onSkip,
                onEndGame = onEndGame,
            )
        }


        /* Surface to receive taps when FilterFlagsButton is expanded, to collapse it */
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(durationMillis = Timings.menuExpand)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timings.menuExpand)),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
                    .clickable { expanded = !expanded },
                color = Color.Black.copy(alpha = 0.35f),
            ) { }
        }


        /* Custom quasi-DropdownMenu elevated above screen content with animated nested menus for
         * selecting super or sub category to filter flags by */
        FilterFlagsButton(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = scaffoldTopPadding,
                    bottom = scaffoldBottomPadding,
                    start = Dimens.marginHorizontal24,
                    end = Dimens.marginHorizontal24,
                ),
            buttonExpanded = expanded,
            onButtonExpand = { expanded = !expanded },
            currentCategoryTitle = currentCategoryTitle,
            currentSuperCategory = currentSuperCategory,
            onCategorySelect = onCategorySelect,
        )
    }
}


@Composable
private fun GameContent(
    modifier: Modifier = Modifier,
    totalFlagCount: Int,
    correctGuessCount: Int,
    currentFlag: FlagResources,
    //currentFlagStrings: List<String>,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    onKeyboardDoneAction: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onEndGame: () -> Unit,
) {
    /* Center arranged column with Game content */
    Column(
        modifier = modifier.fillMaxSize()
            .padding(horizontal = Dimens.marginHorizontal24)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GameCard(
            totalFlagCount = totalFlagCount,
            correctGuessCount = correctGuessCount,
            currentFlag = currentFlag,
            userGuess = userGuess,
            onUserGuessChange = onUserGuessChange,
            isGuessCorrect = isGuessCorrect,
            isGuessCorrectEvent = isGuessCorrectEvent,
            isGuessWrong = isGuessWrong,
            isGuessWrongEvent = isGuessWrongEvent,
            onKeyboardDoneAction = onKeyboardDoneAction,
            onEndGame = onEndGame,
        )

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
                .padding(top = Dimens.extraLarge32),
        ) {
            Text(text = "Submit")
        }

        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
                .padding(top = Dimens.medium16),
        ) {
            Text(text = "Skip")
        }

        Spacer(modifier = Modifier.height(Dimens.bottomSpacer30))
    }
}


@Composable
private fun GameCard(
    modifier: Modifier = Modifier,
    totalFlagCount: Int,
    correctGuessCount: Int,
    currentFlag: FlagResources,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    isGuessCorrect: Boolean,
    isGuessCorrectEvent: Boolean,
    isGuessWrong: Boolean,
    isGuessWrongEvent: Boolean,
    onKeyboardDoneAction: () -> Unit,
    onEndGame: () -> Unit,
) {
    /* Variables for animated colors */
    val focusedColorDefault = OutlinedTextFieldDefaults.colors().focusedIndicatorColor
    val focusedContainerColor: Color = MaterialTheme.colorScheme.surface

    val animationSpecEnter = tween<Color>(durationMillis = 0)
    val animationSpecExit = tween<Color>(durationMillis = Timings.guessStateExit)
    var animationSpecSuccess by remember { mutableStateOf(value = animationSpecEnter) }
    var animationSpecError by remember { mutableStateOf(value = animationSpecEnter) }

    var isSuccessColor by remember { mutableStateOf(value = false) }
    var isErrorColor by remember { mutableStateOf(value = false) }

    val animatedSuccessColor by animateColorAsState(
        targetValue = if (isSuccessColor) successLight
        else focusedColorDefault,
        animationSpec = animationSpecSuccess
    )
    val animatedErrorColor by animateColorAsState(
        targetValue = if (isErrorColor) MaterialTheme.colorScheme.error
        else focusedColorDefault,
        animationSpec = animationSpecError,
    )

    /* Variables for animated label */
    val animationSpecLabelGuess = tween<Float>(durationMillis = 0)
    val animationSpecLabelDefault = tween<Float>(durationMillis = Timings.guessStateExit)

    val labelDefault = stringResource(R.string.flag_game_guess_field_label)
    val labelSuccess = stringResource(R.string.flag_game_guess_field_label_success)
    val labelError = stringResource(R.string.flag_game_guess_field_label_error)
    var labelState by remember { mutableStateOf(value = labelDefault) }


    /* LaunchedEffects for triggering animations of above variables */
    LaunchedEffect(isGuessCorrectEvent) {
        if (isGuessCorrect) {
            animationSpecSuccess = animationSpecEnter
            isSuccessColor = true
            labelState = labelSuccess
            delay(timeMillis = Timings.guessStateDuration.toLong())

            animationSpecSuccess = animationSpecExit
            isSuccessColor = false
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelError) labelError else labelDefault
        }
    }
    LaunchedEffect(isGuessWrongEvent) {
        if (isGuessWrong) {
            animationSpecError = animationSpecEnter
            isErrorColor = true
            labelState = labelError
            delay(timeMillis = Timings.guessStateDuration.toLong())

            animationSpecError = animationSpecExit
            isErrorColor = false
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelSuccess) labelSuccess else labelDefault
        }
    }


    /* Game Card content */
    Card(
        // Set width, affecting landscape orientation
        modifier = modifier.width(450.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(Dimens.medium16),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top row in card
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = Dimens.medium16),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$correctGuessCount/$totalFlagCount",
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = Dimens.extraSmall4, horizontal = Dimens.small10),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                )

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

            Image(
                // Force aspect ratio to keep game card shape consistent
                modifier = Modifier.aspectRatio(ratio = 3f / 2f),
                //painter = painterResource(DataSource.flagsMap.getValue("switzerland").image),
                //painter = painterResource(DataSource.flagsMap.getValue("vaticanCity").image),
                //painter = painterResource(DataSource.flagsMap.getValue("belgium").image),
                //painter = painterResource(DataSource.flagsMap.getValue("unitedStates").image),
                painter = painterResource(currentFlag.image),
                contentDescription = "flag of ${stringResource(currentFlag.flagOf)}",
                contentScale = ContentScale.Inside,
            )

            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChange,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = Dimens.small10),
                label = {
                    Crossfade(
                        targetState = labelState,
                        animationSpec = when (labelState) {
                            labelSuccess -> animationSpecLabelGuess
                            labelError -> animationSpecLabelGuess
                            else -> animationSpecLabelDefault
                        },
                    ) { Text(text = it) }
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
                    //unfocusedBorderColor = focusedColorDefault,
                    unfocusedContainerColor = focusedContainerColor,
                    focusedContainerColor = focusedContainerColor,
                    errorContainerColor = focusedContainerColor,
                    focusedBorderColor = animatedSuccessColor,
                    focusedLabelColor = animatedSuccessColor,
                    cursorColor = animatedSuccessColor,
                    errorBorderColor = animatedErrorColor,
                    errorCursorColor = animatedErrorColor,
                    errorLabelColor = animatedErrorColor,
                    //errorContainerColor = animatedErrorContainerColor,
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
    outOf: Int,
    gameMode: String,
    onExit: () -> Unit,
    onShare: (String) -> Unit,
    onPlayAgain: () -> Unit,
) {
    val scoreMessage = when (finalScore) {
        outOf -> stringResource(R.string.flag_game_over_text_max_score)
        0 -> stringResource(R.string.flag_game_over_text_min_score)
        else -> stringResource(R.string.flag_game_over_text, finalScore, outOf)
    }

    val shareScoreMessage = stringResource(
        R.string.flag_game_over_share_text, finalScore, outOf, gameMode
    )

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = stringResource(R.string.flag_game_over_title)) },
        text = { Text(text = scoreMessage) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onExit,
            ) {
                Text(text = stringResource(R.string.flag_game_over_exit_button))
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = { onShare(shareScoreMessage) }) {
                    Text(text = stringResource(R.string.flag_game_over_share_button))
                }
                TextButton(onClick = onPlayAgain) {
                    Text(text = stringResource(R.string.flag_game_over_play_again_button))
                }
            }
        },
    )
}


// Preview screen in Android Studio
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


/* Ad hoc tool to print contents of currentFlagStrings (for testing correctness due to updating
 * the state from the composable itself which can lead to some weirdness due to recomposition */
/*
currentFlagStrings.forEach { string ->
    Text(text = string, style = MaterialTheme.typography.bodySmall)
}
 */


/* Injecting the strings for the currentFlagStrings list state here because stringResource()
 * is a @Compose function */
// Having conditional here instead of in the viewModel function results in less computations
/*
if (uiState.currentFlagStrings.size < uiState.currentFlagStringResIds.size) {
    uiState.currentFlagStringResIds.forEach { stringResId ->
        viewModel.updateCurrentFlagStrings(stringResource(stringResId))
    }
}
 */