package dev.aftly.flags.ui.component

import android.os.Build
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ScreenLockLandscape
import androidx.compose.material.icons.filled.StayPrimaryLandscape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.theme.surfaceDark
import dev.aftly.flags.ui.theme.surfaceLight
import kotlinx.coroutines.delay


@Composable
fun FullscreenImage(
    flag: FlagResources,
    isFlagWide: Boolean,
    isLandscapeLock: Boolean,
    onLandscapeLockChange: () -> Unit,
    onExitFullScreen: () -> Unit,
) {
    BackHandler { onExitFullScreen() }
    var counter by rememberSaveable { mutableIntStateOf(value = 0) }

    val isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var isSystemBars by rememberSaveable { mutableStateOf(value = isApi30) }
    var isExitButton by rememberSaveable { mutableStateOf(value = true) }

    /* Configure animation timings depending on API version due to different behaviors */
    val systemBarsExitDelay = if (isApi30) 0 else Timings.SYSTEM_BARS_HANG.toLong()
    val exitButtonAnimationTiming = if (isApi30) Timings.SYSTEM_BARS / 2 else Timings.SYSTEM_BARS

    /* Properties for controlling system bars */
    val window = LocalActivity.current?.window
    val windowInsetsController = window?.let { WindowInsetsControllerCompat(it, it.decorView) }
    windowInsetsController?.isAppearanceLightStatusBars = false /* Makes top bar icons white */

    /* Following feature requires SDK version 30+ (app minimum SDK version is 24) */
    if (isApi30) {
        window?.insetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /* Control system bars when user interaction */
    LaunchedEffect(isSystemBars) {
        if (isSystemBars) {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            isExitButton = true

            /* Auto disable system bars and exit button after delay */
            delay(
                timeMillis = Timings.SYSTEM_BARS.let {
                    if (counter > 0) it * 2 else it / 1.5
                }.toLong()
            )
            counter++
            isSystemBars = false
        } else {
            counter++
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

            delay(timeMillis = systemBarsExitDelay)
            if (!isSystemBars) isExitButton = false
        }
    }

    /* To determine screen aspect ratio for image modifier so FullScreenButton can align with it */
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val isAspectRatioWide = displayMetrics.widthPixels >= displayMetrics.heightPixels


    /* Full screen content (Box is [default value] TopStart aligned for back button & top bar) */
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = surfaceDark)
            .combinedClickable(
                onClick = { isSystemBars = !isSystemBars },
                onDoubleClick = onExitFullScreen,
            )
    ) {
        /* Flag image */
        Box(
            modifier = Modifier.align(alignment = Alignment.Center),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Image(
                painter = painterResource(flag.image),
                contentDescription = "Fullscreen image",
                modifier = when (isAspectRatioWide) {
                    true -> Modifier.fillMaxHeight()
                    false -> Modifier.fillMaxWidth()
                },
                contentScale = when (isAspectRatioWide) {
                    true -> ContentScale.FillHeight
                    false -> ContentScale.FillWidth
                },
            )

            if (isFlagWide) {
                /* Toggle landscape orientation */
                OrientationLockButton(
                    modifier = Modifier.align(Alignment.BottomStart),
                    visible = isExitButton,
                    animationTiming = exitButtonAnimationTiming,
                    isLocked = isLandscapeLock,
                    onLockChange = onLandscapeLockChange,
                )
            }

            /* Toggle fullscreen mode */
            FullscreenButton(
                visible = isExitButton,
                isFullScreenView = true,
                animationTiming = exitButtonAnimationTiming,
                onInvisible = { },
                onFullScreen = onExitFullScreen,
            )
        }


        /* Box for semi-translucent background behind top status bar */
        AnimatedVisibility(
            visible = isExitButton,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = exitButtonAnimationTiming,
                    easing = EaseOutExpo,
                ),
                expandFrom = Alignment.Top,
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = exitButtonAnimationTiming,
                    easing = EaseOutCubic,
                ),
                shrinkTowards = Alignment.Top,
            ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .height(28.dp)
                .background(Color.Black.copy(alpha = 0.5f))
            )
        }


        /* Back button which tracks user interaction with system bars visibility */
        AnimatedVisibility(
            visible = isExitButton,
            enter = fadeIn(animationSpec = tween(durationMillis = exitButtonAnimationTiming)),
            exit = fadeOut(animationSpec = tween(durationMillis = exitButtonAnimationTiming)),
        ) {
            IconButton(
                onClick = { onExitFullScreen() },
                modifier = Modifier.padding(
                    top = Dimens.large24,
                    start = Dimens.small8
                ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                    tint = surfaceLight,
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