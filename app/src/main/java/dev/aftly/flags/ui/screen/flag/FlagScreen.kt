package dev.aftly.flags.ui.screen.flag

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.component.RelatedFlagsButton
import dev.aftly.flags.ui.component.RelatedFlagsMenu
import dev.aftly.flags.ui.component.openWebLink
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.util.LocalDarkTheme
import dev.aftly.flags.ui.util.SystemUiController


@Composable
fun FlagScreen(
    viewModel: FlagViewModel = viewModel(),
    currentBackStackEntry: NavBackStackEntry?,
    onNavigateBack: (FlagResources) -> Unit,
    onFullscreen: (FlagResources, List<Int>, Boolean) -> Unit,
    onNavigateError: () -> Unit,
) {
    /* Expose screen and backStack state */
    val uiState by viewModel.uiState.collectAsState()

    /* Handle navigation null case */
    if (uiState.flag == DataSource.nullFlag) onNavigateError()

    /* Manage system bars and flag state after returning from FullScreen */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(view, window) }
    val isDarkTheme = LocalDarkTheme.current

    /* For nav back from fullscreen */
    LaunchedEffect(key1 = Unit) {
        systemUiController.setLightStatusBar(light = !isDarkTheme)
        systemUiController.setSystemBars(visible = true)
    }

    LaunchedEffect(key1 = currentBackStackEntry) {
        currentBackStackEntry?.savedStateHandle?.get<Int>("flagId")?.let { flagId ->
            viewModel.updateFlag(flagId)
        }
    }

    /* Update flag description string when language configuration changes */
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    //LaunchedEffect(locale) { viewModel.updateDescriptionString(flag = uiState.currentFlag) }


    FlagScreen(
        uiState = uiState,
        onFlagSave = { viewModel.updateSavedFlag() },
        onRelatedFlag = { viewModel.updateFlagRelated(flag = it) },
        onFullscreen = { isLandscape ->
            val flagIds = viewModel.getFlagIds()
            onFullscreen(uiState.flag, flagIds, isLandscape)
        },
        onNavigateBack = {
            val flag =
                if (uiState.isRelatedFlagNavigation) uiState.initRelatedFlag else uiState.flag
            onNavigateBack(flag)
        },
    )
}


@Composable
private fun FlagScreen(
    modifier: Modifier = Modifier,
    uiState: FlagUiState,
    onFlagSave: () -> Unit,
    onRelatedFlag: (FlagResources) -> Unit,
    onFullscreen: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val isRelatedFlagsButton = uiState.relatedFlags.size > 1

    /* Controls FilterFlagsButton menu expansion amd tracks current button height
     * Also for FilterFlagsButton to access Scaffold() padding */
    var isButtonExpanded by rememberSaveable { mutableStateOf(value = false) }
    var scaffoldPaddingValues by remember { mutableStateOf(value = PaddingValues()) }
    var buttonOffset by remember { mutableStateOf(value = Offset(x = 0f, y = 0f)) }
    var buttonWidth by remember { mutableIntStateOf(value = 0) }

    var isFlagWide by rememberSaveable { mutableStateOf(value = true) }

    /* To ensure same custom back behaviour as in-app back functionality */
    BackHandler { onNavigateBack() }


    Box(modifier = Modifier.fillMaxSize()) {
        /* ------------------- START OF SCAFFOLD ------------------- */
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                FlagTopBar(
                    isFlagSaved = uiState.savedFlag != null,
                    onFlagSave = onFlagSave,
                    isRelatedFlagsButton = isRelatedFlagsButton,
                    isButtonExpanded = isButtonExpanded,
                    onButtonExpand = { isButtonExpanded = !isButtonExpanded },
                    onButtonPosition = { buttonOffset = it },
                    onButtonWidth = { buttonWidth = it },
                    onNavigateBack = onNavigateBack,
                )
            }
        ) { scaffoldPadding ->
            scaffoldPaddingValues = scaffoldPadding

            FlagContent(
                modifier = Modifier.padding(scaffoldPadding),
                flag = uiState.flag,
                description = uiState.description,
                boldWordPositions = uiState.descriptionBoldWordIndexes,
                onImageWide = { isFlagWide = it },
                onFullscreen = { onFullscreen(isFlagWide) },
            )
        }
        /* ------------------- END OF SCAFFOLD ------------------- */


        if (isRelatedFlagsButton) {
            RelatedFlagsMenu(
                modifier = Modifier.fillMaxSize(),
                scaffoldPadding = scaffoldPaddingValues,
                menuButtonOffset = buttonOffset,
                menuButtonWidth = buttonWidth,
                isExpanded = isButtonExpanded,
                onExpand = { isButtonExpanded = !isButtonExpanded },
                currentFlag = uiState.flag,
                relatedFlags = uiState.relatedFlags,
                onFlagSelect = { newFlag ->
                    if (newFlag != uiState.flag) {
                        isButtonExpanded = false
                        onRelatedFlag(newFlag)
                    }
                },
            )
        }
    }
}


@Composable
private fun FlagContent(
    modifier: Modifier = Modifier,
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
        verticalArrangement = Arrangement.SpaceBetween,
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

        val wikiLink =
            stringResource(R.string.wikipedia_site_prefix) + stringResource(flag.wikipediaUrlPath)


        /* Ui content */
        Spacer(modifier = Modifier.height(0.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .onSizeChanged { size ->
                                /* For fullscreen orientation */
                                onImageWide(size.width > size.height)

                                /*
                                imageHeight = size.height

                                /* Set image height modifier */
                                imageHeightModifier = if (columnHeight < imageHeight) {
                                    Modifier.height(height = (columnHeight / density).dp)
                                } else {
                                    Modifier.height(height = (imageHeight / density).dp)
                                }
                                 */
                            },
                        //.then(imageHeightModifier) /* concatenate height mod after onSizeChanged */
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

            Spacer(modifier = Modifier.height(20.dp))
        }


        WikipediaButton(
            modifier = Modifier.fillMaxWidth(),
            wikiLink = wikiLink,
        )
    }
}


@Composable
fun WikipediaButton(
    modifier: Modifier = Modifier,
    wikiLink: String,
) {
    val context = LocalContext.current

    Button(
        onClick = { openWebLink(context, wikiLink) },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.wikipedia_button),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.OpenInNew,
                contentDescription = null,
                modifier = Modifier.padding(start = Dimens.small8)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlagTopBar(
    modifier: Modifier = Modifier,
    isFlagSaved: Boolean,
    onFlagSave: () -> Unit,
    isRelatedFlagsButton: Boolean,
    isButtonExpanded: Boolean,
    onButtonExpand: () -> Unit,
    onButtonPosition: (Offset) -> Unit,
    onButtonWidth: (Int) -> Unit,
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        title = {
            if (isRelatedFlagsButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    RelatedFlagsButton(
                        menuExpanded = isButtonExpanded,
                        onMenuExpand = onButtonExpand,
                        onButtonPosition = onButtonPosition,
                        onButtonWidth = onButtonWidth,
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            IconButton(onClick = onFlagSave) {
                Icon(
                    imageVector = when (isFlagSaved) {
                        false -> Icons.Default.Add
                        true -> Icons.Default.Check
                    },
                    contentDescription = when (isFlagSaved) {
                        false -> stringResource(R.string.saved_flags_add)
                        true -> stringResource(R.string.saved_flags_remove)
                    },
                )
            }
        },
    )
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