package dev.aftly.flags.ui.screen.flag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.component.RelatedFlagsButton
import dev.aftly.flags.ui.component.Scrim
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings


@Composable
fun FlagScreen(
    viewModel: FlagViewModel = viewModel(),
    navController: NavHostController,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onFullscreen: (Int, List<Int>, Boolean) -> Unit,
    onNavigateError: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("flag")
            ?.observe(lifecycleOwner) { flagId ->
                if (flagId != null) {
                    viewModel.updateFlag(flagId = flagId, flag = null)
                }
            }
    }

    val uiState by viewModel.uiState.collectAsState()
    if (uiState.currentFlag == DataSource.nullFlag) onNavigateError()

    /* Update flag description string when language configuration changes */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    LaunchedEffect(locale) { viewModel.updateDescriptionString() }

    FlagScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        currentFlag = uiState.currentFlag,
        relatedFlags = uiState.relatedFlags,
        description = uiState.description,
        boldWordPositions = uiState.descriptionBoldWordIndexes,
        fontScale = configuration.fontScale,
        onNewFlag = { viewModel.updateFlag(flagId = null, flag = it) },
        onFullscreen = { isLandscape ->
            val flags = when (uiState.currentFlag) {
                uiState.initialFlag -> uiState.flagsFromList
                else -> listOf(uiState.currentFlag.id)
            }
            onFullscreen(
                uiState.currentFlag.id,
                flags,
                isLandscape
            )
        }
    )
}


@Composable
private fun FlagScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentFlag: FlagResources,
    relatedFlags: List<FlagResources>,
    description: List<String>,
    boldWordPositions: List<Int>,
    fontScale: Float,
    onNewFlag: (FlagResources) -> Unit,
    onFullscreen: (Boolean) -> Unit,
) {
    val isRelatedFlagsButton = relatedFlags.size > 1

    /* Controls FilterFlagsButton menu expansion amd tracks current button height
     * Also for FilterFlagsButton to access Scaffold() padding */
    var buttonExpanded by rememberSaveable { mutableStateOf(value = false) }
    var buttonHeight by remember { mutableStateOf(0.dp) }
    var scaffoldTopPadding by remember { mutableStateOf(value = 0.dp) }
    var scaffoldBottomPadding by remember { mutableStateOf(value = 0.dp) }

    var isFlagWide by rememberSaveable { mutableStateOf(value = true) }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                StaticTopAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = canNavigateBack,
                    onNavigateUp = navigateUp,
                    onNavigateDetails = {},
                )
            }
        ) { scaffoldPadding ->
            scaffoldTopPadding = scaffoldPadding.calculateTopPadding()
            scaffoldBottomPadding = scaffoldPadding.calculateBottomPadding()

            FlagContent(
                modifier = Modifier.padding(scaffoldPadding),
                relatedFlagsButtonHeight = when (isRelatedFlagsButton) {
                    true -> buttonHeight
                    else -> 0.dp
                },
                flag = currentFlag,
                description = description,
                boldWordPositions = boldWordPositions,
                onImageWide = { isFlagWide = it },
                onFullscreen = { onFullscreen(isFlagWide) },
            )
        }

        if (isRelatedFlagsButton) {

            /* Surface to receive taps when FilterFlagsButton is expanded, to collapse it */
            AnimatedVisibility(
                visible = buttonExpanded,
                enter = fadeIn(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
                exit = fadeOut(animationSpec = tween(durationMillis = Timings.MENU_EXPAND)),
            ) {
                Scrim(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                    onAction = { buttonExpanded = !buttonExpanded }
                )
            }

            RelatedFlagsButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = scaffoldTopPadding,
                        bottom = scaffoldBottomPadding,
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16,
                    ),
                buttonExpanded = buttonExpanded,
                onButtonExpand = { buttonExpanded = !buttonExpanded },
                onButtonHeightChange = { buttonHeight = it },
                fontScale = fontScale,
                currentFlag = currentFlag,
                relatedFlags = relatedFlags,
                onFlagSelect = { newFlag ->
                    if (newFlag != currentFlag) {
                        buttonExpanded = false
                        onNewFlag(newFlag)
                    }
                },
            )
        }
    }
}


@Composable
private fun FlagContent(
    modifier: Modifier = Modifier,
    relatedFlagsButtonHeight: Dp,
    flag: FlagResources,
    description: List<String>,
    boldWordPositions: List<Int>,
    onImageWide: (Boolean) -> Unit,
    onFullscreen: () -> Unit,
) {
    /* Properties for if image height greater than column height, eg. in landscape orientation,
     * make image height modifier value of column height, else value of image height */
    var columnHeight by remember { mutableIntStateOf(value = 0) }
    var imageHeight by remember { mutableIntStateOf(value = 0) }
    var imageHeightModifier by remember { mutableStateOf<Modifier>(value = Modifier) }
    val density = LocalDensity.current.density

    var isFullScreenButton by rememberSaveable { mutableStateOf(value = false) }


    /* Flag Content */
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = relatedFlagsButtonHeight,
                start = Dimens.marginHorizontal16,
                end = Dimens.marginHorizontal16
            )
            .onSizeChanged { size ->
                columnHeight = size.height

                /* Set image height modifier */
                imageHeightModifier = if (columnHeight < imageHeight) {
                    Modifier.height(height = (columnHeight / density).dp)
                } else {
                    Modifier.height(height = (imageHeight / density).dp)
                }
            }
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /* Determine name title properties */
        val prefix = when (flag.isFlagOfOfficialThe) {
            true -> stringResource(R.string.string_the_capitalized) +
                    stringResource(R.string.string_whitespace)
            false -> null
        }
        val name = stringResource(flag.flagOfOfficial)
        val nameLength = prefix?.let { it.length + name.length } ?: name.length

        val nameStyle = if (nameLength < 36) {
            if (nameLength <= 12) MaterialTheme.typography.displayLarge
            else MaterialTheme.typography.displayMedium
        } else {
            MaterialTheme.typography.displaySmall
        }

        /* Build annotated string from name properties, making name bold */
        val annotatedName = buildAnnotatedString {
            if (prefix != null) append(text = prefix)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(text = name) }
        }

        /* Build annotated string from description string list, making flag names bold */
        val annotatedDescription = buildAnnotatedString {
            for ((index, string) in description.withIndex()) {
                if (index in boldWordPositions) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = string)
                    }
                } else append(text = string)
            }
        }


        /* Flag official name */
        Text(
            text = annotatedName,
            style = nameStyle,
            textAlign = TextAlign.Center,
        )

        /* Image and fullscreen button contents */
        Box(
            modifier = Modifier
                .padding(vertical = Dimens.extraLarge32)
                .combinedClickable(
                    onClick = { isFullScreenButton = !isFullScreenButton },
                    onDoubleClick = onFullscreen,
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(Dimens.extraSmall4),
            ) {
                Image(
                    painter = painterResource(flag.image),
                    modifier = Modifier.fillMaxWidth(),
                        /*
                        .onSizeChanged { size ->
                            imageHeight = size.height

                            /* Set image height modifier */
                            imageHeightModifier = if (columnHeight < imageHeight) {
                                Modifier.height(height = (columnHeight / density).dp)
                            } else {
                                Modifier.height(height = (imageHeight / density).dp)
                            }

                            /* For fullscreen orientation */
                            onImageWide(size.width > size.height)
                        }
                        .then(imageHeightModifier), /* concatenate height mod after onSizeChanged */
                         */
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                )
            }

            FullscreenButton(
                visible = isFullScreenButton,
                onInvisible = { isFullScreenButton = false },
                onFullScreen = onFullscreen,
            )
        }

        /* Natural language description of flag entity from it's categories */
        Text(
            text = annotatedDescription,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            style = TextStyle(fontStyle = FontStyle.Italic),
        )

        Spacer(modifier = Modifier.height(Dimens.bottomSpacer80))
    }
}


/* Preview screen in Android Studio */
/*
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun FlagScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        FlagScreen(
            navArgFlagId = "russia",
            currentScreen = Screen.Flag,
            canNavigateBack = true,
            navigateUp = { },
            onNavigateError = { }
        )
    }
}
 */