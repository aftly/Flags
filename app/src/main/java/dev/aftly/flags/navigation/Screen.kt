package dev.aftly.flags.navigation

import androidx.annotation.StringRes
import dev.aftly.flags.R


sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @StringRes val title_long: Int? = null,
    @StringRes val description: Int? = null
) {
    data object StartMenu : Screen(
        route = "start_menu_screen",
    )

    data object List : Screen(
        route = "list_screen",
        title = R.string.flag_list_title,
        title_long = R.string.flag_list_title,
        description = R.string.flag_list_description,
    )

    data object Flag : Screen(
        route = "flag_screen",
    )

    data object Search : Screen(
        route = "search_screen",
        title = R.string.flag_search_title,
        title_long = R.string.flag_search_title_long,
        description = R.string.flag_search_description,
    )

    data object Game : Screen(
        route = "game_screen",
        title = R.string.flag_game_title_short,
        title_long = R.string.flag_game_title,
        description = R.string.flag_game_description,
    )

    /*
    data object Settings : Screen(
        route = "settings_screen",
        title = R.string.settings_title,
    )
     */
}