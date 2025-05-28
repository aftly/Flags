package dev.aftly.flags.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceLight


@Composable
fun RelatedFlagsMenu(
    modifier: Modifier = Modifier,
    buttonExpanded: Boolean,
    containerColor1: Color = MaterialTheme.colorScheme.secondary,
    containerColor2: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    cardColors: CardColors = CardDefaults.cardColors(containerColor = containerColor1),
    buttonColors1: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor1),
    buttonColors2: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor2),
    currentFlag: FlagResources,
    relatedFlags: List<FlagResources>,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(buttonExpanded) {
        if (buttonExpanded) {
            listState.scrollToItem(index = relatedFlags.indexOf(currentFlag))
        }
    }
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale


    /* Menu content */
    AnimatedVisibility(
        visible = buttonExpanded,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(durationMillis = Timing.MENU_EXPAND),
            expandFrom = Alignment.Top,
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE),
            shrinkTowards = Alignment.Top,
        ),
    ) {
        Card(
            shape = Shapes.large,
            colors = cardColors,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(
                    top = Dimens.small8,
                    bottom = Dimens.small10,
                ),
            ) {
                items(
                    count = relatedFlags.size,
                    key = { index -> relatedFlags[index].id }
                ) { index ->
                    RelatedItem(
                        modifier = Modifier.fillMaxWidth(),
                        flag = relatedFlags[index],
                        currentFlag = currentFlag,
                        buttonColors1 = buttonColors1,
                        buttonColor2 = buttonColors2,
                        onFlagSelect = onFlagSelect,
                    )
                }
            }
        }
    }
}


@Composable
private fun RelatedItem(
    modifier: Modifier = Modifier,
    flag: FlagResources,
    currentFlag: FlagResources,
    buttonColors1: ButtonColors,
    buttonColor2: ButtonColors,
    onFlagSelect: (FlagResources) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val verticalPadding = Dimens.small8
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale

    TextButton(
        onClick = { onFlagSelect(flag) },
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors = when (flag) {
            currentFlag -> buttonColor2
            else -> buttonColors1
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(flag.flagOf),
                    modifier = Modifier.padding(end = Dimens.small8)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    modifier = Modifier.height(dynamicHeight - verticalPadding * 2),
                    contentScale = ContentScale.Fit,
                )
                if (flag == currentFlag) {
                    Box(
                        modifier = Modifier.matchParentSize()
                            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.standardIconSize24 * fontScale),
                        tint = surfaceLight,
                    )
                }
            }
        }
    }
}