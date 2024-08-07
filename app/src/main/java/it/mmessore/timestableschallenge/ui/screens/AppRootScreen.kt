package it.mmessore.timestableschallenge.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.Round

enum class AppScreen(
    @StringRes val title: Int? = null,
    val showBackButton: Boolean = false
) {
    Home(R.string.app_name),
    Menu(R.string.menu),
    Round,
    Share(R.string.menu_share_new_game, true),
    Summary,
    Stats(R.string.menu_your_scores, true),
    Settings(R.string.menu_settings, true)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppRootScreen(
    challengeId: String? = null,
    roundViewModel: RoundViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf(AppScreen.Home.name) }

    if (challengeId != null) {
        startDestination = "${AppScreen.Share.name}/{challengeId}"
    }

    // Get current back stack entry
    val backStackEntryState by navController.currentBackStackEntryAsState()
    // Get the name of the current screen or default to menu
    val currentScreen = backStackEntryState?.destination?.route?.let { route ->
        val screenName = route.substringBefore('/')
        try {
            AppScreen.valueOf(screenName)
        } catch (e: IllegalArgumentException) {
            null
        }
    } ?: AppScreen.Menu

    Scaffold (
        topBar = {
            ScreenAppBar(
                currentScreen = currentScreen,
                canNavigateUp = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier.padding(top = 24.dp)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .semantics {
            testTagsAsResourceId = true
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                HomeScreen(
                    onStartButtonClick = {
                        navController.navigate(AppScreen.Menu.name) {
                            launchSingleTop = true
                        }
                })
            }

            composable(route = AppScreen.Menu.name) {
                MenuScreen(
                    onMenuButtonClick = { action ->
                        when(action) {
                            MenuAction.NEW_GAME -> {
                                roundViewModel.setRound()
                                navController.navigate(AppScreen.Round.name)
                            }
                            MenuAction.LAST_GAME -> {
                                roundViewModel.setLastRound()
                                navController.navigate(AppScreen.Round.name)
                            }
                            MenuAction.SHARE_GAME -> {
                                navController.navigate("${AppScreen.Share.name}/${null}")
                            }
                            MenuAction.YOUR_SCORES -> {
                                navController.navigate(AppScreen.Stats.name)
                            }
                            MenuAction.SETTINGS -> {
                                navController.navigate(AppScreen.Settings.name)
                            }
                        }
                    })
            }

            composable(route = AppScreen.Round.name) {
                BackHandler {
                    Toast.makeText(context, context.getString(R.string.back_not_allowed), Toast.LENGTH_SHORT).show()
                }
                RoundScreen(
                    viewModel = roundViewModel,
                    onRoundFinished = { finishedRound ->
                        val round = finishedRound?.serialize()
                        navController.navigate("${AppScreen.Summary.name}/$round") {
                            popUpTo(AppScreen.Round.name) { inclusive = true }
                        }
                    })
            }

            composable(
                route = "${AppScreen.Share.name}/{challengeId}",
                arguments = listOf(navArgument("challengeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = challengeId
                })
            ) { backStackEntry ->
                val roundId = backStackEntry.arguments?.getString("challengeId")
                ShareScreen(
                    receivedRoundId = roundId,
                    onStartRoundButtonClick = {
                        roundViewModel.setRound(roundId = it)
                        navController.navigate(AppScreen.Round.name)
                    }
                )
            }

            composable(
                route = "${AppScreen.Summary.name}/{round}",
                arguments = listOf(navArgument("round") { type = NavType.StringType })
            ) { backStackEntry ->
                val round = backStackEntry.arguments?.getString("round") ?: ""
                SummaryScreen(
                    round = Round.deserialize(round),
                    onMenuButtonClick = { navController.navigate(AppScreen.Menu.name) {
                        launchSingleTop = true
                        popUpTo("${AppScreen.Summary.name}/{round}") { inclusive = true }
                    }},
                    onStatsButtonClick = { navController.navigate(AppScreen.Stats.name) {
                        popUpTo("${AppScreen.Summary.name}/{round}") { inclusive = true }
                    }}
                )
            }

            composable(route = AppScreen.Stats.name) {
                StatsScreen(
                    onRetryRoundButtonClick = { roundId ->
                        roundViewModel.setRound(roundId = roundId)
                        navController.navigate(AppScreen.Round.name) {
                            popUpTo(AppScreen.Stats.name) { inclusive = true }
                        }
                    })
            }

            composable(route = AppScreen.Settings.name) {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAppBar(
    currentScreen: AppScreen,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    currentScreen.title?.let {
        CenterAlignedTopAppBar(
            title = { Text(
                text = stringResource(id = currentScreen.title),
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )},
            modifier = modifier,
            navigationIcon = {
                if (currentScreen.showBackButton && canNavigateUp) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            }
        )
    }
}