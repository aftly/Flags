package dev.aftly.flags.ui.screen.game

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
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
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.theme.successLight
import kotlinx.coroutines.delay


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

    /* Show pop-up when game over */
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


    /* Scaffold within box so that FilterFlagsButton & it's associated surface can overlay it */
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
            enter = fadeIn(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
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
    /* Properties for if card image height greater than content column height, eg. in landscape
     * orientation, make image height modifier value of column height, else value of image height
     * Also limits game card width in landscape orientation */
    var columnHeight by remember { mutableStateOf(value = 0.dp) }
    var imageHeight by remember { mutableStateOf(value = 0.dp) }
    var imageWidth by remember { mutableStateOf(value = 0.dp) }
    var imageHeightModifier by remember { mutableStateOf<Modifier>(value = Modifier) }
    var cardWidthModifier by remember { mutableStateOf<Modifier>(value = Modifier) }
    val density = LocalDensity.current.density
    val contentTopPadding = Dimens.filterButtonRowHeight30 / 2


    /* Center arranged column with Game content */
    Column(
        modifier = modifier.fillMaxSize()
            .padding(
                /* Top padding so that content scroll disappears into FilterFlagsButton */
                top = Dimens.filterButtonRowHeight30 / 2,
                start = Dimens.marginHorizontal24,
                end = Dimens.marginHorizontal24,
            )
            .onSizeChanged { size ->
                columnHeight = (size.height.toFloat() / density).dp - contentTopPadding

                /* Set image height modifier */
                if (columnHeight - contentTopPadding < imageHeight) {
                    imageHeightModifier = Modifier.height(height = columnHeight)
                    cardWidthModifier = Modifier.width(width = (imageWidth.value * 1.25).dp)
                } else {
                    imageHeightModifier = Modifier.height(height = imageHeight)
                    cardWidthModifier = Modifier.fillMaxWidth()
                }
            }.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /* Spacer to make content start below FilterFlags button */
        Spacer(modifier = Modifier.fillMaxWidth()
            .height(Dimens.filterButtonRowHeight30 / 2 + Dimens.small8)
        )

        GameCard(
            density = density,
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
    density: Float,
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
    val animationSpecExit = tween<Color>(durationMillis = Timings.GUESS_STATE_EXIT)
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

            delay(timeMillis = Timings.GUESS_STATE_DURATION.toLong())
            animationSpecSuccess = animationSpecExit
            isSuccessColor = false

            delay(timeMillis = Timings.GUESS_STATE_EXIT.toLong())
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelError) labelError else labelDefault
        }
    }
    LaunchedEffect(isGuessWrongEvent) {
        if (isGuessWrong) {
            animationSpecError = animationSpecEnter
            isErrorColor = true
            labelState = labelError

            delay(timeMillis = Timings.GUESS_STATE_DURATION.toLong())
            animationSpecError = animationSpecExit
            isErrorColor = false

            delay(timeMillis = Timings.GUESS_STATE_EXIT.toLong())
            /* If other LaunchedEffect is triggered before this one finishes, don't reset label */
            labelState = if (labelState == labelSuccess) labelSuccess else labelDefault
        }
    }


    /* Game Card content */
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = cardWidthModifier.padding(Dimens.medium16),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /* Top row in card */
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
                modifier = Modifier
                    .onSizeChanged { size ->
                        onCardImageHeightChange(
                            (size.height.toFloat() / density).dp
                        )
                        onCardImageWidthChange(
                            (size.width.toFloat() / density).dp
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
                    } /* concatenate height modifier after onSizeChanged */
                    .then(cardImageHeightModifier)
                    /* Force aspect ratio to keep game card shape consistent */
                    .aspectRatio(ratio = 3f / 2f),
                painter = painterResource(currentFlag.image),
                contentDescription = "flag of ${stringResource(currentFlag.flagOf)}",
                contentScale = ContentScale.Fit,
            )

            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChange,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = Dimens.small10),
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