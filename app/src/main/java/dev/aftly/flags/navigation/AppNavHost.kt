package dev.aftly.flags.navigation

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
import dev.aftly.flags.ui.screen.game.GameScreen
import dev.aftly.flags.ui.screen.list.ListFlagsScreen
import dev.aftly.flags.ui.screen.search.SearchScreen
import dev.aftly.flags.ui.screen.settings.SettingsScreen
import dev.aftly.flags.ui.screen.startmenu.StartMenuScreen
import dev.aftly.flags.ui.theme.Timings


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    /* currentBackStackEntryAsState() triggers recomposition upon changes to navigation
     * which ensures dynamic AppTopBar updates and scrollBehaviour works after nav changes */
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val startDestination = Screen.StartMenu.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = Timings.screenNav700),
                initialOffsetX = { it },
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = Timings.screenNav700),
                targetOffsetX = { -it },
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = Timings.screenNav700),
                initialOffsetX = { -it },
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = Timings.screenNav700),
                targetOffsetX = { it },
            )
        },
    ) {

        // StartMenuScreen NavGraph
        composable(
            route = Screen.StartMenu.route
        ) {
            StartMenuScreen(
                currentScreen = Screen.StartMenu,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onTopBarAction = { screen ->
                    navController.navigate(screen.route) { launchSingleTop = true }
                },
                onNavigateDetails = { screen ->
                    navController.navigate(screen.route) { launchSingleTop = true }
                },
            )
        }

        // ListFlagsScreen NavGraph
        composable(
            route = Screen.List.route
        ) {
            ListFlagsScreen(
                currentScreen = Screen.List,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
                onNavigateDetails = { flagNavArg ->
                    navController.navigate(
                        route = "${Screen.Flag.route}/$flagNavArg"
                    ) { launchSingleTop = true }
                },
            )
        }

        // FlagScreen NavGraph
        composable(
            route = "${Screen.Flag.route}/{flag}",
            arguments = listOf(
                navArgument(name = "flag") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val flagArgument = backStackEntry.arguments?.getString("flag")

            // Handling null error navigation to FlagScreen() by popping back to current ?: home
            val onNullError: () -> Unit = {
                navController.popBackStack(
                    route = currentBackStackEntry?.destination?.route ?: startDestination,
                    inclusive = false,
                )
            } // TODO: Replace with navigation to FlagScreen with error argument/page

            FlagScreen(
                navArgFlagId = flagArgument,
                currentScreen = Screen.Flag,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onNavigateError = onNullError,
            )
        }

        // SearchScreen NavGraph
        composable(route = Screen.Search.route) {
            SearchScreen(
                currentScreen = Screen.Search,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
                onNavigateDetails = { flagNavArg ->
                    navController.navigate(
                        route = "${Screen.Flag.route}/$flagNavArg"
                    ) { launchSingleTop = true }
                },
            )
        }

        // GameScreen NavGraph
        composable(route = Screen.Game.route) {
            GameScreen(
                currentScreen = Screen.Game,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() },
            )
        }

        // SettingsScreen NavGraph
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                currentScreen = Screen.Settings,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}

/*
private fun navigateUpWithOptionalArgument(
    navController: NavHostController,
    queryParameter: String,
    argument: String?,
) {
    val previousRouteClean = navController.previousBackStackEntry?.destination?.route
        ?.substringBefore(delimiter = "?")

    if (previousRouteClean != null) {
        navController.navigateUp()
        navController.navigate(
            route = "$previousRouteClean?$queryParameter=$argument"
        ) { launchSingleTop = true }
    } else { /* function is to be used when previousBackStackEntry is not null */ }
}
 */