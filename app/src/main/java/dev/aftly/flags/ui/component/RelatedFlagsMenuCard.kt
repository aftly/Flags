package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource.flagViewMap
import dev.aftly.flags.data.DataSource.inverseFlagViewMap
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.LazyColumnItem
import dev.aftly.flags.model.RelatedFlagGroup
import dev.aftly.flags.model.RelatedFlagsContent
import dev.aftly.flags.model.RelatedFlagsMenu
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Shapes
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.theme.surfaceLight


@Composable
fun RelatedFlagsMenuCard(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    menuButtonOffset: Offset,
    menuButtonWidth: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    containerColor1: Color = MaterialTheme.colorScheme.secondary,
    containerColor2: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    cardColors: CardColors = CardDefaults.cardColors(containerColor = containerColor1),
    buttonColors1: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor1),
    buttonColors2: ButtonColors = ButtonDefaults.buttonColors(containerColor = containerColor2),
    currentFlag: FlagView,
    isOnlyButton: Boolean, /* For over-scrim button, is it full size or shared with other */
    relatedFlagContent: RelatedFlagsContent,
    onFlagSelect: (FlagView) -> Unit,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val relatedFlagItems: List<LazyColumnItem> = relatedFlagContent.groups.flatMap { group ->
        buildList {
            add(
                LazyColumnItem.Header(
                    title = group.category,
                    keyTag = group.categoryKey,
                )
            )
            when (group) {
                is RelatedFlagGroup.Single -> add(
                    LazyColumnItem.Flag(
                        flag = group.flag,
                        keyTag = group.categoryKey,
                    )
                )
                is RelatedFlagGroup.Multiple -> group.flags.forEach { flag ->
                    add(
                        LazyColumnItem.Flag(
                            flag = flag,
                            keyTag = group.categoryKey,
                        )
                    )
                }
                is RelatedFlagGroup.AdminUnits -> group.flags.forEach { flag ->
                    add(
                        LazyColumnItem.Flag(
                            flag = flag,
                            keyTag = group.categoryKey,
                        )
                    )
                }
            }
        }
    }

    /* On menu expand scroll to current item or immediately preceding header */
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            val flag = when (currentFlag.previousFlagOfKey) {
                null -> currentFlag
                else -> flagViewMap.getValue(currentFlag.previousFlagOfKey)
            }

            listState.scrollToItem(
                index = relatedFlagItems.indexOfFirst { lazyColumnItem ->
                    when (lazyColumnItem) {
                        is LazyColumnItem.Flag -> lazyColumnItem.flag.id == flag.id
                        else -> false
                    }
                }.let { index ->
                    when (relatedFlagItems[index.dec()]) {
                        is LazyColumnItem.Header -> index.dec()
                        else -> index
                    }
                }
            )
        }
    }


    /* Box for containing menu, background scrim, and button replicant (to overlay the scrim) */
    Box(modifier = modifier) {
        /* Scrim to receive taps when RelatedFlagsMenu is expanded, to collapse it */
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = Timing.MENU_EXPAND)),
            exit = fadeOut(animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE)),
        ) {
            Scrim(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                onAction = onExpand,
            )
        }


        /* Duplicate related flags button to cover scrim */
        AnimatedVisibility(
            visible = isExpanded,
            enter = EnterTransition.None,
            exit = fadeOut(animationSpec = tween(durationMillis = Timing.MENU_COLLAPSE)),
        ) {
            Box(modifier = Modifier.fillMaxHeight()
                .padding(
                    top = with(density) { menuButtonOffset.y.toDp() },
                    start = with(density) { menuButtonOffset.x.toDp() }
                )
                .width(width = with(density) { menuButtonWidth.toDp() })
            ) {
                RelatedFlagsButton(
                    relatedType = relatedFlagContent.menu,
                    isFullSize = isOnlyButton,
                    menuExpanded = isExpanded,
                    onMenuExpand = onExpand,
                    onButtonPosition = {},
                    onButtonWidth = {},
                )
            }
        }


        /* Menu content */
        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier,
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
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        top = (scaffoldPadding.calculateTopPadding() - Dimens.small10)
                            .coerceAtLeast(0.dp),
                        bottom = scaffoldPadding.calculateBottomPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16,
                    ),
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
                        items = relatedFlagItems,
                        key = { it.key },
                        contentType = { it.type }
                    ) { item ->
                        when (item) {
                            is LazyColumnItem.Header -> RelatedHeader(
                                modifier = Modifier.fillMaxWidth(),
                                title = item.title,
                            )
                            is LazyColumnItem.Flag -> RelatedItem(
                                modifier = Modifier.fillMaxWidth(),
                                flag = item.flag,
                                currentFlag = currentFlag,
                                menu = relatedFlagContent.menu,
                                buttonColors1 = buttonColors1,
                                buttonColors2 = buttonColors2,
                                onFlagSelect = onFlagSelect,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RelatedHeader(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
) {
    Spacer(modifier = Modifier.height(Dimens.small8))
    HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.medium12))

    Row(
        modifier = modifier.fillMaxWidth()
            .padding(
                top = Dimens.extraSmall4,
                start = Dimens.medium12,
                end = Dimens.medium12,
            ),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}


@Composable
private fun RelatedItem(
    modifier: Modifier = Modifier,
    flag: FlagView,
    currentFlag: FlagView,
    menu: RelatedFlagsMenu,
    buttonColors1: ButtonColors,
    buttonColors2: ButtonColors,
    onFlagSelect: (FlagView) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val verticalPadding = Dimens.small8
    val dynamicHeight = Dimens.defaultListItemHeight48 * fontScale
    val isFlagSelected =
        flag == currentFlag || (menu == RelatedFlagsMenu.POLITICAL && inverseFlagViewMap
            .getValue(flag) == currentFlag.previousFlagOfKey)
    val fromToYear =
        if (flag.previousFlagOfKey != null && flag.fromYear != null && flag.toYear != null) {
            val toYear =
                if (flag.toYear == 0) stringResource(R.string.string_present)
                else "${flag.toYear}"

            stringResource(R.string.string_open_bracket) +
                    "${flag.fromYear}" +
                    stringResource(R.string.string_dash) +
                    toYear +
                    stringResource(R.string.string_close_bracket)
        } else null

    TextButton(
        onClick = { onFlagSelect(flag) },
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        colors =
            if (isFlagSelected) buttonColors2
            else buttonColors1
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        /* Separate text from image */
                        .padding(end = Dimens.small8)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(text = stringResource(flag.flagOf))

                    fromToYear?.let {
                        Text(
                            text = it,
                            modifier = Modifier.padding(start = Dimens.extraSmall4),
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                        )
                    }
                }
            }
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = flag.imagePreview),
                    contentDescription = null,
                    modifier = Modifier.height(dynamicHeight - verticalPadding * 2),
                    contentScale = ContentScale.Fit,
                )

                if (isFlagSelected) {
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