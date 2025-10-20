package dev.aftly.flags.ui.screen.fullscreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ScreenLockLandscape
import androidx.compose.material.icons.filled.StayPrimaryLandscape
import androidx.compose.material.icons.filled.Transform
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceDark
import dev.aftly.flags.ui.theme.surfaceLight
import dev.aftly.flags.ui.util.LocalOrientationController
import dev.aftly.flags.ui.util.OrientationController
import dev.aftly.flags.ui.util.SystemUiController
import dev.aftly.flags.ui.util.flagDatesString
import dev.aftly.flags.ui.util.isOrientationLandscape
import dev.aftly.flags.ui.util.isOrientationPortrait
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max


@Composable
fun FullScreen(
    viewModel: FullScreenViewModel = viewModel(),
    isHideTitle: Boolean,
    isFlagWide: Boolean,
    onExitFullScreen: (FlagView) -> Unit,
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
            isGame = isHideTitle,
            onExitFullscreen = {
                onExitFullScreen(uiState.flag)
                orientationController.unsetLandscapeOrientation()
            },
            onCarouselRotation = { flag ->
                viewModel.updateCurrentFlag(flag)
            },
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun FullScreen(
    modifier: Modifier = Modifier,
    uiState: FullScreenUiState,
    orientationController: OrientationController,
    isInitOrientationLandscape: Boolean,
    isFlagWide: Boolean,
    isGame: Boolean,
    onExitFullscreen: () -> Unit,
    onCarouselRotation: (FlagView) -> Unit,
) {
    BackHandler { onExitFullscreen() }

    var isCompInit by remember { mutableStateOf(value = false) }

    /* State for controlling orientation */
    var isLandscape by rememberSaveable {
        mutableStateOf(value = isFlagWide && !isInitOrientationLandscape)
    }
    LaunchedEffect(key1 = isLandscape) {
        when (isLandscape) {
            true -> orientationController.setLandscapeOrientation()
            false -> orientationController.unsetLandscapeOrientation()
        }
    }

    /* Properties for controlling system bars */
    val view = LocalView.current
    val window = (view.context as Activity).window
    val systemUiController = remember { SystemUiController(view, window) }

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

    /* For transform gestures and graphics */
    val haptics = LocalHapticFeedback.current
    var isTransformMode by rememberSaveable { mutableStateOf(value = false) }
    var imageScale by rememberSaveable { mutableFloatStateOf(1f) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(value = IntSize.Zero) }
    var imageSize by remember { mutableStateOf(value = IntSize.Zero) }

    /* To determine screen aspect ratio for image modifier so FullScreenButton can align with it */
    val displayMetrics = LocalResources.current.displayMetrics
    val isAspectRatioWide = displayMetrics.widthPixels >= displayMetrics.heightPixels

    /* Configure animation timings depending on API version due to different behaviors */
    val systemBarsExitDelay = if (isApi30) 0 else Timing.SYSTEM_BARS_HANG.toLong()
    val buttonAnimationTiming = if (isApi30) Timing.SYSTEM_BARS / 2 else Timing.SYSTEM_BARS

    /* Make top bar icons white (as fullscreen is always dark mode) */
    systemUiController.setLightStatusBar(light = false)

    /* For when device SDK version is 30+ (app minimum SDK version is 24) */
    systemUiController.setSystemBarsSwipeBehaviour()

    /* Control system bars when user interaction (when not top bar lock & portrait orientation) */
    LaunchedEffect(key1 = isSystemBars) {
        if (isTopBarLocked && isScreenPortrait && !isTransformMode) {
            systemUiController.setSystemBars(visible = true)

        } else if (isSystemBars) {
            systemUiController.setSystemBars(visible = !isTransformMode)
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

    /* Control status bars when top bar locked in portrait orientation */
    LaunchedEffect(key1 = isTopBarLocked) {
        if (isTopBarLocked && isScreenPortrait) {
            systemUiController.setSystemBars(visible = true)
            isSystemBars = false
            delay(timeMillis = Timing.SYSTEM_BARS.toLong())
            counter ++
            if (!isLockPortraitInteraction) isButtons = false
        } else if (!isTopBarLocked && isScreenPortrait) {
            systemUiController.setSystemBars(visible = false)
            isSystemBars = false
        }
    }

    /* Control buttons when top bar locked and screen in portrait orientation */
    LaunchedEffect(key1 = isLockPortraitInteraction) {
        if (isLockPortraitInteraction) {
            counter++
            isButtons = true
            delay(timeMillis = Timing.SYSTEM_BARS.toLong() * 2)
            isLockPortraitInteraction = false
        } else {
            if (counter != 0) isButtons = false
        }
    }

    LaunchedEffect(key1 = isTransformMode) {
        if (isCompInit) haptics.performHapticFeedback(HapticFeedbackType.LongPress)

        if (isTransformMode && !isGame) {
            imageScale = 1.25f
            systemUiController.setSystemBars(visible = false)
        } else {
            imageScale = 1f
            imageOffset = Offset.Zero

            if (isScreenPortrait) systemUiController.setSystemBars(visible = true)
        }
        isSystemBars = true
    }

    LaunchedEffect(key1 = Unit) { isCompInit = true }


    /* Parent box as surface to receive global tap gestures */
    Box(
        modifier = modifier
            .pointerInput(key1 = (Unit)) {
                detectTapGestures(
                    onTap = {
                        when (isScreenPortrait to isTopBarLocked) {
                            true to true ->
                                isLockPortraitInteraction = !isLockPortraitInteraction
                            else -> isSystemBars = !isSystemBars
                        }
                    },
                    onDoubleTap = {
                        onExitFullscreen()
                    },
                    onLongPress = {
                        isTransformMode = !isTransformMode
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
                    visible = (isTopBarLocked || isSystemBars) && !isTransformMode,
                    enter = fadeIn(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                    exit = fadeOut(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                ) {
                    FullscreenTopBar(
                        isActionOn = isTopBarLocked,
                        isPortraitOrientation = isScreenPortrait,
                        isGame = isGame,
                        flag = uiState.flag,
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
                buttonAnimationTiming = buttonAnimationTiming,
                flag = uiState.flag,
                flags = uiState.flags,
                isScreenPortrait = isScreenPortrait,
                isLandscape = isLandscape,
                isTransformMode = isTransformMode,
                imageScale = imageScale,
                imageOffset = imageOffset,
                onImageTransform = { pan, zoom ->
                    imageScale = (imageScale * zoom).coerceIn(minimumValue = 1f, maximumValue = 5f)

                    val offset = imageOffset + pan
                    val scaledWidth = imageSize.width * imageScale
                    val scaledHeight = imageSize.height * imageScale
                    val overflowX = (scaledWidth - boxSize.width) / 2
                    val overflowY = (scaledHeight - boxSize.height) / 2
                    val maxOffsetX = max(a = 0f, b = overflowX)
                    val maxOffsetY = max(a = 0f, b = overflowY)

                    imageOffset = Offset(
                        x = offset.x.coerceIn(-maxOffsetX, maxOffsetX),
                        y = offset.y.coerceIn(-maxOffsetY, maxOffsetY)
                    )
                },
                onBoxSize = { boxSize = it },
                onImageSize = { imageSize = it },
                onLandscapeChange = { isLandscape = !isLandscape },
                onToggleTransform = { isTransformMode = !isTransformMode },
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
    buttonAnimationTiming: Int,
    flag: FlagView,
    flags: List<FlagView>,
    isLandscape: Boolean,
    isTransformMode: Boolean,
    imageScale: Float,
    imageOffset: Offset,
    onImageTransform: (Offset, Float) -> Unit,
    onBoxSize: (IntSize) -> Unit,
    onImageSize: (IntSize) -> Unit,
    onLandscapeChange: () -> Unit,
    onToggleTransform: () -> Unit,
    onExitFullScreen: () -> Unit,
    onCarouselRotation: (FlagView) -> Unit,
) {
    val displayMetrics = LocalResources.current.displayMetrics
    val density = LocalDensity.current

    val statusBarHeight = WindowInsets.statusBars.getTop(density = density)
    val screenWidthPx = remember { displayMetrics.widthPixels.toFloat() }
    val screenWidthDp = remember { with(density) { screenWidthPx.toDp() } }

    val carouselState = rememberCarouselState(
        initialItem = flags.indexOf(flag),
        itemCount = { flags.size },
    )

    val isOrientationLockButton = isFlagWide || flags.size > 1

    /* For controlling scroll to end/beginning when at first or last item in carousel */
    var isLastItem by remember { mutableStateOf(value = false) }
    var scrollToBeginning: Boolean? by remember { mutableStateOf(value = null) }

    LaunchedEffect(scrollToBeginning) {
        scrollToBeginning?.let {
            carouselState.animateScrollBy(
                value = -10_000_000f,
                animationSpec = tween(durationMillis = 10_000),
            )
        }
    }

    if (isGame || isTransformMode) {
        Box(
            modifier = modifier.fillMaxSize()
                .background(color = surfaceDark)
                .onGloballyPositioned { onBoxSize(it.size) }
                .pointerInput(key1 = Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        onImageTransform(pan, zoom)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = flag.image),
                contentDescription = null,
                modifier =
                    if (isAspectRatioWide) {
                        Modifier.fillMaxHeight()
                    } else {
                        Modifier.fillMaxWidth()
                    }.graphicsLayer(
                        scaleX = imageScale,
                        scaleY = imageScale,
                        translationX = imageOffset.x,
                        translationY = imageOffset.y,
                    ).onGloballyPositioned {
                        onImageSize(it.size)
                    },
                contentScale =
                    if (isAspectRatioWide) ContentScale.FillHeight
                    else ContentScale.FillWidth,
            )

            if (!isGame) {
                TransformModeButton(
                    visible = isButtons,
                    sizeMultiplier = 2f,
                    animationTiming = buttonAnimationTiming,
                    onToggleTransform = onToggleTransform,
                )
            }
        }
    } else {
        HorizontalUncontainedCarousel(
            state = carouselState,
            itemWidth = screenWidthDp,
            modifier = modifier
                .fillMaxSize()
                .background(color = surfaceDark),
            flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(
                state = carouselState,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        ) { i ->
            val item = flags[i]

            /* Image margins relative to their respective window margins */
            var leftMarginPosition by remember { mutableFloatStateOf(value = 0f) }
            var rightMarginPosition by remember { mutableFloatStateOf(value = 100f) }

            isLastItem = when (i) {
                flags.size - 1 -> true
                else -> false
            }

            /* If item becomes centred on screen update external item state */
            LaunchedEffect(
                key1 = leftMarginPosition,
                key2 = rightMarginPosition
            ) {
                if (abs(x = leftMarginPosition - rightMarginPosition) <= 2f) {
                    onCarouselRotation(item)
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
                            leftMarginPosition = layout.positionOnScreen().x
                            rightMarginPosition =
                                screenWidthPx - (leftMarginPosition + layout.size.width.toFloat())

                        },
                        contentScale = when (isAspectRatioWide) {
                            true -> ContentScale.FillHeight
                            false -> ContentScale.FillWidth
                        },
                    )

                    if (isOrientationLockButton) {
                        /* Toggle landscape orientation */
                        OrientationLockButton(
                            modifier = Modifier.align(Alignment.BottomStart),
                            visible = isButtons,
                            animationTiming = buttonAnimationTiming,
                            isLocked = isLandscape,
                            onLockChange = onLandscapeChange,
                        )
                    }

                    /* Toggle transform mode */
                    TransformModeButton(
                        modifier = Modifier.align(
                            alignment =
                                if (isOrientationLockButton) Alignment.BottomCenter
                                else Alignment.BottomStart
                        ),
                        visible = isButtons,
                        animationTiming = buttonAnimationTiming,
                        onToggleTransform = onToggleTransform,
                    )

                    /* Toggle fullscreen mode */
                    FullscreenButton(
                        visible = isButtons,
                        isFullScreenView = true,
                        animationTiming = buttonAnimationTiming,
                        onInvisible = {},
                        onFullScreen = onExitFullScreen,
                    )
                }

                /* Scroll to beginning of list button */
                if (isLastItem && flags.size > 2) {
                    ScrollToBeginningButton(
                        visible = isButtons,
                        animationTiming = buttonAnimationTiming,
                        isScreenPortrait = isScreenPortrait,
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
}


@Composable
private fun ScrollToBeginningButton(
    visible: Boolean,
    animationTiming: Int,
    isScreenPortrait: Boolean,
    onClick: () -> Unit,
) {
    /* Icon & IconButton properties */
    val iconButtonSize = Dimens.standardIconSize24 * 2

    /* Pill island properties */
    val surfaceHeight = iconButtonSize + Dimens.small8 * 2
    val surfaceWidth = if (isScreenPortrait) surfaceHeight else iconButtonSize * 2 + Dimens.small8
    val surfaceShape = RoundedCornerShape(
        topStart = surfaceHeight / 2,
        bottomStart = surfaceHeight / 2,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = animationTiming)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationTiming)),
        ) {
            Surface(
                modifier = Modifier
                    .height(surfaceHeight)
                    .width(surfaceWidth),
                color = Color.Black,
                shape = surfaceShape,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .padding(start = Dimens.small8)
                            .size(iconButtonSize),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = surfaceLight,
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowLeft,
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
private fun TransformModeButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    sizeMultiplier: Float? = null,
    animationTiming: Int,
    onToggleTransform: () -> Unit,
) {
    val paddingModifier = Modifier.padding(all = Dimens.small8)
    val iconButtonModifier = if (sizeMultiplier == null) paddingModifier else {
        paddingModifier.size(size = Dimens.standardIconSize24 * sizeMultiplier * 1.5f)
    }
    val iconModifier = if (sizeMultiplier == null) Modifier else {
        Modifier.size(size = Dimens.standardIconSize24 * sizeMultiplier)
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = animationTiming)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationTiming)),
    ) {
        IconButton(
            onClick = onToggleTransform,
            modifier = iconButtonModifier,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Transform,
                contentDescription = null,
                modifier = iconModifier,
                tint = surfaceLight,
            )
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
        enter = fadeIn(animationSpec = tween(durationMillis = animationTiming)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationTiming)),
    ) {
        IconButton(
            onClick = onLockChange,
            modifier = Modifier.padding(Dimens.small8),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector =
                    if (isLocked) Icons.Default.ScreenLockLandscape
                    else Icons.Default.StayPrimaryLandscape,
                contentDescription = null,
                tint = surfaceLight,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenTopBar(
    modifier: Modifier = Modifier,
    isActionOn: Boolean, /* When multi-icon state */
    isPortraitOrientation: Boolean?,
    isGame: Boolean,
    flag: FlagView,
    onNavigateUp: () -> Unit,
    onAction: () -> Unit,
) {
    val dateSpanStyle = SpanStyle(
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize =
            if (isPortraitOrientation == true)
                MaterialTheme.typography.headlineSmall.fontSize * 0.75f
            else MaterialTheme.typography.headlineLarge.fontSize * 0.75f,
    )

    val title = if (isGame) null else buildAnnotatedString {
        append(stringResource(flag.flagOf))
        append(stringResource(R.string.string_whitespace))

        if (flag.previousFlagOfKey != null || flag.flagOfDescriptor != null) {
            withStyle(style = dateSpanStyle) {
                append(flagDatesString(flag))
            }
        }
    }

    TopAppBar(
        title = {
            title?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        style =
                            if (isPortraitOrientation == true)
                                MaterialTheme.typography.headlineSmall
                            else MaterialTheme.typography.headlineLarge,
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                colors =
                    if (isGame) IconButtonDefaults.iconButtonColors(
                        containerColor = surfaceDark.copy(alpha = 0.5f)
                    )
                    else IconButtonDefaults.iconButtonColors(),
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
                colors =
                    if (isGame) IconButtonDefaults.iconButtonColors(
                        containerColor = surfaceDark.copy(alpha = 0.5f)
                    )
                    else IconButtonDefaults.iconButtonColors(),
            ) {
                if (isActionOn) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                    )
                }
            }
        },
        colors =
            if (isGame) {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = surfaceLight,
                    actionIconContentColor = surfaceLight,
                )
            } else {
                TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceDark.copy(alpha = 0.5f),
                    navigationIconContentColor = surfaceLight,
                    titleContentColor = surfaceLight,
                    actionIconContentColor = surfaceLight,
                )
            },
    )
}