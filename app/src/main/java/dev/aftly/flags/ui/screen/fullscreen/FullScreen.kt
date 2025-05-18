package dev.aftly.flags.ui.screen.fullscreen

import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenLockLandscape
import androidx.compose.material.icons.filled.StayPrimaryLandscape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.FullscreenButton
import dev.aftly.flags.ui.component.LocalOrientationController
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceDark
import dev.aftly.flags.ui.theme.surfaceLight
import kotlinx.coroutines.delay
import kotlin.math.abs


@Composable
fun FullScreen(
    viewModel: FullscreenViewModel = viewModel(),
    currentScreen: Screen,
    canNavigateBack: Boolean,
    isFlagWide: Boolean,
    onExitFullScreen: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val orientationController = LocalOrientationController.current
    var isLandscape by rememberSaveable { mutableStateOf(value = isFlagWide) }

    /* Initialise with correct orientation & ensure composition is delayed until end of coroutine */
    var isInit by rememberSaveable { mutableStateOf(value = false) }
    LaunchedEffect(Unit) {
        if (isFlagWide) orientationController.setLandscapeOrientation()
        isInit = true
    }

    LaunchedEffect(isLandscape) {
        when (isLandscape) {
            true -> orientationController.setLandscapeOrientation()
            false -> orientationController.unsetLandscapeOrientation()
        }
    }

    if (isInit) {
        FullscreenScaffold(
            currentScreen = currentScreen,
            currentTitle = uiState.currentFlagTitle,
            canNavigateBack = canNavigateBack,
            isFlagWide = isFlagWide,
            isLandscape = isLandscape,
            onLandscapeChange = { isLandscape = !isLandscape },
            flag = uiState.initialFlag,
            flags = uiState.flags,
            onExitFullscreen = {
                orientationController.unsetLandscapeOrientation()
                onExitFullScreen(uiState.currentFlagId)
            },
            onCarouselRotation = { id, title ->
                viewModel.updateCurrentFlagId(id, title)
            },
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun FullscreenScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    currentTitle: Int,
    canNavigateBack: Boolean,
    isFlagWide: Boolean,
    isLandscape: Boolean,
    onLandscapeChange: () -> Unit,
    flag: FlagResources,
    flags: List<FlagResources>,
    onExitFullscreen: () -> Unit,
    onCarouselRotation: (Int, Int) -> Unit,
) {
    BackHandler { onExitFullscreen() }

    var counter by rememberSaveable { mutableIntStateOf(value = 0) }

    val isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var isSystemBars by rememberSaveable { mutableStateOf(value = isApi30) }
    var isButtons by rememberSaveable { mutableStateOf(value = true) }
    var isTopBarLocked by rememberSaveable { mutableStateOf(value = !isFlagWide) }

    /* Configure animation timings depending on API version due to different behaviors */
    val systemBarsExitDelay = if (isApi30) 0 else Timing.SYSTEM_BARS_HANG.toLong()
    val exitButtonAnimationTiming = if (isApi30) Timing.SYSTEM_BARS / 2 else Timing.SYSTEM_BARS

    /* Properties for controlling system bars */
    val activity = LocalActivity.current
    val windowInsetsController = activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView) }
    windowInsetsController?.isAppearanceLightStatusBars = false /* Makes top bar icons white */

    /* Following feature requires SDK version 30+ (app minimum SDK version is 24) */
    if (isApi30) {
        activity?.window?.insetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    /* Control system bars when user interaction */
    LaunchedEffect(isSystemBars) {
        if (isSystemBars) {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            isButtons = true

            /* Auto disable system bars and exit button after delay */
            delay(
                timeMillis = Timing.SYSTEM_BARS.let {
                    if (counter > 0) it * 2 else it / 1.5
                }.toLong()
            )
            counter++
            isSystemBars = false
        } else {
            counter++
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

            delay(timeMillis = systemBarsExitDelay)
            if (!isSystemBars) isButtons = false
        }
    }

    /* To determine screen aspect ratio for image modifier so FullScreenButton can align with it */
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val isAspectRatioWide = displayMetrics.widthPixels >= displayMetrics.heightPixels



    /* Full screen content (Box is [default value] TopStart aligned for back button & top bar) */
    Box(
        modifier = Modifier
            .pointerInput(key1 = (Unit)) {
                detectTapGestures(
                    onTap = { isSystemBars = !isSystemBars },
                    onDoubleTap = { onExitFullscreen() },
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
            modifier = modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(
                    visible = when (isTopBarLocked) {
                        true -> true
                        false -> isSystemBars
                    },
                    enter = fadeIn(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                    exit = fadeOut(animationSpec = tween(durationMillis = Timing.SYSTEM_BARS)),
                ) {
                    StaticTopAppBar(
                        currentScreen = currentScreen,
                        currentTitle = currentTitle,
                        canNavigateBack = canNavigateBack,
                        isActionOn = isTopBarLocked,
                        onNavigateUp = onExitFullscreen,
                        onNavigateDetails = {},
                        onAction = { isTopBarLocked = !isTopBarLocked },
                    )
                }
            },
        ) { _ ->
            FullscreenContent(
                isAspectRatioWide = isAspectRatioWide,
                isFlagWide = isFlagWide,
                isButtons = isButtons,
                exitButtonAnimationTiming = exitButtonAnimationTiming,
                flag = flag,
                flags = flags,
                isLandscape = isLandscape,
                onLandscapeChange = onLandscapeChange,
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

    val screenWidthPx = remember { displayMetrics.widthPixels.toFloat() }
    val screenWidthDp = remember { with(density) { screenWidthPx.toDp() } }

    val carouselState = rememberCarouselState(
        initialItem = flags.indexOf(flag),
        itemCount = { flags.count() },
    )

    var isLastItem by remember { mutableStateOf(value = false) }

    var scrollToBeginning by remember { mutableStateOf(value = false) }
    LaunchedEffect(scrollToBeginning) {
        if (scrollToBeginning) {
            carouselState.animateScrollBy(value = -10_000_000f)
        }
    }


    HorizontalUncontainedCarousel(
        state = carouselState,
        itemWidth = screenWidthDp,
        modifier = modifier.fillMaxSize()
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

                if (i == flags.size - 1) {
                    isLastItem = true
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


/*
@Composable
private fun ScrollToFirstButton() {
}
 */