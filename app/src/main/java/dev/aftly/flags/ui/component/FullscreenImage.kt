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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import dev.aftly.flags.ui.theme.surfaceLight
import kotlinx.coroutines.delay


@Composable
fun FullscreenImage(
    flag: FlagResources,
    onExitFullScreen: () -> Unit,
) {
    BackHandler { onExitFullScreen() }

    val isApi30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var isSystemBars by rememberSaveable { mutableStateOf(value = isApi30) }
    var isExitButton by rememberSaveable { mutableStateOf(value = true) }

    val window = LocalActivity.current?.window
    val windowInsetsController = window?.let { WindowInsetsControllerCompat(it, it.decorView) }
    windowInsetsController?.isAppearanceLightStatusBars = false /* Makes top bar icons white */

    /* Configure animation timings depending on API version due to different behaviors */
    val systemBarsExitDelay = if (isApi30) 0 else Timings.SYSTEM_BARS_HANG.toLong()
    val exitButtonAnimationTiming = if (isApi30) Timings.SYSTEM_BARS / 2 else Timings.SYSTEM_BARS

    /* Following feature requires SDK version 30+ (app minimum SDK version is 24) */
    if (isApi30) {
        window?.insetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    LaunchedEffect(isSystemBars) {
        if (isSystemBars) {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            isExitButton = true

            /* Auto disable system bars and exit button after delay */
            delay(timeMillis = Timings.SYSTEM_BARS.toLong() * 2)
            isSystemBars = false
        } else {
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

            delay(timeMillis = systemBarsExitDelay)
            if (!isSystemBars) isExitButton = false
        }
    }


    /* Full screen content */
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black)
            .clickable { isSystemBars = !isSystemBars }
    ) {
        /* Flag image */
        Image(
            painter = painterResource(flag.image),
            contentDescription = "Fullscreen image",
            modifier = Modifier.fillMaxSize()
                .align(alignment = Alignment.Center),
            contentScale = ContentScale.Fit,
        )


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
                    start = Dimens.small8,
                ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                    tint = surfaceLight,
                )
            }
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
                //.height(Dimens.large24)
                .height(Dimens.large24)
                .background(Color.Black.copy(alpha = 0.3f))
            )
        }
    }
}