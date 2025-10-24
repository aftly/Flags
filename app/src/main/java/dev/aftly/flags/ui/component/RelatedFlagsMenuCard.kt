package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import dev.aftly.flags.model.FlagView
import dev.aftly.flags.model.menu.FlagsMenu
import dev.aftly.flags.model.menu.relatedmenu.LazyColumnItem
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagsCategory
import dev.aftly.flags.model.menu.relatedmenu.RelatedFlagsContent
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.getLatestFlag
import dev.aftly.flags.ui.util.getRelatedFlagItems
import kotlinx.coroutines.delay


@Composable
fun RelatedFlagsMenuCard(
    modifier: Modifier = Modifier,
    relatedFlagsContent: RelatedFlagsContent,
    scaffoldPadding: PaddingValues,
    menuButtonOffset: Offset,
    menuButtonWidth: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    currentFlag: FlagView,
    isOnlyButton: Boolean, /* For over-scrim button, is it full size or shared with other */
    onFlagSelect: (FlagView) -> Unit,
) {
    val listState = rememberLazyListState()
    var isExpandedDelay by remember { mutableStateOf(value = false) }
    var itemHeight by remember { mutableIntStateOf(value = 0) }
    var cardHeight by remember { mutableIntStateOf(value = 0) }
    val bottomItemOffset = cardHeight - (itemHeight * 5 / 4)

    val menu = relatedFlagsContent.menu
    val relatedFlagItems = getRelatedFlagItems(relatedFlagsContent)

    val currentMenuFlag =
        if (menu == FlagsMenu.POLITICAL) getLatestFlag(currentFlag) else currentFlag

    val showDatesHeaderTitles = listOf(
        RelatedFlagsCategory.HISTORICAL_FLAGS.title,
        RelatedFlagsCategory.PREVIOUS_ENTITIES_POLITY.title,
        RelatedFlagsCategory.PREVIOUS_ENTITIES_NON_POLITY.title,
        RelatedFlagsCategory.HISTORICAL_UNITS.title,
        RelatedFlagsCategory.HISTORICAL_UNIT_SELECTED.title,
        RelatedFlagsCategory.PREVIOUS_ENTITIES_OF_SOVEREIGN.title
    )

    val firstItemModifier = Modifier.fillMaxWidth()
        .onSizeChanged { itemHeight = it.height }

    /* On menu expand scroll to current to beginning */
    LaunchedEffect(key1 = isExpanded) {
        if (isExpanded) {
            listState.scrollToItem(index = 0)
            delay(timeMillis = Timing.MENU_EXPAND.toLong() * 2)
            isExpandedDelay = true
        } else {
            isExpandedDelay = false
        }
    }
    /* After delay animate scroll to current item offset to bottom of card
     * (in a separate LaunchedEffect because bottomItemOffset is calculated late and
     * LaunchedEffect takes a snapshot of state too early) */
    LaunchedEffect(key1 = isExpandedDelay) {
        if (isExpandedDelay) {
            listState.animateScrollToItem(
                index = relatedFlagItems.indexOfFirst { lazyColumnItem ->
                    when (lazyColumnItem) {
                        is LazyColumnItem.Flag -> lazyColumnItem.flag.id == currentMenuFlag.id
                        else -> false
                    }
                },
                scrollOffset = -bottomItemOffset
            )
        }
    }


    /* Box for containing menu, background scrim, and button replicant (to overlay the scrim) */
    Box(modifier = modifier) {
        /* Scrim to receive taps when RelatedFlagsMenu is expanded, to collapse it */
        MenuScrim(visible = isExpanded, onClick = onExpand)

        /* Duplicate RelatedFlagsMenu button as scrim covers top bar (where main button is) */
        DuplicateRelatedFlagsButton(
            visible = isExpanded,
            menu = menu,
            isOnlyButton = isOnlyButton,
            menuButtonOffset = menuButtonOffset,
            menuButtonWidth = menuButtonWidth,
            onMenuExpand = onExpand,
        )

        /* MENU CARD CONTENT */
        MenuAnimatedVisibility(visible = isExpanded) {
            MenuCard(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        top = (scaffoldPadding.calculateTopPadding() - Dimens.small10)
                            .coerceAtLeast(minimumValue = 0.dp),
                        bottom = scaffoldPadding.calculateBottomPadding(),
                        start = Dimens.marginHorizontal16,
                        end = Dimens.marginHorizontal16,
                    )
                    .onSizeChanged { cardHeight = it.height },
                menu = menu,
            ) {
                RelatedFlagsLazyList(
                    modifier = Modifier.fillMaxWidth(),
                    listState = listState,
                    contentPadding = PaddingValues(top = Dimens.small8, bottom = Dimens.small10),
                    menu = menu,
                    relatedFlagItems = relatedFlagItems,
                    firstItemModifier = firstItemModifier,
                    selectedFlag = currentFlag,
                    showDatesHeaderTitles = showDatesHeaderTitles,
                    onFlagSelect = onFlagSelect,
                )
            }
        }
    }
}

@Composable
private fun DuplicateRelatedFlagsButton(
    visible: Boolean,
    menu: FlagsMenu,
    isOnlyButton: Boolean,
    menuButtonOffset: Offset,
    menuButtonWidth: Int,
    onMenuExpand: () -> Unit,
) {
    val density = LocalDensity.current

    MenuScrimAnimatedVisibility(visible = visible) {
        Box(modifier = Modifier.fillMaxHeight()
            .padding(
                top = with(receiver = density) { menuButtonOffset.y.toDp() },
                start = with(receiver = density) { menuButtonOffset.x.toDp() }
            )
            .width(width = with(receiver = density) { menuButtonWidth.toDp() })
        ) {
            RelatedFlagsButton(
                menu = menu,
                isFullSize = isOnlyButton,
                menuExpanded = visible,
                onMenuExpand = onMenuExpand,
            )
        }
    }
}

@Composable
private fun RelatedFlagsLazyList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    contentPadding: PaddingValues,
    menu: FlagsMenu,
    relatedFlagItems: List<LazyColumnItem>,
    firstItemModifier: Modifier,
    selectedFlag: FlagView,
    showDatesHeaderTitles: List<Int>,
    onFlagSelect: (FlagView) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        items(
            items = relatedFlagItems,
            key = { it.key },
            contentType = { it.type }
        ) { item ->
            val index = relatedFlagItems.indexOf(item)
            val firstFlag = relatedFlagItems.first { it is LazyColumnItem.Flag }
            val firstFlagIndex = relatedFlagItems.indexOf(firstFlag)

            val itemModifier = Modifier.fillMaxWidth()
            val flagModifier = if (index == firstFlagIndex) firstItemModifier else itemModifier

            val lastHeader = relatedFlagItems.subList(0, index)
                .lastOrNull { it is LazyColumnItem.Header } as? LazyColumnItem.Header

            when (item) {
                is LazyColumnItem.Header -> RelatedHeader(
                    modifier = itemModifier,
                    title = item.title,
                )
                is LazyColumnItem.Flag -> MenuFlagItem(
                    modifier = flagModifier,
                    flag = item.flag,
                    selectedFlag = selectedFlag,
                    menu = menu,
                    isShowDates = lastHeader?.title in showDatesHeaderTitles,
                    onFlagSelect = onFlagSelect,
                )
            }
        }
    }
}

@Composable
private fun RelatedHeader(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
) {
    Spacer(modifier = Modifier.height(height = Dimens.small8))
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
            text = stringResource(id = title),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}