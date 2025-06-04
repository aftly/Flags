package dev.aftly.flags.navigation

import androidx.annotation.StringRes
import dev.aftly.flags.R


sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @StringRes val description: Int? = null,
) {
    data object StartMenu : Screen(
        route = "start_menu_screen",
        title = R.string.start_menu_title,
    )

    data object List : Screen(
        route = "list_screen",
        title = R.string.list_title,
        description = R.string.list_description,
    )

    data object Flag : Screen(
        route = "flag_screen",
    )

    data object Game : Screen(
        route = "game_screen",
        title = R.string.game_title,
        description = R.string.game_description,
    )

    data object GameHistory : Screen(
        route = "game_history_screen",
        title = R.string.game_history_title,
    )

    data object Fullscreen : Screen(
        route = "full_screen",
    )

    data object Settings : Screen(
        route = "settings_screen",
        title = R.string.settings_title,
    )
}