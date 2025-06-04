package dev.aftly.flags.ui.screen.fullscreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ScreenLockLandscape
import androidx.compose.material.icons.filled.StayPrimaryLandscape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceDark
import dev.aftly.flags.ui.theme.surfaceLight
import dev.aftly.flags.ui.util.LocalOrientationController
import dev.aftly.flags.ui.util.OrientationController
import dev.aftly.flags.ui.util.SystemUiController
import dev.aftly.flags.ui.util.isOrientationLandscape
import dev.aftly.flags.ui.util.isOrientationPortrait
import kotlinx.coroutines.delay
import kotlin.math.abs


@Composable
fun FullScreen(
    viewModel: FullscreenViewModel = viewModel(),
    hideTitle: Boolean,
    isFlagWide: Boolean,
    onExitFullScreen: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val orientationController = LocalOrientationController.current
    val configuration = LocalConfiguration.current

    /* Initialise with correct orientation to ensure composition is delayed until end of process */
    val isInitOrientationLandscape = isOrientationLandscape(configuration = configuration)
    var isInit by rememberSaveable { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        if (isFlagWide && !isInitOrientationLandscape) {
            orientationController.setLandscapeOrientation()
        }
        isInit = true
    }

    if (isInit) {
        FullScreen(
            uiState = uiState,
            orientationController = orientationController,
            isInitOrientationLandscape = isInitOrientationLandscape,
            isFlagWide = isFlagWide,
            isGame = hideTitle,
            onExitFullscreen = {
                onExitFullScreen(uiState.currentFlagId)
                orientationController.unsetLandscapeOrientation()
            },
            onCarouselRotation = { id, title ->
                viewModel.updateCurrentFlagId(id, title)
            },
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun FullScreen(
    modifier: Modifier = Modifier,
    uiState: FullscreenUiState,
    orientationController: OrientationController,
    isInitOrientationLandscape: Boolean,
    isFlagWide: Boolean,
    isGame: Boolean,
    onExitFullscreen: () -> Unit,
    onCarouselRotation: (Int, Int) -> Unit,
) {
    BackHandler { onExitFullscreen() }

    /* State for controlling orientation */
    var isLandscape by rememberSaveable {
        mutableStateOf(value = isFlagWide && !isInitOrientationLandscape)
    }
    LaunchedEffect(isLandscape) {
        when (isLandscape) {
            true -> orientationController.setLandscapeOrientation()
            false -> orientationController.unsetLandscapeOrientation()
        }
    }

    /* Properties for controlling system bars */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(window, view) }

    val configuration = LocalConfiguration.current
    val isScreenPortrait by remember {
        mutableStateOf(value = isOrientationPortrait(configuration = configuration))
    }

    var counter by remember { mutableIntStateOf(value = 0) }
    val isApi30 = systemUiController.isApi30
    var isSystemBars by remember { mutableStateOf(value = isApi30) }
    var isButtons by remember { mutableStateOf(value = true) }
    var isLockPortraitInteraction by remember { mutableStateOf(value = false) }

    var isTopBarLocked by remember { mutableStateOf(value = isScreenPortrait) }

    /* Configure animation timings depending on API version due to different behaviors */
    val systemBarsExitDelay = if (isApi30) 0 else Timing.SYSTEM_BARS_HANG.toLong()
    val exitButtonAnimationTiming = if (isApi30) Timing.SYSTEM_BARS / 2 else Timing.SYSTEM_BARS

    systemUiController.setLightStatusBar(light = false) /* Makes top bar icons white */

    /* For when device SDK version is 30+ (app minimum SDK version is 24) */
    systemUiController.setSystemBarsSwipeBehaviour()

    /* Control system bars when user interaction (when not top bar lock & portrait orientation) */
    LaunchedEffect(isSystemBars) {
        if (!(isTopBarLocked && isScreenPortrait)) {
            if (isSystemBars) {
                systemUiController.setSystemBars(visible = true)
                isButtons = true

                /* Auto disable system bars and buttons after delay */
                delay(
                    timeMillis = Timing.SYSTEM_BARS.let {
                        if (counter > 0) it * 2 else it / 1.5
                    }.toLong()
                )
                counter++
                isSystemBars = false
            } else {
                counter++
                systemUiController.setSystemBars(visible = false)

                delay(timeMillis = systemBarsExitDelay)
                if (!isSystemBars) isButtons = false
            }
        }
    }

    /* Control status bars when top bar locked in portrait orientation */
    LaunchedEffect(isTopBarLocked) {
        if (isTopBarLocked && isScreenPortrait) {
            systemUiController.setSystemBars(visible = true)
            isSystemBars = false
            delay(timeMillis = Timing.SYSTEM_BARS.toLong())
            counter ++
            if (!isLockPortraitInteraction) isButtons = false
        } else if (!isTopBarLocked && isScreenPortrait) {
            systemUiController.setSystemBars(visible = false)
        }
    }

    /* Control buttons when top bar locked and screen in portrait orientation */
    LaunchedEffect(isLockPortraitInteraction) {
        if (isLockPortraitInteraction) {
            counter++
            isButtons = true
            delay(timeMillis = Timing.SYSTEM_BARS.toLong() * 2)
            isLockPortraitInteraction = false
        } else {
            if (counter != 0) isButtons = false
        }
    }

    /* To determine screen aspect ratio for image modifier so FullScreenButton can align with it */
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val isAspectRatioWide = displayMetrics.widthPixels >= displayMetrics.heightPixels


    /* Parent box as surface to receive tap gestures */
    Box(
        modifier = modifier
            .pointerInput(key1 = (Unit)) {
                detectTapGestures(
                    onTap = {
                        when (isScreenPortrait to isTopBarLocked) {
                            true to true -> isLockPortraitInteraction = !isLockPortraitInteraction
                            else -> isSystemBars = !isSystemBars
                        }
                    },
                    onDoubleTap = {
                        onExitFullscreen()
                    },
                )
            }
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onExitFullscreen()
                    true
                } else false
            },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(
                    visible = when (isTopBarLocked) {
                        true -> true
                        false -> isSystemBars
                    },
                    enter = fadeIn(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                    exit = fadeOut(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                ) {
                    FullscreenTopBar(
                        currentTitle = uiState.currentFlagTitle,
                        isActionOn = isTopBarLocked,
                        isPortraitOrientation = isScreenPortrait,
                        isGame = isGame,
                        onNavigateUp = onExitFullscreen,
                        onAction = { isTopBarLocked = !isTopBarLocked },
                    )
                }
            },
        ) { _ ->
            FullscreenContent(
                isGame = isGame,
                isAspectRatioWide = isAspectRatioWide,
                isFlagWide = isFlagWide,
                isButtons = isButtons,
                exitButtonAnimationTiming = exitButtonAnimationTiming,
                flag = uiState.initialFlag,
                flags = uiState.flags,
                isScreenPortrait = isScreenPortrait,
                isLandscape = isLandscape,
                onLandscapeChange = { isLandscape = !isLandscape },
                onExitFullScreen = onExitFullscreen,
                onCarouselRotation = onCarouselRotation,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenContent(
    modifier: Modifier = Modifier,
    isGame: Boolean,
    isScreenPortrait: Boolean,
    isAspectRatioWide: Boolean,
    isFlagWide: Boolean,
    isButtons: Boolean,
    exitButtonAnimationTiming: Int,
    flag: FlagResources,
    flags: List<FlagResources>,
    isLandscape: Boolean,
    onLandscapeChange: () -> Unit,
    onExitFullScreen: () -> Unit,
    onCarouselRotation: (Int, Int) -> Unit,
) {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val density = LocalDensity.current

    val statusBarHeight = WindowInsets.statusBars.getTop(density = density)
    val screenWidthPx = remember { displayMetrics.widthPixels.toFloat() }
    val screenWidthDp = remember { with(density) { screenWidthPx.toDp() } }

    val carouselState = rememberCarouselState(
        initialItem = flags.indexOf(flag),
        itemCount = { flags.size },
    )

    /* For controlling scroll to end/beginning when at first or last item in carousel */
    var isFirstItem by remember { mutableStateOf(value = false) }
    var isLastItem by remember { mutableStateOf(value = false) }
    var scrollToEnd: Boolean? by remember { mutableStateOf(value = null) }
    var scrollToBeginning: Boolean? by remember { mutableStateOf(value = null) }

    LaunchedEffect(scrollToEnd) {
        scrollToEnd?.let {
            carouselState.animateScrollBy(
                value = 10_000_000f,
                animationSpec = tween(durationMillis = 10_000),
            )
        }
    }
    LaunchedEffect(scrollToBeginning) {
        scrollToBeginning?.let {
            carouselState.animateScrollBy(
                value = -10_000_000f,
                animationSpec = tween(durationMillis = 10_000),
            )
        }
    }


    HorizontalUncontainedCarousel(
        state = carouselState,
        itemWidth = screenWidthDp,
        modifier = modifier
            .fillMaxSize()
            .background(surfaceDark),
        flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(
            state = carouselState,
            snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium)
        )
    ) { i ->
        val item = flags[i]

        var leftEdgeFromWindowLeft by remember { mutableFloatStateOf(value = 0f) }
        var rightEdgeFromWindowRight by remember { mutableFloatStateOf(value = 100f) }

        /* If item becomes centred on screen update external item state */
        LaunchedEffect(
            key1 = leftEdgeFromWindowLeft,
            key2 = rightEdgeFromWindowRight
        ) {
            if (abs(x = leftEdgeFromWindowLeft - rightEdgeFromWindowRight) <= 2f) {
                onCarouselRotation(item.id, item.flagOf)

                isFirstItem = when (i) {
                    0 -> true
                    else -> false
                }

                isLastItem = when (i) {
                    flags.size - 1 -> true
                    else -> false
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
            ) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = null,
                    modifier = when (isAspectRatioWide) {
                        true -> Modifier.fillMaxHeight()
                        false -> Modifier.fillMaxWidth()

                    }.onGloballyPositioned { layout ->
                        leftEdgeFromWindowLeft = layout.positionOnScreen().x
                        rightEdgeFromWindowRight =
                            screenWidthPx - (leftEdgeFromWindowLeft + layout.size.width.toFloat())

                    },
                    contentScale = when (isAspectRatioWide) {
                        true -> ContentScale.FillHeight
                        false -> ContentScale.FillWidth
                    },
                )

                if (isFlagWide || flags.size > 1) {
                    /* Toggle landscape orientation */
                    OrientationLockButton(
                        modifier = Modifier.align(Alignment.BottomStart),
                        visible = isButtons,
                        animationTiming = exitButtonAnimationTiming,
                        isLocked = isLandscape,
                        onLockChange = onLandscapeChange,
                    )
                }

                /* Toggle fullscreen mode */
                FullscreenButton(
                    visible = isButtons,
                    isFullScreenView = true,
                    animationTiming = exitButtonAnimationTiming,
                    onInvisible = {},
                    onFullScreen = onExitFullScreen,
                )
            }

            /* Scroll end of list button */
            if (isFirstItem && flags.size > 2) {
                ScrollToOppositeEndButton(
                    visible = isButtons,
                    animationTiming = exitButtonAnimationTiming,
                    isScreenPortrait = isScreenPortrait,
                    isEnd = false,
                    onClick = {
                        scrollToEnd = when (val scrollToCopy = scrollToEnd) {
                            null -> true
                            else -> !scrollToCopy
                        }
                    }
                )
            }

            /* Scroll to beginning of list button */
            if (isLastItem && flags.size > 2) {
                ScrollToOppositeEndButton(
                    visible = isButtons,
                    animationTiming = exitButtonAnimationTiming,
                    isScreenPortrait = isScreenPortrait,
                    isEnd = true,
                    onClick = {
                        scrollToBeginning = when (val scrollToCopy = scrollToBeginning) {
                            null -> true
                            else -> !scrollToCopy
                        }
                    }
                )
            }

            /* Status bar scrim */
            if (isGame) {
                Box(modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(with(density) { statusBarHeight.toDp() })
                    .background(color = surfaceDark.copy(alpha = 0.5f))
                )
            }
        }
    }
}


@Composable
private fun ScrollToOppositeEndButton(
    visible: Boolean,
    animationTiming: Int,
    isScreenPortrait: Boolean,
    isEnd: Boolean,
    onClick: () -> Unit,
) {
    /* Composable fullscreen alignment */
    val contentAlignment = when (isEnd) {
        true -> Alignment.CenterEnd
        false -> Alignment.CenterStart
    }

    /* Icon & IconButton properties */
    val icon = when (isEnd) {
        true -> Icons.Default.KeyboardDoubleArrowLeft
        false -> Icons.Default.KeyboardDoubleArrowRight
    }
    val iconButtonSize = Dimens.standardIconSize24 * 2
    val iconButtonPadding = when (isEnd) {
        true -> PaddingValues(start = Dimens.small8)
        false -> PaddingValues(end = Dimens.small8)
    }

    /* Pill island properties */
    val surfaceHeight = iconButtonSize + Dimens.small8 * 2
    val surfaceWidth = when (isScreenPortrait) {
        true -> surfaceHeight
        false -> iconButtonSize * 2 + Dimens.small8
    }
    val surfaceShape = when (isEnd) {
        true ->
            RoundedCornerShape(
                topStart = surfaceHeight / 2,
                bottomStart = surfaceHeight / 2,
            )
        false ->
            RoundedCornerShape(
                topEnd = surfaceHeight / 2,
                bottomEnd = surfaceHeight / 2,
            )
    }

    /* Arrangement of IconButton inside of island */
    val rowArrangement = when (isEnd) {
        true -> Arrangement.Start
        false -> Arrangement.End
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = contentAlignment,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = animationTiming)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = animationTiming)
            ),
        ) {
            Surface(
                modifier = Modifier
                    .height(surfaceHeight)
                    .width(surfaceWidth),
                color = Color.Black,
                shape = surfaceShape,
            ) {
                Row(
                    horizontalArrangement = rowArrangement,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .padding(paddingValues = iconButtonPadding)
                            .size(iconButtonSize),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = surfaceLight,
                        )
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.standardIconSize24 * 1.25f),
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun OrientationLockButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    animationTiming: Int,
    isLocked: Boolean,
    onLockChange: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(durationMillis = animationTiming)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = animationTiming)
        ),
    ) {
        IconButton(
            onClick = onLockChange,
            modifier = Modifier.padding(Dimens.small8),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = when (isLocked) {
                    true -> Icons.Default.ScreenLockLandscape
                    false -> Icons.Default.StayPrimaryLandscape
                },
                contentDescription = null,
                tint = surfaceLight,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenTopBar(
    modifier: Modifier = Modifier,
    @StringRes currentTitle: Int?,
    isActionOn: Boolean, /* When multi-icon state */
    isPortraitOrientation: Boolean?,
    isGame: Boolean,
    onNavigateUp: () -> Unit,
    onAction: () -> Unit,
) {
    @StringRes val title = when (isGame) {
        true -> null
        false -> currentTitle
    }

    TopAppBar(
        title = {
            title?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(it),
                        textAlign = TextAlign.Center,
                        style = when (isPortraitOrientation) {
                            true -> MaterialTheme.typography.headlineSmall
                            else -> MaterialTheme.typography.headlineLarge
                        },
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                colors = when (isGame) {
                    true -> IconButtonDefaults.iconButtonColors(
                        containerColor = surfaceDark.copy(alpha = 0.5f)
                    )
                    else -> IconButtonDefaults.iconButtonColors()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            IconButton(
                onClick = onAction,
                colors = when (isGame) {
                    true -> IconButtonDefaults.iconButtonColors(
                        containerColor = surfaceDark.copy(alpha = 0.5f)
                    )
                    false -> IconButtonDefaults.iconButtonColors()
                },
            ) {
                when(isActionOn) {
                    true ->
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                        )
                    false ->
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                        )
                }
            }
        },
        colors = when (isGame) {
            true ->
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = surfaceLight,
                    actionIconContentColor = surfaceLight,
                )
            false ->
                TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceDark.copy(alpha = 0.5f),
                    navigationIconContentColor = surfaceLight,
                    titleContentColor = surfaceLight,
                    actionIconContentColor = surfaceLight,
                )
        }
    )
}


/*
@Composable
private fun ScrollToFirstButton() {
}
 */