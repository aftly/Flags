package dev.aftly.flags.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.aftly.flags.ui.component.AppNavigationDrawer
import dev.aftly.flags.ui.screen.flag.FlagScreen
import dev.aftly.flags.ui.screen.fullscreen.FullScreen
import dev.aftly.flags.ui.screen.game.GameScreen
import dev.aftly.flags.ui.screen.gamehistory.GameHistoryScreen
import dev.aftly.flags.ui.screen.list.ListFlagsScreen
import dev.aftly.flags.ui.screen.settings.SettingsScreen
import dev.aftly.flags.ui.theme.Timing
import dev.aftly.flags.ui.util.getFlagIds
import dev.aftly.flags.ui.util.getFlagIdsString
import kotlinx.coroutines.launch


private val listRoute =
    "${Screen.List.route}?scrollToFlagId={scrollToFlagId}&isNavigateBack={isNavigateBack}"
private val gameRoute = "${Screen.Game.route}?isGameOver={isGameOver}"

private fun getScreen(
    backStackEntry: NavBackStackEntry?
): Screen? = when (backStackEntry?.destination?.route) {
    listRoute -> Screen.getScreenFromRoute(route = Screen.List.route)
    gameRoute -> Screen.getScreenFromRoute(route = Screen.Game.route)
    else -> Screen.getScreenFromRoute(route = backStackEntry?.destination?.route)
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    /* currentBackStackEntryAsState() triggers recomposition after navigation events which ensures
     * (dynamic) ScreenTopBars update and scrollBehaviour in screens works after navigation(s) */
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val startDestination = listRoute
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var onDrawerNavToListFromGame by remember { mutableStateOf(value = false) }
    var onDrawerNavToListFromList by remember { mutableStateOf(value = false) }
    var onDrawerNavToGameFromGame by remember { mutableStateOf(value = false) }

    AppNavigationDrawer(
        drawerState = drawerState,
        currentScreen = getScreen(currentBackStackEntry),
        isGesturesEnabled = when (getScreen(currentBackStackEntry)) {
            in listOf(Screen.List, Screen.Game, Screen.Settings) -> true
            else -> false
        },
        onClose = {
            scope.launch { drawerState.close() }
        },
        onNavigateDetails = { screen ->
            screen.getMenuOrNull()?.let { menu ->
                when (menu) {
                    Screen.Menu.LIST ->
                        if (getScreen(currentBackStackEntry) == Screen.List) {
                            onDrawerNavToListFromList = true

                        } else if (getScreen(currentBackStackEntry) == Screen.Game) {
                            onDrawerNavToListFromGame = true

                        } else {
                            /* Negate unintended LaunchedEffect()s from nav back */
                            navController.getBackStackEntry(listRoute).savedStateHandle
                                .set(key = "isNavigateBack", value = true)

                            navController.popBackStack(
                                route = listRoute,
                                inclusive = false,
                            )
                        }
                    Screen.Menu.GAME ->
                        if (getScreen(currentBackStackEntry) == Screen.Game) {
                            onDrawerNavToGameFromGame = true

                        } else if (getScreen(navController.previousBackStackEntry) == Screen.Game) {
                            navController.popBackStack(route = gameRoute, inclusive = false)

                        } else {
                            /* Pop settings if current screen to prevent screen duplication when
                             * navigating forwards to settings from game */
                            if (getScreen(currentBackStackEntry) == Screen.Settings) {
                                navController.popBackStack()
                            }
                            navController.navigate(route = screen.route) {
                                launchSingleTop = true
                            }
                        }
                    Screen.Menu.SETTINGS -> navController.navigate(route = screen.route) {
                        launchSingleTop = true
                    }
                }
            }
        },
    ) {
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
            /* ListFlagsScreen NavGraph */
            composable(
                route = listRoute,
                arguments = listOf(
                    navArgument(name = "scrollToFlagId") {
                        type = NavType.IntType
                        defaultValue = 0
                    },
                    navArgument(name = "isNavigateBack") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                ),
                exitTransition = {
                    when (targetState.destination.route) {
                        in listOf(gameRoute, Screen.Settings.route) ->
                            fadeOut(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        in listOf(gameRoute, Screen.Settings.route) ->
                            fadeIn(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                        else -> null
                    }
                },
            ) {
                ListFlagsScreen(
                    currentBackStackEntry = currentBackStackEntry,
                    onDrawerNavigateToList = onDrawerNavToListFromList,
                    onResetDrawerNavigateToList = { onDrawerNavToListFromList = false },
                    onNavigationDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onNavigateToFlagScreen = { flag, flags ->
                        val flagIdArg = flag.id
                        val flagIds = getFlagIds(flags)
                        val flagIdsArg = getFlagIdsString(flagIds)

                        navController.navigate(
                            route = "${Screen.Flag.route}/$flagIdArg/$flagIdsArg"
                        ) { launchSingleTop = true }
                    },
                )
            }

            /* FlagScreen NavGraph */
            composable(
                route = "${Screen.Flag.route}/{flagId}/{flagIds}",
                arguments = listOf(
                    navArgument(name = "flagId") { type = NavType.IntType },
                    /* Although String type, is List<Int> with CSV conversion */
                    navArgument(name = "flagIds") { type = NavType.StringType }
                ),
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.FullScreen.route -> ExitTransition.None
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
                    currentBackStackEntry = currentBackStackEntry,
                    onNavigateBack = { flagId ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "scrollToFlagId", value = flagId)

                        /* Negate unintended LaunchedEffect()s from nav back */
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "isNavigateBack", value = true)

                        navController.navigateUp()
                    },
                    onFullscreen = { flag, flagIds, isLandscape ->
                        val flagIdArg = flag.id
                        val flagIdsArg = getFlagIdsString(flagIds)

                        navController.navigate(
                            route = "${Screen.FullScreen.route}/$flagIdArg/$flagIdsArg/$isLandscape?isHideTitle=false"
                        ) { launchSingleTop = true }
                    },
                    onNavigateError = onNullError,
                )
            }

            /* GameScreen NavGraph */
            composable(
                route = gameRoute,
                arguments = listOf(
                    navArgument(name = "isGameOver") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                ),
                enterTransition = {
                    when (initialState.destination.route) {
                        in listOf(listRoute, Screen.Settings.route) ->
                            fadeIn(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.FullScreen.route -> ExitTransition.None
                        in listOf(listRoute, Screen.Settings.route) ->
                            fadeOut(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        Screen.FullScreen.route -> EnterTransition.None
                        Screen.Settings.route -> fadeIn(
                            animationSpec = tween(durationMillis = Timing.DRAWER_NAV)
                        )
                        else -> null
                    }
                },
            ) {
                GameScreen(
                    currentBackStackEntry = currentBackStackEntry,
                    screen = Screen.Game,
                    isNavigationDrawerOpen = drawerState.isOpen,
                    onDrawerNavigateToList = onDrawerNavToListFromGame,
                    onResetDrawerNavigateToList = { onDrawerNavToListFromGame = false },
                    onDrawerNavigateToGame = onDrawerNavToGameFromGame,
                    onResetDrawerNavigateToGame = { onDrawerNavToGameFromGame = false },
                    onNavigationDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onNavigateUp = {
                        /* Negate unintended LaunchedEffect()s from nav back */
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "isNavigateBack", value = true)

                        navController.navigateUp()
                        onDrawerNavToListFromGame = false
                    },
                    onScoreHistory = { isGameOver ->
                        navController.navigate(route = "${Screen.GameHistory.route}/$isGameOver")
                    },
                    onFullscreen = { flag, isLandscape, isHideTitle ->
                        val flagIdArg = flag.id
                        val flagIdsArg = getFlagIdsString(flagIds = listOf(flagIdArg))

                        navController.navigate(
                            route = "${Screen.FullScreen.route}/$flagIdArg/$flagIdsArg/$isLandscape?isHideTitle=$isHideTitle"
                        ) { launchSingleTop = true }
                    }
                )
            }

            /* GameHistory NavGraph (for score history) */
            composable(
                route = "${Screen.GameHistory.route}/{isFromGameOver}",
                arguments = listOf(
                    navArgument(name = "isFromGameOver") { type = NavType.BoolType }
                ),
            ) {
                GameHistoryScreen(
                    screen = Screen.GameHistory,
                    onNavigateUp = { isGameOver ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "isGameOver", value = isGameOver)

                        navController.navigateUp()
                    },
                )
            }

            /* FullScreen NavGraph */
            composable(
                route = "${Screen.FullScreen.route}/{flagId}/{flagIds}/{isLandscape}?isHideTitle={isHideTitle}",
                arguments = listOf(
                    navArgument(name = "flagId") { type = NavType.IntType },
                    /* Although String type, is functionally List<Int> with immediate CSV conversions */
                    navArgument(name = "flagIds") { type = NavType.StringType },
                    navArgument(name = "isLandscape") { type = NavType.BoolType },
                    navArgument(name = "isHideTitle") { type = NavType.BoolType },
                ),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) { backStackEntry ->
                val isLandscape = backStackEntry.arguments?.getBoolean("isLandscape") ?: true
                val isHideTitle = backStackEntry.arguments?.getBoolean("isHideTitle") ?: false

                FullScreen(
                    isHideTitle = isHideTitle,
                    isFlagWide = isLandscape,
                    onExitFullScreen = { flag ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "flagId", value = flag.id)
                        navController.navigateUp()
                    },
                )
            }

            /* SettingsScreen NavGraph */
            composable(
                route = Screen.Settings.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(durationMillis = Timing.DRAWER_NAV))
                },
            ) {
                SettingsScreen(
                    screen = Screen.Settings,
                    onNavigationDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onNavigateUp = {
                        /* Negate unintended LaunchedEffect()s from nav back */
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set(key = "isNavigateBack", value = true)

                        navController.navigateUp()
                    }
                )
            }
        }
    }
}