package dev.aftly.flags.ui.component

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun FullScreenImage(
    flag: FlagResources,
    onExitFullScreen: () -> Unit,
) {
    var isSystemBars by rememberSaveable { mutableStateOf(value = false) }
    var isExitButton by rememberSaveable { mutableStateOf(value = true) }

    val activity = LocalActivity.current
    val windowInsetsController = if (activity != null) {
        WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    } else null

    val coroutineScope = rememberCoroutineScope()

    BackHandler { onExitFullScreen() }

    LaunchedEffect(isSystemBars) {
        if (isSystemBars) {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            isExitButton = true
        } else {
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
            coroutineScope.launch {
                delay(timeMillis = Timings.SYSTEM_BARS_HANG.toLong())
                if (!isSystemBars) isExitButton = false
            }
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
            modifier = Modifier.fillMaxSize()
                .clickable { isSystemBars = !isSystemBars },
            color = Color.Transparent,
        ) { }


        /* Back button which tracks user interaction with system bars visibility */
        AnimatedVisibility(
            visible = isExitButton,
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