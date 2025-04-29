package dev.aftly.flags.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun FullScreenImage(
    flag: FlagResources,
    onShowSystemBars: () -> Unit,
    onHideSystemBars: () -> Unit,
    onExitFullScreen: () -> Unit,
) {
    var isInitialised by rememberSaveable { mutableStateOf(value = true) }
    var isSystemBars by rememberSaveable { mutableStateOf(value = false) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler { onExitFullScreen() }

    LaunchedEffect(isInitialised) {
        coroutineScope.launch {
            delay(timeMillis = Timings.SYSTEM_BARS.toLong() / 2)
            isInitialised = false
        }
    }


    /* Full screen content */
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black)
    ) {
        /* Flag image */
        Image(
            painter = painterResource(flag.image),
            contentDescription = "Fullscreen image",
            modifier = Modifier.fillMaxSize()
                .align(alignment = Alignment.Center),
            contentScale = ContentScale.Fit,
        )


        /* Surface for tapping to show/hide system bars */
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (!isSystemBars) onShowSystemBars() else onHideSystemBars()

                    if (isSystemBars) {
                        coroutineScope.launch {
                            delay(timeMillis = Timings.SYSTEM_BARS_HANG.toLong())
                            if (isSystemBars) isSystemBars = false
                        }
                    } else {
                        isSystemBars = true
                    }
                },
            color = Color.Transparent,
        ) { }


        /* Initial back button whose visibility tracks initial system bars visibility */
        AnimatedVisibility(
            visible = isInitialised,
            enter = fadeIn(animationSpec = tween(durationMillis = 0)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timings.SYSTEM_BARS)),
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
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }


        /* Back button which tracks user interaction with system bars visibility */
        AnimatedVisibility(
            visible = isSystemBars,
            enter = fadeIn(animationSpec = tween(durationMillis = Timings.SYSTEM_BARS)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timings.SYSTEM_BARS)),
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
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}