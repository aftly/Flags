package dev.aftly.flags.navigation

import androidx.annotation.StringRes
import dev.aftly.flags.R


sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @StringRes val description: Int? = null,
) {
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


    enum class Menu { LIST, GAME, SETTINGS }

    fun getMenuOrNull(): Menu? = when (this) {
        List -> Menu.LIST
        Game -> Menu.GAME
        Settings -> Menu.SETTINGS
        else -> null
    }

    companion object {
        fun getScreenFromRoute(route: String?): Screen? = when (route) {
            List.route -> List
            Flag.route -> Flag
            Game.route -> Game
            GameHistory.route -> GameHistory
            Fullscreen.route -> Fullscreen
            Settings.route -> Settings
            else -> null
        }
    }
}