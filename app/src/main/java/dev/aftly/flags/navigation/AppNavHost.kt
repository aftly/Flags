package dev.aftly.flags.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import dev.aftly.flags.ui.screen.list.ListFlagsScreen
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
                currentScreen = Screen.StartMenu,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
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
                currentScreen = Screen.List,
                canNavigateBack = navController.previousBackStackEntry != null,
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
                when (navController.currentBackStackEntry?.destination?.route) {
                    Screen.List.route -> null
                    else -> ExitTransition.None
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
                currentScreen = Screen.Flag,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
                onFullscreen = { flagArg, flagsArg, isLandscape ->
                    val flagsAsString = flagsArg.joinToString(separator = ",")

                    navController.navigate(
                        route = "${Screen.Fullscreen.route}/$flagArg/$flagsAsString/$isLandscape"
                    ) { launchSingleTop = true }
                },
                onNavigateError = onNullError,
            )
        }

        /* GameScreen NavGraph */
        composable(
            route = Screen.Game.route,
            exitTransition = {
                when (navController.currentBackStackEntry?.destination?.route) {
                    Screen.StartMenu.route -> null
                    else -> ExitTransition.None
                }
            },
            popEnterTransition = { EnterTransition.None }

        ) {
            GameScreen(
                navController = navController,
                currentScreen = Screen.Game,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
                onFullscreen = { flagArg, isLandscape ->
                    val flagsArg = "$flagArg"

                    navController.navigate(
                        route = "${Screen.Fullscreen.route}/$flagArg/$flagsArg/$isLandscape"
                    ) { launchSingleTop = true }
                }
            )
        }

        /* FullScreen NavGraph */
        composable(
            route = "${Screen.Fullscreen.route}/{flag}/{flags}/{landscape}",
            arguments = listOf(
                navArgument(name = "flag") { type = NavType.IntType },
                /* Although String type, is functionally List<Int> with immediate CSV conversions */
                navArgument(name = "flags") { type = NavType.StringType },
                navArgument(name = "landscape") { type = NavType.BoolType }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val isLandscape = backStackEntry.arguments?.getBoolean("landscape") ?: false
            val isGame =
                navController.previousBackStackEntry?.destination?.route == Screen.Game.route


            FullScreen(
                currentScreen = Screen.Fullscreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                isGame = isGame,
                isFlagWide = isLandscape,
                onExitFullScreen = { flagId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("flag", flagId)
                    navController.navigateUp()
                },
            )
        }

        /* SettingsScreen NavGraph */
        /*
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                currentScreen = Screen.Settings,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
         */
    }
}