package it.mmessore.timestableschallenge.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.ui.navigation.AppDestination
import it.mmessore.timestableschallenge.ui.navigation.Navigator
import it.mmessore.timestableschallenge.ui.navigation.rememberNavigationState
import it.mmessore.timestableschallenge.ui.navigation.toEntries

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppRootScreen(
    challengeId: String? = null,
    roundViewModel: RoundViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val navigationState = rememberNavigationState(
        startRoute = AppDestination.Home,
        topLevelRoutes = setOf(AppDestination.Home)
    )
    val navigator = remember { Navigator(navigationState) }

    LaunchedEffect(challengeId) {
        if (challengeId != null) {
            navigator.navigate(AppDestination.Share(challengeId))
        }
    }

    val currentDestination = (navigationState.backStacks[navigationState.topLevelRoute]?.last() as? AppDestination) 
        ?: AppDestination.Home

    val entryProvider = remember {
        entryProvider<NavKey> {
            entry<AppDestination.Home> {
                HomeScreen(
                    onStartButtonClick = {
                        navigator.navigate(AppDestination.Menu)
                    })
            }

            entry<AppDestination.Menu> {
                MenuScreen(
                    onMenuButtonClick = { action ->
                        when (action) {
                            MenuAction.NEW_GAME -> {
                                roundViewModel.setRound()
                                navigator.navigate(AppDestination.Round)
                            }
                            MenuAction.LAST_GAME -> {
                                roundViewModel.setLastRound()
                                navigator.navigate(AppDestination.Round)
                            }
                            MenuAction.SHARE_GAME -> {
                                navigator.navigate(AppDestination.Share(null))
                            }
                            MenuAction.YOUR_SCORES -> {
                                navigator.navigate(AppDestination.Stats)
                            }
                            MenuAction.SETTINGS -> {
                                navigator.navigate(AppDestination.Settings)
                            }
                        }
                    })
            }

            entry<AppDestination.Round> {
                BackHandler {
                    Toast.makeText(context, context.getString(R.string.back_not_allowed), Toast.LENGTH_SHORT).show()
                }
                RoundScreen(
                    viewModel = roundViewModel,
                    onRoundFinished = { finishedRound ->
                        val round = finishedRound?.serialize() ?: ""
                        navigator.popAndNavigate(AppDestination.Summary(round))
                    })
            }

            entry<AppDestination.Share> { key ->
                ShareScreen(
                    receivedRoundId = key.challengeId,
                    onStartRoundButtonClick = {
                        roundViewModel.setRound(roundId = it)
                        navigator.navigate(AppDestination.Round)
                    }
                )
            }

            entry<AppDestination.Summary> { key ->
                SummaryScreen(
                    round = Round.deserialize(key.round),
                    onMenuButtonClick = {
                        navigator.navigateAndPopUpToRoot(AppDestination.Home)
                        navigator.navigate(AppDestination.Menu)
                    },
                    onStatsButtonClick = {
                        navigator.navigateAndPopUpToRoot(AppDestination.Home)
                        navigator.navigate(AppDestination.Stats)
                    }
                )
            }

            entry<AppDestination.Stats> {
                StatsScreen(
                    onRetryRoundButtonClick = { roundId ->
                        roundViewModel.setRound(roundId = roundId)
                        navigator.popAndNavigate(AppDestination.Round)
                    })
            }

            entry<AppDestination.Settings> {
                SettingsScreen()
            }
        }
    }

    Scaffold (
        topBar = {
            ScreenAppBar(
                currentScreen = currentDestination,
                canNavigateUp = (navigationState.backStacks[navigationState.topLevelRoute]?.size ?: 0) > 1 || navigationState.topLevelRoute != navigationState.startRoute,
                navigateUp = { navigator.goBack() },
                modifier = Modifier.padding(top = 24.dp)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .semantics {
            testTagsAsResourceId = true
        }

    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAppBar(
    currentScreen: AppDestination,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    currentScreen.title?.let {
        CenterAlignedTopAppBar(
            title = { Text(
                text = stringResource(id = it),
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
