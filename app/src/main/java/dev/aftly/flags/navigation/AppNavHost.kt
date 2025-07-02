package dev.aftly.flags.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.aftly.flags.ui.screen.flag.FlagScreen
import dev.aftly.flags.ui.screen.fullscreen.FullScreen
import dev.aftly.flags.ui.screen.game.GameScreen
import dev.aftly.flags.ui.screen.gamehistory.GameHistoryScreen
import dev.aftly.flags.ui.screen.list.ListFlagsScreen
import dev.aftly.flags.ui.screen.settings.SettingsScreen
import dev.aftly.flags.ui.screen.startmenu.StartMenuScreen
import dev.aftly.flags.ui.theme.Timing


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    /* currentBackStackEntryAsState() triggers recomposition after navigation events which ensures
     * (dynamic) ScreenTopBars update and scrollBehaviour in screens works after navigation(s) */
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val startDestination = Screen.StartMenu.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = Timing.SCREEN_NAV),
                initialOffsetX = { it },
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = Timing.SCREEN_NAV),
                targetOffsetX = { -it },
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = Timing.SCREEN_NAV),
                initialOffsetX = { -it },
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = Timing.SCREEN_NAV),
                targetOffsetX = { it },
            )
        },
    ) {

        /* StartMenuScreen NavGraph */
        composable(
            route = Screen.StartMenu.route
        ) {
            StartMenuScreen(
                screen = Screen.StartMenu,
                onNavigateDetails = { screen ->
                    navController.navigate(screen.route) { launchSingleTop = true }
                },
            )
        }

        /* ListFlagsScreen NavGraph */
        composable(
            route = Screen.List.route
        ) {
            ListFlagsScreen(
                screen = Screen.List,
                onNavigateUp = { navController.navigateUp() },
                onNavigateDetails = { flagArg, flagsArg ->
                    val flagsAsString = flagsArg.joinToString(separator = ",")

                    navController.navigate(
                        route = "${Screen.Flag.route}/$flagArg/$flagsAsString"
                    ) { launchSingleTop = true }
                },
            )
        }

        /* FlagScreen NavGraph */
        composable(
            route = "${Screen.Flag.route}/{flag}/{flags}",
            arguments = listOf(
                navArgument(name = "flag") { type = NavType.IntType },
                /* Although String type, is functionally List<Int> with immediate CSV conversions */
                navArgument(name = "flags") { type = NavType.StringType }
            ),
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Fullscreen.route -> ExitTransition.None
                    else -> null
                }
            },
            popEnterTransition = { EnterTransition.None },

        ) {
            /* Handling null error navigation to FlagScreen() by popping back to current ?: home */
            val onNullError: () -> Unit = {
                navController.popBackStack(
                    route = currentBackStackEntry?.destination?.route ?: startDestination,
                    inclusive = false,
                )
            }

            FlagScreen(
                navController = navController,
                onNavigateUp = { navController.navigateUp() },
                onFullscreen = { flagArg, flagsArg, isLandscape ->
                    val flagsAsString = flagsArg.joinToString(separator = ",")

                    navController.navigate(
                        route = "${Screen.Fullscreen.route}/$flagArg/$flagsAsString/$isLandscape?hideTitle=false"
                    ) { launchSingleTop = true }
                },
                onNavigateError = onNullError,
            )
        }

        /* GameScreen NavGraph */
        composable(
            route = "${Screen.Game.route}?isGameOver={isGameOver}",
            arguments = listOf(
                navArgument(name = "isGameOver") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Fullscreen.route -> ExitTransition.None
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.Fullscreen.route -> EnterTransition.None
                    else -> null
                }
            }

        ) {
            GameScreen(
                navController = navController,
                screen = Screen.Game,
                onNavigateUp = { navController.navigateUp() },
                onNavigateDetails = { isGameOver ->
                    navController.navigate(route = "${Screen.GameHistory.route}/$isGameOver")
                },
                onFullscreen = { flagArg, isLandscape, hideTitle ->
                    val flagsArg = "$flagArg"

                    navController.navigate(
                        route = "${Screen.Fullscreen.route}/$flagArg/$flagsArg/$isLandscape?hideTitle=$hideTitle"
                    ) { launchSingleTop = true }
                }
            )
        }

        /* GameHistory NavGraph (for score history) */
        composable(
            route = "${Screen.GameHistory.route}/{isGameOver}",
            arguments = listOf(
                navArgument(name = "isGameOver") { type = NavType.BoolType }
            )
        ) {
            GameHistoryScreen(
                screen = Screen.GameHistory,
                onNavigateUp = { isGameOver ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        key = "isGameOver",
                        value = isGameOver,
                    )
                    navController.navigateUp()
                },
            )
        }

        /* FullScreen NavGraph */
        composable(
            route = "${Screen.Fullscreen.route}/{flag}/{flags}/{landscape}?hideTitle={hideTitle}",
            arguments = listOf(
                navArgument(name = "flag") { type = NavType.IntType },
                /* Although String type, is functionally List<Int> with immediate CSV conversions */
                navArgument(name = "flags") { type = NavType.StringType },
                navArgument(name = "landscape") { type = NavType.BoolType },
                navArgument(name = "hideTitle") { type = NavType.BoolType },
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val isLandscape = backStackEntry.arguments?.getBoolean("landscape") ?: true
            val hideTitle = backStackEntry.arguments?.getBoolean("hideTitle") ?: false

            FullScreen(
                hideTitle = hideTitle,
                isFlagWide = isLandscape,
                onExitFullScreen = { flagId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("flag", flagId)
                    navController.navigateUp()
                },
            )
        }

        /* SettingsScreen NavGraph */
        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen(
                screen = Screen.Settings,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
            )
        }
    }
}